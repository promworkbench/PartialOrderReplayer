package org.processmining.partialorder.models.projection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.processmining.extension.XDataExtension;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.partialorder.dal.models.EnumDataAccessType;
import org.processmining.partialorder.models.palignment.Move;
import org.processmining.partialorder.models.palignment.PAlignment;
import org.processmining.partialorder.models.replay.POAlignmentDataProvider;
import org.processmining.partialorder.models.replay.POSyncReplayResult;
import org.processmining.partialorder.plugins.replay.POAlignmentBuilder;
import org.processmining.partialorder.plugins.vis.projection.PAlignmentsOnModelVisPanel;
import org.processmining.partialorder.plugins.vis.projection.PProjectionConfigPanel;
import org.processmining.partialorder.ptrace.model.PLog;
import org.processmining.partialorder.util.LogUtil;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

/**
 * This data provider keeps all information that is necessary to visualize
 * {@link PAlignmentsOnModelVisPanel}
 * 
 * @author xlu
 * 
 */
public class POAlignmentOnModelDataProvider extends POAlignmentDataProvider {

	//	PartiallyOrderedAlignments poalignments;

	/**
	 * A list of {@link LogMovePattern} that appeared in this.logReplayResult
	 */
	protected List<LogMovePattern> logMovePatterns;

	/**
	 * A map : data attribute * transition -> the input data pattern (which
	 * obtained all information about this transition that read this data
	 * attribute)
	 */
	protected Map<String, Map<Transition, DataPattern>> inputDataPatterns;
	/**
	 * A map : data attribute * transition -> the output data pattern (which
	 * obtained all information about this transition that written this data
	 * attribute
	 */
	protected Map<String, Map<Transition, DataPattern>> outputDataPatterns;

	/**
	 * A map: a log move class -> a boolean indicating whether the log move is
	 * selected in the {@link PProjectionConfigPanel}
	 */
	protected Map<String, Boolean> selectedLogMoveClasses;

	/**
	 * A map : the set of accessed data attributes -> boolean indicating whether
	 * the data attribute is selected in the {@link PProjectionConfigPanel}
	 */
	protected Map<String, Boolean> selectedDataElem;
	/**
	 * A map from a log move transition in the projected model to log move pattern
	 * to provide data for the log move transition
	 */
	protected Map<LogMoveTransition, LogMovePattern> mapLMTransToPattern;


	public POAlignmentOnModelDataProvider(PLog poLog, XLog log, PNRepResult logReplayResult,
			PetrinetGraph graph, TransEvClassMapping mapping, CostBasedCompleteParam param) {
		super(poLog, log, logReplayResult, graph, mapping, param);

		logMovePatterns = new ArrayList<LogMovePattern>();
		selectedLogMoveClasses = new HashMap<String, Boolean>();
		mapLMTransToPattern = new HashMap<LogMoveTransition, LogMovePattern>();

		/*
		 * init all data elements
		 */

		inputDataPatterns = new HashMap<String, Map<Transition, DataPattern>>();
		outputDataPatterns = new HashMap<String, Map<Transition, DataPattern>>();

		List<Set<String>> dataElems = LogUtil.getAllEventIOAttributeKeys(log);
		for (String elem : dataElems.get(LogUtil.INPUT_INDEX)) {
			inputDataPatterns.put(elem, new HashMap<Transition, DataPattern>());
		}

		for (String elem : dataElems.get(LogUtil.OUTPUT_INDEX)) {
			outputDataPatterns.put(elem, new HashMap<Transition, DataPattern>());
		}
		selectedDataElem = new HashMap<String, Boolean>();
		for (String elem : dataElems.get(LogUtil.INOUTPUT_INDEX)) {
			selectedDataElem.put(elem, true);
		}
		
		
		int i = 0;
		for (SyncReplayResult srr : this.logReplayResult) {
			System.out.println("Computed alignment " + i + "/" + this.logReplayResult.size());
			i++;
			if (srr instanceof POSyncReplayResult) {
				PAlignment poalignment = ((POSyncReplayResult) srr).getPOAlignmentGraph();
				if(poalignment == null){
					int traceIndex = srr.getTraceIndex().first();
					poalignment = POAlignmentBuilder.computePAlignmentAndUpdateResult(
							getGraph(), 
							getInitialMarking(), getFinalMarkings(), getEventClasses(), getMapping(),
							this.log.get(traceIndex), poLog.get(traceIndex), traceIndex, ((POSyncReplayResult) srr));
				}
				for (Move node : poalignment.getMoves()) {
					
					// FIXME: statistic now only poalignment
					// Fix: for traces of this po alignment

					if (node.isLogMove() || node.isSyncMove()) {
						/*
						 *  Compute Log Move Patterns and Information
						 */
						if (node.isLogMove()) {
//							POLogMoveNode logmove = (POLogMoveNode) node;
							initDataLogMove(poalignment, node);

						}

						/*
						 *  Compute Sync Moves - Data and Data Access Information
						 */
						if (node.isSyncMove()) {
							
							initDataSyncMove(poalignment, node);
						}
					}
				}

			}
		}
	}

	private void initDataSyncMove(PAlignment poalignment, Move node) {
		XEvent e = node.getEvent();
		Transition t = node.getTransition();
		XAttributeMap inputmap = XDataExtension.instance().extractInputAttributes(e);
		if (inputmap != null) {
			for (XAttribute input : inputmap.values()) {
				String key = input.getKey();
				DataPattern pattern = null;
				if (!inputDataPatterns.get(key).containsKey(t)) {
					pattern = new DataPattern(key, t, EnumDataAccessType.I);
					inputDataPatterns.get(key).put(t, pattern);
				} else {
					pattern = inputDataPatterns.get(key).get(t);
				}
				pattern.addAttribute(poalignment.getTraceIndex(), node.getEventIndex(),
						input);

			}
		}

		XAttributeMap outputmap = XDataExtension.instance().extractOutputAttributes(e);
		if (outputmap != null) {
			for (XAttribute output : outputmap.values()) {
				String key = output.getKey();
				DataPattern pattern = null;
				if (!outputDataPatterns.get(key).containsKey(t)) {
					pattern = new DataPattern(key, t, EnumDataAccessType.O);
					outputDataPatterns.get(key).put(t, pattern);
				} else {
					pattern = outputDataPatterns.get(key).get(t);
				}
				pattern.addAttribute(poalignment.getTraceIndex(), node.getEventIndex(),
						output);

			}
		}
		
	}

	private void initDataLogMove(PAlignment poalignment, Move node) {
		/*
		 * Get preset and postset that are in the model of the log move
		 */
		int traceIndex = poalignment.getTraceIndex();
		Set<Transition> preset = getPreSetTransitions(poalignment, node);
		Set<Transition> postset = getPostSetTransitions(poalignment, node);
		XEventClass eclass = this.getEventClasses().getClassOf(node.getEvent());
		/*
		 * Create a LogMovePattern
		 */
		LogMovePattern lmp = new LogMovePattern(eclass, preset, postset);
		/*
		 * Test if the LogMovePattern already existed
		 */
		int index = logMovePatterns.indexOf(lmp);
		if (index < 0) {
			/* if does not exist, then add lmp to this.logMovePatterns and update index */
			logMovePatterns.add(lmp);
			index = logMovePatterns.size() - 1;
		}
		lmp = logMovePatterns.get(index);

		/*
		 * Add the trace that has this log move pattern to (existing) lmp
		 */
		lmp.addTraceAndEvent(traceIndex, node.getEventIndex());

		selectedLogMoveClasses.put(eclass.toString(), true);

		/*
		 *  update the input data patterns of the log move
		 */
		XEvent e = node.getEvent();
		XAttributeMap inputmap = XDataExtension.instance().extractInputAttributes(e);
		if (inputmap != null) {
			for (XAttribute input : inputmap.values()) {
				String key = input.getKey();
				DataPattern pattern = new DataPattern(key, lmp, EnumDataAccessType.I);
				int indexDP = lmp.getDataPatterns().indexOf(pattern);
				if (indexDP < 0) {
					lmp.addDataPattern(pattern);
					indexDP = lmp.getDataPatterns().size() - 1;
				} else {
					pattern = lmp.getDataPatterns().get(indexDP);
				}
				pattern.addAttribute(traceIndex, node.getEventIndex(), input);

			}
		}
		/*
		 *  update the output data patterns of the log move
		 */
		XAttributeMap outputmap = XDataExtension.instance().extractOutputAttributes(e);
		if (outputmap != null) {
			for (XAttribute output : outputmap.values()) {
				String key = output.getKey();
				DataPattern pattern = new DataPattern(key, lmp, EnumDataAccessType.O);
				int indexDP = lmp.getDataPatterns().indexOf(pattern);
				if (indexDP < 0) {
					lmp.addDataPattern(pattern);
					indexDP = lmp.getDataPatterns().size() - 1;
				} else {
					pattern = lmp.getDataPatterns().get(indexDP);
				}
				pattern.addAttribute(traceIndex, node.getEventIndex(), output);

			}
		}

	}

	public List<LogMovePattern> getLogMovePatterns() {
		return logMovePatterns;
	}

	

	public boolean isLogMoveEClassSelected(String label) {
		if (selectedLogMoveClasses.containsKey(label)) {
			return selectedLogMoveClasses.get(label);
		}
		return false;
	}

	public Set<String> getLogMoveEClasses() {
		return selectedLogMoveClasses.keySet();
	}

	public void setLogMoveEClassSelected(String lm, boolean b) {
		if (selectedLogMoveClasses.containsKey(lm)) {
			selectedLogMoveClasses.put(lm, b);
		}
	}

	public Set<String> getDataElements() {
		return this.selectedDataElem.keySet();
	}

	public void setDataElementSelected(String dataElem, boolean b) {
		if (selectedDataElem.containsKey(dataElem)) {
			selectedDataElem.put(dataElem, b);
		}
	}

	public boolean isDataElemSelected(String label) {
		if (selectedDataElem.containsKey(label)) {
			return selectedDataElem.get(label);
		}
		return false;
	}


	public void putLogMoveTransToPattern(LogMoveTransition t, LogMovePattern lm) {
		mapLMTransToPattern.put(t, lm);
	}

	public LogMovePattern getPattern(LogMoveTransition t) {
		if (mapLMTransToPattern.containsKey(t)) {
			return mapLMTransToPattern.get(t);
		}
		return null;
	}

	public Map<Transition, DataPattern> getOutputTransOfData(String datakey) {
		if (outputDataPatterns.containsKey(datakey)) {
			return outputDataPatterns.get(datakey);
		}
		return new HashMap<Transition, DataPattern>();
	}

	public Map<Transition, DataPattern> getInputTransOfData(String datakey) {
		if (inputDataPatterns.containsKey(datakey)) {
			return inputDataPatterns.get(datakey);
		}
		return new HashMap<Transition, DataPattern>();
	}

	public List<DataPattern> getDataPatternsOfKey(String label) {
		List<DataPattern> result = new ArrayList<DataPattern>();
		result.addAll(this.inputDataPatterns.get(label).values());
		result.addAll(this.outputDataPatterns.get(label).values());
		for (LogMovePattern lmp : logMovePatterns) {
			for (DataPattern dp : lmp.getDataPatterns()) {
				if (dp.getDataKey().equals(label)) {
					result.add(dp);
				}
			}
		}
		return result;
	}

	
	private Set<Transition> getPostSetTransitions(PAlignment poalignment, Move node) {
		Set<Transition> ts = new HashSet<Transition>();
		for (Move move : poalignment.getDirectChildren(node)) {
			if (move.isSyncMove() || move.isModelMove()) {
				ts.add(move.getTransition());
			} else if (move.isLogMove()) {
				Set<Transition> posts = getPostSetTransitions(poalignment, move);
				ts.addAll(posts);
			}

		}
		return ts;
	}

	private Set<Transition> getPreSetTransitions(PAlignment poalignment, Move node) {
		Set<Transition> ts = new HashSet<Transition>();
		for (Move source : poalignment.getDirectParents(node)) {			
			if (source.isSyncMove() || source.isModelMove()) {
				ts.add(source.getTransition());
			} else if (source.isLogMove()) {
				Set<Transition> pres = getPreSetTransitions(poalignment, source);
				ts.addAll(pres);
			}
		}
		return ts;
	}
}
