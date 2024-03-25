package org.processmining.partialorder.models.palignment;

import java.util.Map;

import org.apache.commons.collections15.map.HashedMap;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XEvent;
import org.processmining.partialorder.models.graph.POEdge;
import org.processmining.partialorder.models.graph.PONode;
import org.processmining.partialorder.models.graph.PartialOrderGraph;
import org.processmining.partialorder.models.graph.edge.POEdgeImp;
import org.processmining.partialorder.models.graph.edge.POEdgeLog;
import org.processmining.partialorder.models.graph.edge.POEdgeModel;
import org.processmining.partialorder.models.graph.edge.POEdgeSync;
import org.processmining.partialorder.models.graph.node.POLogMoveNode;
import org.processmining.partialorder.models.graph.node.POModelMoveInvisibleNode;
import org.processmining.partialorder.models.graph.node.POModelMoveVisibleNode;
import org.processmining.partialorder.models.graph.node.PONodeMove;
import org.processmining.partialorder.models.graph.node.POSyncMoveNode;
import org.processmining.partialorder.plugins.vis.PartialOrderGraphFactory;
import org.processmining.partialorder.plugins.vis.PartialVisualType;
import org.processmining.partialorder.plugins.vis.palignment.PAlignmentMainVisPanel.POAlignmentVisType;

public class PAlignmentConverter {

	public PartialOrderGraph convert(PAlignment pAlignment, XEventClassifier classifier) {
		return convert(pAlignment, POAlignmentVisType.P_Alignment_Standard, PartialVisualType.AS_IS,  classifier);
	}

	public PartialOrderGraph convert(PAlignment pAlignment, POAlignmentVisType currentType, PartialVisualType visType, XEventClassifier classifier) {
		int traceIndex = pAlignment.getTraceIndex();
		PartialOrderGraph graph = new PartialOrderGraph("Alignment " + traceIndex, traceIndex);


		
		Map<Move, PONode> map = addNodes(pAlignment, graph, classifier);

		if (currentType.equals(POAlignmentVisType.S_Alignment)) {
			addSequentialEdges(pAlignment, graph, map);

		} else {
			addPAlignmentDependencies(pAlignment, graph, map, visType);
		}
		return graph;
	}

	private Map<Move, PONode> addNodes(PAlignment pAlignment, PartialOrderGraph graph, XEventClassifier classifier) {
		Map<Move, PONode> map = new HashedMap<Move, PONode>();
		for (Move move : pAlignment.getMoves()) {
			String label = getMoveLabel(move, classifier);
			
			PONodeMove node = convert(move, graph);
			node.setLabel(label);
			map.put(move, node);
			graph.addNode(node);
		}

		return map;
	}

	public static String getMoveLabel(Move move, XEventClassifier classifier) {
		String label = null;
		XEvent e = move.getEvent();
		if(e != null){
			label = classifier.getClassIdentity(e);
		} else {
			label = move.getTransition().getLabel();
		}
				
				
		return label;
	}

	private void addSequentialEdges(PAlignment pAlignment, PartialOrderGraph graph, Map<Move, PONode> map) {
		for (Move source : pAlignment.getMoves()) {
			for (Move target : pAlignment.getMoves()) {
				if (source.getMoveIndex() + 1 == target.getMoveIndex()) {
					POEdge edge = new POEdgeImp(map.get(source), map.get(target));
					graph.addEdge(edge);
				}
			}
		}
	}

	private void addPAlignmentDependencies(PAlignment pAlignment, PartialOrderGraph graph, Map<Move, PONode> map,
			PartialVisualType visType) {
		for (MoveDependency dep : pAlignment.getDependencies()) {
			if (isMoveDependencyQualified(dep, visType)) {
				PONode source = map.get(dep.getSource());
				PONode target = map.get(dep.getTarget());
				POEdge edge = convert(source, target, dep, graph);
				graph.addEdge(edge);
			}
		}
		if (visType.equals(PartialVisualType.ALIGNMENT_MINIMAL)) {
			PartialOrderGraphFactory.removeRedundantEdges(graph);
		} else if (visType.equals(PartialVisualType.MAXIMAL_CLOSURE)) {
			PartialOrderGraphFactory.addTransitiveEdges(graph);
		}

	}

	private static boolean isMoveDependencyQualified(MoveDependency dep, PartialVisualType visType) {
		boolean qualified = false;
		if (visType.equals(PartialVisualType.AS_IS) || visType.equals(PartialVisualType.MAXIMAL_CLOSURE)) {
			qualified = true;
		} else {
			//(visType.equals(PartialVisualType.MINIMAL_REDUTION) || visType.equals(PartialVisualType.ALIGNMENT_MINIMAL)
			if ((dep.getEventDependency() != null && dep.getEventDependency().isDirect()) || dep.isModelDependency()) {
				qualified = true;
			}
		}
		return qualified;
	}

	private static POEdge convert(PONode source, PONode target, MoveDependency dep, PartialOrderGraph graph) {
		POEdge edge = null;
		if (dep.isSyncDependency()) {
			edge = new POEdgeSync(source, target, dep.getEventDependency());
		} else if (dep.isModelDependency()) {
			edge = new POEdgeModel(source, target);
		} else if (dep.isLogDependency()) {
			edge = new POEdgeLog(source, target, dep.getEventDependency());
		} else {
			edge = new POEdgeImp(source, target);
		}
		return edge;
	}

	public static PONodeMove convert(Move move, PartialOrderGraph graph) {
		PONodeMove node = null;
		if (move.isLogMove()) {
			node = new POLogMoveNode(graph, move);
		} else if (move.isVisibleModelMove()) {
			node = new POModelMoveVisibleNode(graph, move);
		} else if (move.isSilentModelMove()) {
			node = new POModelMoveInvisibleNode(graph, move);
		} else if (move.isSyncMove()) {
			node = new POSyncMoveNode(graph, move);
		} else {
			// error handle
		}
		return node;
	}

}
