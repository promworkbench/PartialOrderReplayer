package org.processmining.partialorder.zexperiment;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.partialorder.models.dependency.Dependency;
import org.processmining.partialorder.models.graph.POEdge;
import org.processmining.partialorder.models.graph.PONode;
import org.processmining.partialorder.models.graph.PartialOrderGraph;
import org.processmining.partialorder.models.graph.edge.POEdgeModel;
import org.processmining.partialorder.models.graph.node.POLogMoveNode;
import org.processmining.partialorder.models.graph.node.POModelMoveVisibleNode;
import org.processmining.partialorder.models.graph.node.PONodeMove;
import org.processmining.partialorder.models.graph.node.POSyncMoveNode;
import org.processmining.partialorder.models.palignment.Move;
import org.processmining.partialorder.models.palignment.MoveFactory;
import org.processmining.partialorder.models.palignment.PAlignment;
import org.processmining.partialorder.models.replay.POSyncReplayResult;
import org.processmining.partialorder.plugins.vis.PartialOrderGraphFactory;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

public class IdealAlignmentAlg {

	public static Map<Integer, PartialOrderGraph> computeIdealAlignments(XLog idealLog, PNRepResult resIdeal, LogNoiseRecorder recorder) {
		Map<Integer, PartialOrderGraph> mapTIndex2pAlignment = new HashMap<Integer, PartialOrderGraph>();
		for (SyncReplayResult srr : resIdeal) {
			POSyncReplayResult posrr = (POSyncReplayResult) srr;

			for (int traceIndex : posrr.getTraceIndex()) {
				PAlignment palignment = posrr.getPOAlignmentGraph();
				
				PartialOrderGraph graph = PartialOrderGraphFactory.convertToAlignmentMinimal(palignment, new XEventNameClassifier());				
				PartialOrderGraph ideal = createIdealAlignmentWithNoise(traceIndex, idealLog.get(traceIndex),
						graph, recorder);

				Collection<POEdge> edges = PartialOrderGraphFactory.computeTransitiveReduction(ideal);
				ideal.retainEdges(edges);

				mapTIndex2pAlignment.put(traceIndex, ideal);
			}

		}
		return mapTIndex2pAlignment;
	}
	
	private static PartialOrderGraph createIdealAlignmentWithNoise(int traceIndex, XTrace trace, PartialOrderGraph pAlignment,
			LogNoiseRecorder recorder) {

		PartialOrderGraph idealAlignment = new PartialOrderGraph(String.valueOf(traceIndex), traceIndex);

		List<MoveAdded> addedNoises = recorder.getAdded(traceIndex);
		Map<PONode, PONode> mapOrig2New = new HashMap<PONode, PONode>();

		for (PONode n : pAlignment.getNodes()) {
			PONodeMove nodeMove = (PONodeMove) n;
			PONodeMove newNodeMove = null;
			
			
			// FIXME: clone moves with different events
			Move m = nodeMove.getMove().clone();
			
			if (nodeMove instanceof POLogMoveNode) {
				m.setEvent(trace.get(nodeMove.getEventIndex()));
				newNodeMove = new POLogMoveNode(idealAlignment, m); 
			} else if (nodeMove instanceof POSyncMoveNode) {
				m.setEvent(trace.get(nodeMove.getEventIndex()));
				newNodeMove = new POSyncMoveNode(idealAlignment, m);
			} else {
				newNodeMove = new POModelMoveVisibleNode(idealAlignment, m);
			}
			mapOrig2New.put(nodeMove, newNodeMove);
			if (!idealAlignment.addNode(newNodeMove)) {
				System.out.println("Ideal move duplicated");
			}

		}
		for (POEdge e : pAlignment.getEdges()) {
			POEdge newE = new POEdgeModel(mapOrig2New.get(e.getSource()), mapOrig2New.get(e.getTarget()));
			idealAlignment.addEdge(newE);

		}
		for (MoveAdded added : addedNoises) {
			Move move = MoveFactory.createLogMove(-1, -1, added.getNewEvent());
					
			POLogMoveNode logmove = new POLogMoveNode(idealAlignment, move);
			boolean isAdded = idealAlignment.addNode(logmove);
			if (!isAdded) {
				System.out.println("Ideal log move duplicated");
			}
		}

		for (Dependency<XEvent> dep : recorder.getAddedDependencies(traceIndex)) {

			XEvent source = dep.getSource();
			XEvent target = dep.getTarget();
			//if(source.equals(added.getNewEvent())){
			PONode sourceNode = idealAlignment.getNode(source);
			PONode targetNode = idealAlignment.getNode(target);
			POEdge edge = new POEdgeModel(sourceNode, targetNode);
			idealAlignment.addEdge(edge);
			//				} else {
			//					PONode sourceNode = idealAlignment.getNode(source);
			//					POEdge edge = new POEdge(sourceNode, logmove);
			//					idealAlignment.addEdge(edge);
			//					
			//				}				

		}

		List<MoveRemoved> removedNoises = recorder.getRemoved(traceIndex);
		for (MoveRemoved removed : removedNoises) {

			PONode origMove = idealAlignment.getNode(removed.getOrigEvent());
			Move move = MoveFactory.createVisibleModelMove(-1, ((PONodeMove) origMove).getMove().getTransition());
			POModelMoveVisibleNode modelmove = new POModelMoveVisibleNode(idealAlignment, move);

			if (!idealAlignment.addNode(modelmove)) {
				System.err.println("Nodes duplicated");
			}
			Collection<POEdge> inEdges = idealAlignment.getInEdges(origMove);
			for (POEdge inEdge : inEdges) {
				PONode source = inEdge.getSource();
				idealAlignment.addEdge(new POEdgeModel(source, modelmove));
			}

			Collection<POEdge> outEdges = idealAlignment.getOutEdges(origMove);
			for (POEdge edge : outEdges) {
				PONode target = edge.getTarget();
				idealAlignment.addEdge(new POEdgeModel(modelmove, target));
			}
			idealAlignment.removeEdges(inEdges);
			idealAlignment.removeEdges(outEdges);
			idealAlignment.removeNode(origMove);
		}
		return idealAlignment;

	}

}
