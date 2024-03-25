package org.processmining.partialorder.ptrace.plugins.builder.alg;

import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.processmining.partialorder.models.dependency.DependencyFactory;
import org.processmining.partialorder.models.dependency.PDependency;
import org.processmining.partialorder.ptrace.model.PTrace;
import org.processmining.partialorder.ptrace.model.imp.PTraceImp;
import org.processmining.partialorder.ptrace.plugins.vis.model.FilterDistance;
import org.processmining.partialorder.ptrace.plugins.vis.model.FilterDistance.ComparisonType;

import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class PTraceUtil {

	/**
	 * For each edge in the ptrace, compute isDirect and setDirect
	 * 
	 * @param ptrace
	 */
	public static void computeAndSetTransitiveReduction(PTrace ptrace) {
		DirectedSparseGraph<Integer, PDependency> graph = new DirectedSparseGraph<Integer, PDependency>();
		for (PDependency e : ptrace.getDependencies()) {
			graph.addVertex(e.getSource());
			graph.addVertex(e.getTarget());
			graph.addEdge(e, e.getSource(), e.getTarget());
		}

		for (PDependency e : ptrace.getDependencies()) {
			Integer source = graph.getSource(e);
			Integer target = graph.getDest(e);
			graph.removeEdge(e);
			UnweightedShortestPath<Integer, PDependency> dist = new UnweightedShortestPath<Integer, PDependency>(graph);
			if (dist.getDistance(source, target) == null) {
				graph.addEdge(e, source, target);
				e.setDirect(true);
			} else {
				e.setDirect(false);
			}
		}
	}

	/**
	 * Given a ptrace, the function computes the Eventually-followed-by
	 * relations and the Uncertainty relations and add it to ptrace.
	 * 
	 * @param ptrace
	 */
	public static void computeCompleteRelations(PTrace ptrace) {
		DirectedSparseGraph<Integer, PDependency> graph = new DirectedSparseGraph<Integer, PDependency>();
		for (PDependency e : ptrace.getDependencies()) {
			graph.addVertex(e.getSource());
			graph.addVertex(e.getTarget());
			graph.addEdge(e, e.getSource(), e.getTarget());
		}

		for (int i = 0; i < ptrace.size(); i++) {
			for (int j = 0; j < ptrace.size(); j++) {
				PDependency e_ij = ptrace.getDependency(i, j);
				PDependency e_ji = ptrace.getDependency(j, i);

				if (e_ij == null && e_ji == null) {
					UnweightedShortestPath<Integer, PDependency> dist = new UnweightedShortestPath<Integer, PDependency>(
							graph);
					if (dist.getDistance(i, j) == null && dist.getDistance(i, j) == null) {
						// add concurrency
						ptrace.addDependency(DependencyFactory.createUncertainDependency(i, j), i, j);
					} else if (dist.getDistance(i, j) != null) {
						// add e_i eventually followed by e_j
						ptrace.addDependency(DependencyFactory.createEventuallyDependency(i, j), i, j);

					} else {
						// add e_j eventually followed by e_i
						ptrace.addDependency(DependencyFactory.createEventuallyDependency(j, i), j, i);
					}
				} else {
					PDependency e = e_ij != null ? e_ij : e_ji;
					graph.removeEdge(e);
					UnweightedShortestPath<Integer, PDependency> dist = new UnweightedShortestPath<Integer, PDependency>(
							graph);
					if (dist.getDistance(e.getSource(), e.getTarget()) == null) {
						graph.addEdge(e, e.getSource(), e.getTarget());
						e.setDirect(true);
					} else {
						e.setDirect(false);
					}
				}

			}
		}
	}

	public static boolean areEventsWithInDistance(PTrace potrace, FilterDistance filter) {
		if (potrace instanceof PTraceImp) {
			Set<Integer> preds = new HashSet<Integer>();
			Set<Integer> succs = new HashSet<Integer>();
			for (Integer i : potrace.getEventIndices()) {
				String name = XConceptExtension.instance().extractName(potrace.getTrace().get(i));
				if (name.contains(filter.getPredecessor())) {
					preds.add(i);
				}
				if (name.contains(filter.getSuccessor())) {
					succs.add(i);
				}
			}
			//			PTraceImp potraceGraph = (PTraceImp) potrace;

			DirectedSparseGraph<Integer, PDependency> poGraph = new DirectedSparseGraph<Integer, PDependency>();
			for (Integer i : potrace.getEventIndices()) {
				poGraph.addVertex(i);
			}
			for (PDependency d : potrace.getDependencies()) {
				if (d.isDirect()) {
					poGraph.addEdge(d, d.getSource(), d.getTarget());
				}
			}

			UnweightedShortestPath<Integer, PDependency> distGraph = new UnweightedShortestPath<Integer, PDependency>(
					poGraph);
			int minDist = Integer.MAX_VALUE;
			for (Integer p : preds) {
				for (Integer s : succs) {
					Number n = distGraph.getDistance(p, s);
					if (n != null && n.intValue() > 0) {
						if (n.intValue() < minDist) {
							minDist = n.intValue();

						}

						//						return true;
					}
				}
			}
			Number d = distGraph.getDistance((Integer) potrace.getStartEventIndices().toArray()[0], (Integer) potrace
					.getEndEventIndices().toArray()[0]);

			System.out.println(minDist + ", " + d);
			if (filter.getCompareType().equals(ComparisonType.Concurrent)) {
				if (minDist == Integer.MAX_VALUE) {
					return true;
				} else {
					return false;
				}
			} else {
				if (minDist == Integer.MAX_VALUE) {
					return false;
				}
				if (filter.getCompareType().equals(ComparisonType.Equal)) {
					return minDist == filter.getDist();
				}
				if (filter.getCompareType().equals(ComparisonType.Equal_and_LessThan)) {
					return minDist <= filter.getDist();
				}
				if (filter.getCompareType().equals(ComparisonType.Equal_and_GreaterThan)) {
					return minDist >= filter.getDist();
				}
			}
		}

		return false;
	}

}
