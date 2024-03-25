package org.processmining.partialorder.plugins.vis;

import java.util.Collection;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.processmining.partialorder.models.graph.POEdge;
import org.processmining.partialorder.models.graph.PONode;
import org.processmining.partialorder.models.graph.PartialOrderGraph;
import org.processmining.partialorder.models.graph.edge.POEdgeTransitive;
import org.processmining.partialorder.models.palignment.PAlignment;
import org.processmining.partialorder.models.palignment.PAlignmentConverter;
import org.processmining.partialorder.plugins.vis.palignment.PAlignmentMainVisPanel.POAlignmentVisType;

import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class PartialOrderGraphFactory {
	
	public static PartialOrderGraph convertToAlignmentMinimal(PAlignment pAlignment, XEventClassifier classifier) {
		return convert(pAlignment, POAlignmentVisType.P_Alignment_Standard, PartialVisualType.ALIGNMENT_MINIMAL, classifier);
	}
	
	public static PartialOrderGraph convertToSeqAlignment(PAlignment pAlignment, XEventClassifier classifier) {
		return convert(pAlignment, POAlignmentVisType.S_Alignment, PartialVisualType.ALIGNMENT_MINIMAL, classifier);
	}
	
	public static PartialOrderGraph convertToStandard(PAlignment pAlignment, XEventClassifier classifier) {
		return convert(pAlignment, POAlignmentVisType.P_Alignment_Standard, PartialVisualType.MINIMAL_REDUTION, classifier);
	}
	

	
	public static PartialOrderGraph convert(PAlignment pAlignment, POAlignmentVisType currentType,
			PartialVisualType visType, XEventClassifier classifier) {
		PAlignmentConverter plugin = new PAlignmentConverter();
		return plugin.convert(pAlignment, currentType, visType, classifier);
	}
	
	
	public static void addTransitiveEdges(PartialOrderGraph alignment) {		
		DirectedSparseGraph<PONode, POEdge> graph = new DirectedSparseGraph<PONode, POEdge>();
		for (PONode n : alignment.getNodes()) {
			if (!graph.addVertex(n)) {
				System.err.println("Node in graph redundant");
			}
		}

		for (POEdge e : alignment.getEdges()) {
//			System.out.println(e.getSource() + ", " + e.getTarget());
			if (!graph.addEdge(e, e.getSource(), e.getTarget())) {
				System.err.println("Edge in graph redundant");
			}
		}
		
		UnweightedShortestPath<PONode, POEdge> dist = new UnweightedShortestPath<PONode, POEdge>(
				graph);
		for (PONode i : graph.getVertices()) {
			for (PONode j : graph.getVertices()) {
				if (dist.getDistance(i, j) != null && dist.getDistance(i, j).intValue() > 1) {
					POEdge e = new POEdgeTransitive(i, j);
					alignment.addEdge(e);
				}
			}
		}
	}

	
	public static void removeRedundantEdges(PartialOrderGraph graph) {
		Collection<POEdge> edges = computeTransitiveReduction(graph);
		graph.retainEdges(edges);		
	}
	
	
	public static Collection<POEdge> computeTransitiveReduction(PartialOrderGraph poalignment) {
		DirectedSparseGraph<PONode, POEdge> graph = new DirectedSparseGraph<PONode, POEdge>();
		for (PONode n : poalignment.getNodes()) {
			if (!graph.addVertex(n)) {
				System.err.println("Node in graph redundant");
			}
		}

		for (POEdge e : poalignment.getEdges()) {
//			System.out.println(e.getSource() + ", " + e.getTarget());
			if (!graph.addEdge(e, e.getSource(), e.getTarget())) {
				System.err.println("Edge in graph redundant");
			}
		}

		for (POEdge e : poalignment.getEdges()) {
			PONode source = graph.getSource(e);
			PONode target = graph.getDest(e);
			if (source == null || target == null) {
				continue;
			}
			graph.removeEdge(e);
			UnweightedShortestPath<PONode, POEdge> dist = new UnweightedShortestPath<PONode, POEdge>(graph);
			if (dist.getDistance(source, target) == null) {
				graph.addEdge(e, source, target);
			}
		}

		return graph.getEdges();
	}

	public static PartialOrderGraph convert(PAlignment alignment, POAlignmentVisType currentType,
			PartialVisualType visType) {
		return convert(alignment, currentType, visType, new XEventNameClassifier());
	}



}
