package org.processmining.partialorder.zexperiment;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import nl.tue.astar.AStarException;

import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.extension.std.XIdentityExtension;
import org.deckfour.xes.id.XID;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.partialorder.models.graph.POEdge;
import org.processmining.partialorder.models.graph.PONode;
import org.processmining.partialorder.models.graph.PartialOrderGraph;
import org.processmining.partialorder.models.graph.node.POLogMoveNode;
import org.processmining.partialorder.models.graph.node.POModelMoveNode;
import org.processmining.partialorder.models.graph.node.POModelMoveVisibleNode;
import org.processmining.partialorder.models.graph.node.PONodeMove;
import org.processmining.partialorder.models.graph.node.POSyncMoveNode;
import org.processmining.partialorder.models.palignment.Move;
import org.processmining.partialorder.models.palignment.PAlignment;
import org.processmining.partialorder.models.replay.POSyncReplayResult;
import org.processmining.partialorder.plugins.vis.PartialOrderGraphFactory;
import org.processmining.partialorder.ptrace.param.PAlignmentParameter;
import org.processmining.partialorder.ptrace.param.PTraceParameter.PTraceType;
import org.processmining.partialorder.zexperiment.metric.AbstractMetric.MetricProperty;
import org.processmining.partialorder.zexperiment.metric.ExpMetricPerTrace;
import org.processmining.partialorder.zexperiment.metric.LogMetric;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.ui.PNReplayerUI;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

/**
 * This class provides the plugins (functions) that execute four experiments.
 * 
 * (1) To execute the four experiments in the paper "Conformance checking based
 * on partially ordered event data" published in BPM2014 SBP workshop, please
 * check out the version 15231. Or check out the same class in the
 * EnrichedLogReplayer package. 
 * 
 * @author xlu
 * 
 */
//@Plugin(name = "Partial Order Experiment (SBP)", returnLabels = { "Experiment" }, returnTypes = { PNRepResult.class },
//		parameterLabels = { "Petri net", "Event Log" })
public class ExperimentSBP extends Experiment {

	private int[] expparam;

//	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Xixi Lu", email = "x.lu@tue.nl")
//	@PluginVariant(requiredParameterLabels = { 0, 1 })
	public PNRepResult run(final UIPluginContext context, Petrinet net, XLog log) throws Exception {

		/*
		 * The following three parameters are the settings of the experiment
		 * executed for BPM 2014 SBP workshop.
		 * 
		 * @expNumber is the Experiment number x
		 * 
		 * @version is the version of csv file produced
		 * 
		 * @numbExps is the number of re-runs of experiments to be executed in
		 * one run.
		 */
		int expNumber = 4;
		int version = 1;
		int numbExps = 1;

		// 
		Boolean shuffle = initializeExperiment(expNumber, version);
		configurations = getConfiguration(context, net, log);

		TransEvClassMapping mapping = (TransEvClassMapping) configurations[PNReplayerUI.MAPPING];
		XLogInfo info = XLogInfoFactory.createLogInfo(log, mapping.getEventClassifier());
		XEventClasses classes = info.getEventClasses();

		for (int i = 0; i < numbExps; i++) {
			XLog testLog = (XLog) log.clone();
			LogNoiseRecorder recorder = new LogNoiseRecorder();
			ExperimentUtil.addIdentifiersToEventsAndRecord(testLog, recorder);

			XLog idealLog = (XLog) testLog.clone();

			testLog = addDeviationsAndUpdateRecorder(testLog, recorder);
			testLog = generateTestLogByShuffleEvents(testLog, shuffle);

			Map<Integer, PartialOrderGraph> mapIdeals = computeIdealAlignments(context, net, idealLog, mapping,
					classes, recorder);

			executeExperiment(context, net, testLog, mapIdeals, mapping, classes, recorder,
					PTraceType.Sequential_Dependency);

			executeExperiment(context, net, testLog, mapIdeals, mapping, classes, recorder,
					PTraceType.Non_Equal_Timestamp_Dependency);

			executeExperiment(context, net, testLog, mapIdeals, mapping, classes, recorder,
					PTraceType.Data_Dependency);
		}
		out.close();
		resfile.close();
		return null;
	}

	private boolean initializeExperiment(int expNumber, int version) {
		resfile = getPrintWriterByFile("RunsOfExperiment" + expNumber + "v" + version + ".csv");
		out = getPrintWriterByFile("logE" + expNumber + "v" + version + ".txt");

		boolean shuffle;
		switch (expNumber) {
			case 1 :
				expparam = new int[] { 0, 0, 0, 0, 0, 0 };
				shuffle = false;
				break;
			case 2 :
				expparam = new int[] { 0, 0, 0, 0, 0, 0 };
				shuffle = true;
				break;
			case 3 :
				expparam = new int[] { 2, 2, 0, 0, 0, 0 };
				shuffle = false;
				break;
			case 4 :
				expparam = new int[] { 2, 2, 0, 0, 0, 0 };
				shuffle = true;
				break;
			default :
				expparam = new int[] { 0, 0, 0, 0, 0, 0 };
				shuffle = false;
				break;
		}
		return shuffle;
	}

	private void executeExperiment(UIPluginContext context, Petrinet net, XLog testLog,
			Map<Integer, PartialOrderGraph> mapIdeals, TransEvClassMapping mapping, XEventClasses classes,
			LogNoiseRecorder recorder, PTraceType type) throws ConnectionCannotBeObtained, AStarException {
		PAlignmentParameter param = new PAlignmentParameter();
		param.setType(type);
		printParam(System.currentTimeMillis());
		PNRepResult res1 = runTest(context, net, testLog, param);

		for (SyncReplayResult res : res1) {
			POSyncReplayResult posrr = (POSyncReplayResult) res;
			if (posrr.getStepTypes().size() != posrr.getPOAlignmentGraph().getMoves().size()
					|| posrr.getNodeInstance().size() < 5) {
				try {
					throw new Exception("Moves removed before compute metric");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		LogMetric logMetric1 = computeComplexMetricOfLog(testLog, mapIdeals, res1, mapping, classes, recorder, param);
		printMetric(resfile, logMetric1);
	}

	private Map<Integer, PartialOrderGraph> computeIdealAlignments(UIPluginContext context, Petrinet net,
			XLog idealLog, TransEvClassMapping mapping, XEventClasses classes, LogNoiseRecorder recorder)
			throws ConnectionCannotBeObtained, AStarException {
		PAlignmentParameter param = new PAlignmentParameter();
		param.setType(PTraceType.Data_Dependency);
		PNRepResult resIdeal = runTestWithoutPrint(context, net, idealLog, param);
		Map<Integer, PartialOrderGraph> mapTIndex2pAlignment = IdealAlignmentAlg.computeIdealAlignments(idealLog,
				resIdeal, recorder);
		return mapTIndex2pAlignment;
	}

	private LogMetric computeComplexMetricOfLog(XLog testLog, Map<Integer, PartialOrderGraph> mapIdeals,
			PNRepResult res, TransEvClassMapping mapping, XEventClasses classes, LogNoiseRecorder recorder,
			PAlignmentParameter param) {
		LogMetric logMetric = new LogMetric();

		/* Compared to the known result */
		for (SyncReplayResult srr : res) {
			POSyncReplayResult posrr = (POSyncReplayResult) srr;

			//			if (posrr.getStepTypes().size() != posrr.getPOAlignmentGraph().getMoves().size()
			//					|| posrr.getNodeInstance().size() < 5) {
			//				try {
			//					throw new Exception("Moves removed out for loop");
			//				} catch (Exception e) {
			//					e.printStackTrace();
			//				}
			//			}
			for (int traceIndex : posrr.getTraceIndex()) {
				out.println("Trace " + traceIndex + " measure : =================");
				XTrace newTrace = testLog.get(traceIndex);
				//				//REMOVE
				//				if (posrr.getStepTypes().size() != posrr.getPOAlignmentGraph().getMoves().size()
				//						|| posrr.getNodeInstance().size() < 5) {
				//					try {
				//						throw new Exception("Moves removed inner forloop");
				//					} catch (Exception e) {
				//						e.printStackTrace();
				//					}
				//				}
				ExpMetricPerTrace traceMetric = computeComplexMetricOfTrace(traceIndex, newTrace, posrr,
						mapIdeals.get(traceIndex), mapping, classes, recorder, param);

				printMetric(out, traceMetric);
				logMetric.addTraceMetric(traceIndex, traceMetric);
			}
		}
		return logMetric;

	}

	private ExpMetricPerTrace computeComplexMetricOfTrace(int traceIndex, XTrace newTrace, POSyncReplayResult posrr,
			PartialOrderGraph idealAlignment, TransEvClassMapping mapping, XEventClasses classes,
			LogNoiseRecorder recorder, PAlignmentParameter param) {
		ExpMetricPerTrace traceMetric = new ExpMetricPerTrace();

		PAlignment poalignment = posrr.getPOAlignmentGraph();

		PartialOrderGraph graph = null;
		if (param.isComputingSequentialTraces()) {
			graph = PartialOrderGraphFactory.convertToSeqAlignment(poalignment, mapping.getEventClassifier());
		} else {
			graph = PartialOrderGraphFactory.convertToAlignmentMinimal(poalignment, mapping.getEventClassifier());
		}

		//REMOVE
		//		if (posrr.getStepTypes().size() != posrr.getPOAlignmentGraph().getMoves().size()
		//				|| posrr.getNodeInstance().size() < 5) {
		//			try {
		//				throw new Exception("Moves removed");
		//			} catch (Exception e) {
		//				e.printStackTrace();
		//			}
		//		}
		// Evaluate moves 
		Map<PONode, Object> mapNode2EventOrTransition = computeMoveMetricOfTrace(idealAlignment, graph, posrr,
				newTrace, traceMetric);

		// Evaluate depedencies
		computeDependencyMetricOfTrace(idealAlignment, traceMetric, graph, mapNode2EventOrTransition, mapping, classes);

		return traceMetric;
	}

	private Map<PONode, Object> computeMoveMetricOfTrace(PartialOrderGraph idealAlignment,
			PartialOrderGraph poalignment, POSyncReplayResult posrr, XTrace newTrace, ExpMetricPerTrace traceMetric) {
		Collection<PONode> fnMoves = new HashSet<PONode>(idealAlignment.getNodes());
		Map<PONode, Object> mapNode2EventOrTransition = new HashMap<PONode, Object>();
		for (int i = 0; i < posrr.getStepTypes().size(); i++) {
			PONodeMove n = poalignment.getNode(i);
			//			StepTypes stepType = posrr.getStepTypes().get(i);
			int newEventIndex = posrr.getIndeces().get(i);

			PONode ideal = null;

			if (n.getMove().isModelMove()) {
				Transition modelmove = n.getTransition();
				ideal = isContainedInIdealModelMovesAndRemove(fnMoves, modelmove, i);
				if (ideal != null) {
					traceMetric.incrementPropByOne(MetricProperty.TP_MODEL_MOVE);
				} else {
					traceMetric.incrementPropByOne(MetricProperty.FP_MODEL_MOVE);
				}
				mapNode2EventOrTransition.put(n, modelmove);

			} else if (n.getMove().isLogMove()) {
				XEvent logmove = newTrace.get(newEventIndex);
				ideal = isInIdealLogMovesAndRemove(fnMoves, logmove, i);
				if (ideal != null) {
					traceMetric.incrementPropByOne(MetricProperty.TP_LOG_MOVE);
				} else {
					traceMetric.incrementPropByOne(MetricProperty.FP_LOG_MOVE);
				}
				mapNode2EventOrTransition.put(n, logmove);

			} else {
				XEvent synmove = newTrace.get(newEventIndex);
				ideal = isInIdealSyncMovesAndRemove(fnMoves, synmove, i);
				if (ideal != null) {
					traceMetric.incrementPropByOne(MetricProperty.TP_SYNC_MOVE);
				} else {
					traceMetric.incrementPropByOne(MetricProperty.FP_SYNC_MOVE);
				}
				mapNode2EventOrTransition.put(n, synmove);
			}
			fnMoves.remove(ideal);

		}

		for (PONode node : fnMoves) {
			if (node instanceof POSyncMoveNode) {
				traceMetric.incrementPropByOne(MetricProperty.FN_SYNC_MOVE);
			} else if (node instanceof POModelMoveVisibleNode) {
				traceMetric.incrementPropByOne(MetricProperty.FN_MODEL_MOVE);
			} else {
				traceMetric.incrementPropByOne(MetricProperty.FN_LOG_MOVE);
			}
		}
		return mapNode2EventOrTransition;

	}

	private PONode isInIdealSyncMovesAndRemove(Collection<PONode> fnMoves, XEvent synmove, int i) {

		for (PONode nodeIdeal : fnMoves) {
			if (nodeIdeal instanceof POSyncMoveNode && isEqualEventsByXIDattribute(synmove, nodeIdeal)) {
				return nodeIdeal;
			}
		}
		return null;
	}

	private PONode isInIdealLogMovesAndRemove(Collection<PONode> fnMoves, XEvent logmove, int i) {
		//		PONode found = null;
		for (PONode nodeIdeal : fnMoves) {
			if (nodeIdeal instanceof POLogMoveNode && isEqualEventsByXIDattribute(logmove, nodeIdeal)) {
				return nodeIdeal;
			}
		}
		return null;
	}

	private boolean isEqualEventsByXIDattribute(XEvent event, PONode nodeIdeal) {
		XID id = XIdentityExtension.instance().extractID(((PONodeMove) nodeIdeal).getEvent());
		XID idEvent = XIdentityExtension.instance().extractID(event);
		return id.equals(idEvent);
	}

	private PONode isContainedInIdealModelMovesAndRemove(Collection<PONode> fnMoves, Transition modelmove, int stepIndex) {
		//		PONode found = null;
		for (PONode nodeIdeal : fnMoves) {
			if (((PONodeMove) nodeIdeal) instanceof POModelMoveNode) {
				Move move = ((PONodeMove) nodeIdeal).getMove();

				if (move.getTransition() != null && move.getTransition().equals(modelmove)) {
					//					System.out.println("modelmove found");
					return nodeIdeal;
				}

			}
		}
		return null;
	}

	private void computeDependencyMetricOfTrace(PartialOrderGraph idealAlignment, ExpMetricPerTrace traceMetric,
			PartialOrderGraph poalignment, Map<PONode, Object> mapNode2EventOrTransition, TransEvClassMapping mapping,
			XEventClasses classes) {

		Collection<POEdge> fnDeps = new HashSet<POEdge>(idealAlignment.getEdges());
		Collection<POEdge> reductedalignment = PartialOrderGraphFactory.computeTransitiveReduction(poalignment); // Only the minimal edges

		for (POEdge e : reductedalignment) {
			if (isContainedInIdealEdgesAndRemove(fnDeps, e, mapNode2EventOrTransition, mapping, classes)) {
				traceMetric.incrementPropByOne(MetricProperty.TP_DFOLLOWED);
			} else {
				traceMetric.incrementPropByOne(MetricProperty.FP_DFOLLOWED);
			}
		}
		traceMetric.incrementProp(MetricProperty.FN_DFOLLOWED, fnDeps.size());

	}

	private boolean isContainedInIdealEdgesAndRemove(Collection<POEdge> fnDeps, POEdge e,
			Map<PONode, Object> mapNode2EventOrTransition, TransEvClassMapping mapping, XEventClasses classes) {

		Object source = mapNode2EventOrTransition.get(e.getSource());
		Object target = mapNode2EventOrTransition.get(e.getTarget());
		Map<POEdge, Integer> founds = new HashMap<POEdge, Integer>();
		for (POEdge idealEdge : fnDeps) {
			PONode idealSource = idealEdge.getSource();
			PONode idealTarget = idealEdge.getTarget();
			int matchCertainty = isNodesExactMatch(source, idealSource, mapping, classes)
					+ isNodesExactMatch(target, idealTarget, mapping, classes);
			if (matchCertainty > 0) {
				founds.put(idealEdge, matchCertainty);
			}
		}
		if (founds.keySet().size() > 1) {
			POEdge found = getMaxEdge(founds);
			fnDeps.remove(found);
			return true;
		}
		if (founds.keySet().size() == 1) {
			for (POEdge found : founds.keySet()) {
				fnDeps.remove(found);
			}
			return true;
		}
		return false;

	}

	private POEdge getMaxEdge(Map<POEdge, Integer> founds) {
		int max = Integer.MIN_VALUE;
		POEdge found = null;
		for (POEdge e : founds.keySet()) {
			if (founds.get(e) > max) {
				max = founds.get(e);
				found = e;
			}
		}
		return found;
	}

	private int isNodesExactMatch(Object source, PONode idealSource, TransEvClassMapping mapping, XEventClasses classes) {
		if (source instanceof XEvent) {
			if (idealSource instanceof POLogMoveNode || idealSource instanceof POSyncMoveNode) {
				if (isEqualEventsByXIDattribute((XEvent) source, idealSource)) {
					// events are matching. Highest level
					return 3;
				} else {
					// Different events, no match
					return -3;
				}
			} else {
				if (isSameClass((XEvent) source, ((POModelMoveVisibleNode) idealSource).getTransition(), mapping,
						classes)) {
					return 1;
				} else {
					return -3;
				}
			}
		} else {
			if (idealSource instanceof POModelMoveVisibleNode) {
				if (((PONodeMove) idealSource).getTransition().equals(source)) {
					return 2;
				} else {
					return -3;
				}
			}
			if (isSameClass(((PONodeMove) idealSource).getEvent(), (Transition) source, mapping, classes)) {
				return 1;
			} else {
				return -3;
			}
		}
	}

	private boolean isSameClass(XEvent event, Transition source, TransEvClassMapping mapping, XEventClasses classes) {
		return mapping.get(source).equals(classes.getClassOf(event));
	}

	private XLog addDeviationsAndUpdateRecorder(XLog log, LogNoiseRecorder recorder) {
		XLog testLog = (XLog) log.clone();

		ExperimentUtil.addEvents(testLog, recorder, expparam[0], out);
		ExperimentUtil.removeEvents(testLog, recorder, expparam[1], out);
		ExperimentUtil.reindexEvents(testLog, recorder, expparam[2], out);
		ExperimentUtil.addCopiedData(testLog, recorder, expparam[3], out);
		ExperimentUtil.removeData(testLog, recorder, expparam[4], out);
		ExperimentUtil.addRemovedData(testLog, recorder, expparam[5], out);

		out.println("=======================================");
		out.println("=====Finish adding noise ==============");
		out.println("=======================================");

		return testLog;
	}

	private void printParam(long run) {
		resfile.print("Run at " + run + ";");
		for (int i : expparam) {
			resfile.print(i + ";");
		}
	}

	@SuppressWarnings("unused")
	private void compareLogMetrics(XLog testLog, LogMetric logMetric1, LogMetric logMetric2) {
		for (int i = 0; i < testLog.size(); i++) {
			ExpMetricPerTrace traceMetric1 = logMetric1.getTraceMetric(i);
			ExpMetricPerTrace traceMetric2 = logMetric2.getTraceMetric(i);
			MetricProperty[] props = new MetricProperty[] { MetricProperty.FP_SYNC_MOVE, MetricProperty.FP_LOG_MOVE };
			for (MetricProperty prop : props) {
				if (traceMetric1.getNumberOfProperty(prop) != traceMetric2.getNumberOfProperty(prop)) {
					System.out.println("Trace " + i + " - " + prop + " unequal ");

				}
			}
		}

	}

}
