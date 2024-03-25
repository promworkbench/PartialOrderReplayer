package org.processmining.partialorder.zexperiment;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.tue.astar.AStarException;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.models.connections.petrinets.EvClassLogPetrinetConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.partialorder.dialogs.PartialNetReplayerUI;
import org.processmining.partialorder.models.dependency.Dependency;
import org.processmining.partialorder.models.dependency.DependencyImp;
import org.processmining.partialorder.models.graph.POEdge;
import org.processmining.partialorder.models.graph.PONode;
import org.processmining.partialorder.models.graph.PartialOrderGraph;
import org.processmining.partialorder.models.graph.edge.POEdgeModel;
import org.processmining.partialorder.models.graph.node.PONodeMove;
import org.processmining.partialorder.plugins.PartialNetReplayer;
import org.processmining.partialorder.ptrace.param.PAlignmentParameter;
import org.processmining.partialorder.zexperiment.metric.AbstractMetric;
import org.processmining.partialorder.zexperiment.metric.AbstractMetric.MetricProperty;
import org.processmining.partialorder.zexperiment.metric.ExpMetricPerTrace;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNPartialOrderAwareReplayAlgorithm;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayer.ui.PNReplayerUI;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;

public abstract class Experiment {
	PrintWriter resfile;
	PrintWriter out;// = new PrintWriter(System.out);
	
	Object[] configurations = new Object[3];
	
	
	
	
	
	@SuppressWarnings("deprecation")
	protected XLog generateTestLogByShuffleEvents(XLog log, Boolean shuffle) {
		XLog testLog = (XLog) log.clone();
		/*
		 * Set the timestamps of all events to the same date.
		 */
		Date d = new Date(2014, 4, 30);
		for (XTrace t : testLog){
			for (XEvent e : t){
				XTimeExtension.instance().assignTimestamp(e, d);
			}
			/* Shuffle */ 
			if(shuffle){
				Collections.shuffle(t);
			}
		}
		return testLog;
	}

	public static PrintWriter getPrintWriterByFile(String string) {
		PrintWriter printer = null;
		try {
			printer = new PrintWriter(new BufferedWriter(new FileWriter(string, true)));
			//out = new PrintWriter("log.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return printer;
	}
	
	protected Object[] getConfiguration(UIPluginContext context, Petrinet net, XLog log) throws ConnectionCannotBeObtained {
		PartialNetReplayerUI partialnetReplayerUI = new PartialNetReplayerUI();
		Object[] resultConfiguration = partialnetReplayerUI.getConfiguration(context, net, log);
		if (resultConfiguration == null) {
			context.getFutureResult(0).cancel(true);
			return null;
		}

		// if all parameters are set, replay log
		if (resultConfiguration[PNReplayerUI.MAPPING] != null) {
			context.log("replay is performed. All parameters are set.");

			// This connection MUST exists, as it is constructed by the configuration if necessary
			context.getConnectionManager().getFirstConnection(EvClassLogPetrinetConnection.class, context, net, log);

			// get all parameters
//			IPNPartialOrderAwareReplayAlgorithm selectedAlg = (IPNPartialOrderAwareReplayAlgorithm) resultConfiguration[PNReplayerUI.ALGORITHM];
			IPNReplayParameter algParameters = (IPNReplayParameter) resultConfiguration[PNReplayerUI.PARAMETERS];

			// since based on GUI, create connection
			algParameters.setCreateConn(true);
			algParameters.setGUIMode(false);
		}
		return resultConfiguration;
	}

	protected PNRepResult runTestWithoutPrint(UIPluginContext context, Petrinet net, XLog log,
			PAlignmentParameter param) throws ConnectionCannotBeObtained, AStarException {
		IPNPartialOrderAwareReplayAlgorithm selectedAlg = (IPNPartialOrderAwareReplayAlgorithm) configurations[PNReplayerUI.ALGORITHM];
		IPNReplayParameter algParameters = (IPNReplayParameter) configurations[PNReplayerUI.PARAMETERS];
		TransEvClassMapping mapping = (TransEvClassMapping) configurations[PNReplayerUI.MAPPING];
		PartialNetReplayer replayer = new PartialNetReplayer();
//		
		if (!selectedAlg.isAllReqSatisfied(context, net, log, mapping, algParameters)) {
			getConfiguration(context, net, log);
			selectedAlg = (IPNPartialOrderAwareReplayAlgorithm) configurations[PNReplayerUI.ALGORITHM];
			algParameters = (IPNReplayParameter) configurations[PNReplayerUI.PARAMETERS];
			mapping = (TransEvClassMapping) configurations[PNReplayerUI.MAPPING];
		}
		
		param.setComputeLasy(false);
		PNRepResult res = replayer.replayLogExperiment(context, net, log, mapping, selectedAlg, algParameters,  param);
		return res;
	}
	
	protected PNRepResult runTest(UIPluginContext context, Petrinet net, XLog log, PAlignmentParameter param)
			throws ConnectionCannotBeObtained, AStarException {

		
		PNRepResult res =  runTestWithoutPrint(context, net, log, param);
		
		/* IF noise log moves with names not in model */
//		PartialNetReplayer replayer = new PartialNetReplayer();
//		PNRepResult res = replayer.replayLog(context, net, log, param);

		
		if (param.isComputingDataDependencies()) {
			resfile.print("Data PO;");
		} else if (param.isComputingSequentialTraces()) {
			resfile.print("Sequential;");
			//sequentializeResult(res);
		} else {
			resfile.print("Time PO;");
		}
		return res;
	}

//	private void sequentializeResult(PNRepResult res) {
//		for( SyncReplayResult srr : res){
//			for(Integer index : srr.getTraceIndex()){
//				POSyncReplayResult posrr =  (POSyncReplayResult) srr;
//				PAlignment palignment = posrr.getPOAlignmentGraph();
//				PartialOrderGraph graph = POAlignmentBuilder.convertToSeqAlignment(palignment);
//				
//				graph.removeEdges();
//				int size = graph.getNodes().size();
//				PONode[] nodes = new PONode[size];
//				for(PONode n : graph.getNodes()){
//					PONodeMove move = (PONodeMove) n;
//					nodes[move.getStepIndex()] = move;
//				}
//				for(int i = 0; i < size -1; i++ ){
//					graph.addEdge(new POEdgeModel(nodes[i], nodes[i+1]));
//				}
//			}
//		}
//		
//	}

	protected DirectlyPrecededGraph computeDirectlyPrecedings(Petrinet net) {
		DirectlyPrecededGraph graph = new DirectlyPrecededGraph();
		for (Transition t : net.getTransitions()) {
			graph.addVertex(t.getLabel());
		}
		for (Place p : net.getPlaces()) {
			Set<Dependency<String>> xorDepedencies = new HashSet<Dependency<String>>();
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> inEdge : net.getInEdges(p)) {
				Transition source = (Transition) inEdge.getSource();

				for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> outEdge : net.getOutEdges(p)) {
					Transition target = (Transition) outEdge.getTarget();
					Dependency<String> dep = new DependencyImp<String>(source.getLabel(), target.getLabel());
					xorDepedencies.add(dep);
					graph.addEdge(dep, source.getLabel(), target.getLabel());

				}
			}
			graph.addChoiceDepedencies(xorDepedencies);
		}

		return graph;
	}

	protected void printMetric(PrintWriter resultRecFile, AbstractMetric logMetric) {
		for (MetricProperty prop : MetricProperty.values()) {
			resultRecFile.print(logMetric.getNumberOfProperty(prop) + ";");
		}
		resultRecFile.println("");
	}

//	protected Collection<POEdge> computeTransitiveReduction(PartialOrderGraph poalignment) {
//		DirectedSparseGraph<Integer, Integer> graph = new DirectedSparseGraph<Integer, Integer>();
//		int i = 0;
//		Map<POEdge, Integer> mapEdge = new HashMap<POEdge, Integer>();
//		Map<PONode, Integer> mapNode = new HashMap<PONode, Integer>();
//		for(PONode n : poalignment.getNodes()){
//			mapNode.put(n, i);
//			if(!graph.addVertex(i)){
//				System.err.println("Node in graph redundant");
//			}
//			i++;
//		}
//
//		i = 0;
//		for (POEdge e : poalignment.getEdges()) {
//			mapEdge.put(e, i);
//			System.out.println(e.getSource() + ", " + e.getTarget());
//			if(!graph.addEdge(i, mapNode.get(e.getSource()), mapNode.get(e.getTarget()))){
//			
//				System.err.println("Edge in graph redundant");
//			}
////			if(map.containsKey(e)){
////				System.err.println("Edge in map redundant");
////			} 
////			map.put(e, i);
//			i++;
//		}
//		
//
//		for (POEdge e : poalignment.getEdges()) {
//			Integer source = graph.getSource(mapEdge.get(e));
//			Integer target = graph.getDest(mapEdge.get(e));
//			if(source == null || target == null){
//				continue;
//			}
//			graph.removeEdge(mapEdge.get(e));
//			UnweightedShortestPath<Integer, Integer> dist = new UnweightedShortestPath<Integer, Integer>(graph);
//			if (dist.getDistance(source, target) == null) {
//				graph.addEdge(mapEdge.get(e), source, target);
//			} 
//		}
//		
//		
//
//		return null;
//	}

	
	protected PONodeMove getMoveByStepIndex(PartialOrderGraph poalignment, int i) {
		for (PONode node : poalignment.getNodes()) {
			if (node instanceof PONodeMove) {
				PONodeMove move = (PONodeMove) node;
				if (move.getStepIndex() == i) {
					return move;
				}
			}
		}
		return null;
	}

	protected void computeDependencyMetricOfTrace(Collection<POEdge> reductedalignment,
			DirectlyPrecededGraph dirPreceded, List<Dependency<XEvent>> deps, ExpMetricPerTrace traceMetric, PartialOrderGraph poalignment) {
		Map<Dependency<String>, Boolean> positiveDirFollowedFound = new HashMap<Dependency<String>, Boolean>();
		for (Dependency<String> e : dirPreceded.getActivatedDirPrecedings()) {
			positiveDirFollowedFound.put(e, false);
		}
		//		System.out.println("Num Model dep : " + positiveDirFollowedFound.keySet().size());
		for (POEdge e : reductedalignment) {
			PONodeMove sourceMove = (PONodeMove) e.getSource();
			PONodeMove targetMove = (PONodeMove) e.getTarget();

			Dependency<String> foundDep = pullDependencyFrom(dirPreceded, positiveDirFollowedFound, sourceMove,
					targetMove);

			if (foundDep != null) {
				/* TP dirf + 1 */
				traceMetric.incrementPropByOne(MetricProperty.TP_DFOLLOWED);
				out.format("TP DD, found in trace: [ %s -> %s ] \n", sourceMove.getLabel(), targetMove.getLabel());
//				POEdge origEdge = poalignment.getEdge(sourceMove, targetMove);
//				origEdge.setMeasurement(MetricProperty.TP_DFOLLOWED);
				
			} else {
				/* FP dirf + 1 */

				if (isContainedAndRemoveFrom(deps, sourceMove, targetMove)) {
					/* TP dirf + 1 */
					traceMetric.incrementPropByOne(MetricProperty.TP_DFOLLOWED);
					out.format("TP DD, added noise: [ %s -> %s ] \n", sourceMove.getLabel(), targetMove.getLabel());
					POEdge origEdge = new POEdgeModel(sourceMove, targetMove);
					poalignment.addEdge(origEdge);
//					origEdge.setMeasurement(MetricProperty.TP_DFOLLOWED);
				} else {
					traceMetric.incrementPropByOne(MetricProperty.FP_DFOLLOWED);
					out.format("FP DD, found in trace: [ %s -> %s ] \n", sourceMove.getLabel(), targetMove.getLabel());
//					POEdge origEdge = poalignment.getEdge(sourceMove, targetMove);
//					origEdge.setMeasurement(MetricProperty.FP_DFOLLOWED);
				}
			}
		}

		for (Dependency<String> e : positiveDirFollowedFound.keySet()) {
			/* Not found dirfs, = FN */
			if (!positiveDirFollowedFound.get(e)
					&& noOtherChoiceDependenciesActivated(positiveDirFollowedFound, e, dirPreceded)) {
				// Debug
				out.format("FN DD, not found in trace: [ %s -> %s ] \n", e.getSource(), e.getTarget());
				traceMetric.incrementPropByOne(MetricProperty.FN_DFOLLOWED);
				PONode source = poalignment.getNode(e.getSource());
				PONode target = poalignment.getNode(e.getTarget());
				if(source != null && target != null){
					POEdge newEdge = new POEdgeModel(source, target);
//					newEdge.setMeasurement(MetricProperty.FN_DFOLLOWED);
					poalignment.addEdge(newEdge);
				}
			}
		}

		for (Dependency<XEvent> e : deps) {
				out.format("FN DD, added noise: [ %s -> %s ] \n", 
						XConceptExtension.instance().extractName( e.getSource()), 
						XConceptExtension.instance().extractName( e.getTarget()));
				traceMetric.incrementPropByOne(MetricProperty.FN_DFOLLOWED);
				
				PONode source = poalignment.getNode(e.getSource());
				PONode target = poalignment.getNode(e.getTarget());
				if(source != null && target != null){
					POEdge newEdge = new POEdgeModel(source, target);
//					newEdge.setMeasurement(MetricProperty.FN_DFOLLOWED);
					poalignment.addEdge(newEdge);
				}
		}
	

	}

	private boolean isContainedAndRemoveFrom(List<Dependency<XEvent>> deps, PONodeMove sourceMove,
			PONodeMove targetMove) {
		if (sourceMove.getEvent() != null && targetMove.getEvent() != null) {
			for (Dependency<XEvent> dep : deps) {
				if (dep.getSource().equals(sourceMove.getEvent()) && dep.getTarget().equals(targetMove.getEvent())) {
					deps.remove(dep);
					return true;
				}
			}
		}
		return false;
	}

	private boolean noOtherChoiceDependenciesActivated(Map<Dependency<String>, Boolean> positiveDirFollowedFound,
			Dependency<String> e, DirectlyPrecededGraph dirPreceded) {
		Set<Dependency<String>> choiceDeps = dirPreceded.getChoiceDependencySet(e);
		for (Dependency<String> otherDepInChoices : choiceDeps) {
			if (positiveDirFollowedFound.containsKey(otherDepInChoices)
					&& positiveDirFollowedFound.get(otherDepInChoices)) {
				return false;
			}
		}

		return true;
	}

	private Dependency<String> pullDependencyFrom(DirectlyPrecededGraph dirPreceded,
			Map<Dependency<String>, Boolean> positiveDirFollowedFound, PONodeMove sourceMove,
			PONodeMove targetMove) {
		String sourcelabel = dirPreceded.getContainedLabel(sourceMove);
		String targetlabel = dirPreceded.getContainedLabel(targetMove);
		for (Dependency<String> e : positiveDirFollowedFound.keySet()) {
			if (e.getSource().equals(sourcelabel) && e.getTarget().equals(targetlabel)) {
				positiveDirFollowedFound.put(e, true);
				return e;
			}
		}

		return null;
	}
	


}
