package org.processmining.partialorder.models.replay;

import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.partialorder.plugins.vis.palignment.PAlignments;
import org.processmining.partialorder.ptrace.model.PLog;
import org.processmining.partialorder.ptrace.model.PTrace;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;


public class POAlignmentDataProvider {

	protected PLog poLog;
	protected XLog log;
	protected PetrinetGraph graph;
	protected TransEvClassMapping mapping;
	protected CostBasedCompleteParam param;
	protected PNRepResult logReplayResult;
	protected XLogInfo info;
	private PAlignments palignments;

	//	/* init data variables */
	//	PNRepResult logReplayResult = data.getLogReplayResult();
	//	XLog log = data.getLog();
	//	PartiallyOrderedLog poLog = data.getPoLog();
	//	XEventClasses classes = data.getEventClasses();
	//	CostBasedCompleteParam param = data.getParam();
	//	TransEvClassMapping mapping = data.getMapping();
	//	PetrinetGraph model = data.getGraph();

	public POAlignmentDataProvider(PLog poLog, XLog log, PNRepResult logReplayResult, PetrinetGraph graph,
			TransEvClassMapping mapping, CostBasedCompleteParam param) {
		this.poLog = poLog;
		this.log = log;
		this.graph = graph;
		this.mapping = mapping;
		this.param = param;
		this.logReplayResult = logReplayResult;
		this.info = XLogInfoFactory.createLogInfo(log, mapping.getEventClassifier());
	}
	
	public POAlignmentDataProvider(PLog poLog, XLog log, PAlignments palignments, PetrinetGraph graph,
			TransEvClassMapping mapping, CostBasedCompleteParam param) {
		this.poLog = poLog;
		this.log = log;
		this.graph = graph;
		this.mapping = mapping;
		this.param = param;
//		this.logReplayResult = logReplayResult;
		this.setPalignments(palignments);
		this.info = XLogInfoFactory.createLogInfo(log, mapping.getEventClassifier());
	}

	public PLog getPoLog() {
		return poLog;
	}

	public void setPoLog(PLog poLog) {
		this.poLog = poLog;
	}

	public XLog getLog() {
		return log;
	}

	public void setLog(XLog log) {
		this.log = log;
	}

	public PetrinetGraph getGraph() {
		return graph;
	}

	public void setGraph(PetrinetGraph graph) {
		this.graph = graph;
	}

	public TransEvClassMapping getMapping() {
		return mapping;
	}

	public void setMapping(TransEvClassMapping mapping) {
		this.mapping = mapping;
	}

	public CostBasedCompleteParam getParam() {
		return param;
	}

	public void setParam(CostBasedCompleteParam param) {
		this.param = param;
	}

	public PNRepResult getLogReplayResult() {
		return logReplayResult;
	}

	public void setLogReplayResult(PNRepResult logReplayResult) {
		this.logReplayResult = logReplayResult;
	}

	public XEventClasses getEventClasses() {
		return info.getEventClasses();
	}

	public Marking getInitialMarking() {
		return param.getInitialMarking();
	}

	public Marking[] getFinalMarkings() {
		return param.getFinalMarkings();
	}

	public Petrinet getPetrinet() {
		try {
			if (graph instanceof Petrinet) {
				return (Petrinet) graph;
			} else {
				throw new UnsupportedOperationException ("Graph not a Petri Net");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public PTrace getPOTrace(int traceIndex) {
		return poLog.get(traceIndex);
	}

	public XTrace getXTrace(int traceIndex) {
		return log.get(traceIndex);
	}

	public PAlignments getPalignments() {
		return palignments;
	}

	public void setPalignments(PAlignments palignments) {
		this.palignments = palignments;
	}

}
