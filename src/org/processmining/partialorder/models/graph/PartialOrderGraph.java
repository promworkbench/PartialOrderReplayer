package org.processmining.partialorder.models.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingConstants;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XIdentityExtension;
import org.deckfour.xes.id.XID;
import org.deckfour.xes.model.XEvent;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.graphbased.directed.DirectedGraphEdge;
import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.DirectedGraphNode;
import org.processmining.partialorder.models.graph.node.POLogMoveNode;
import org.processmining.partialorder.models.graph.node.POModelMoveVisibleNode;
import org.processmining.partialorder.models.graph.node.PONodeMove;
import org.processmining.partialorder.models.graph.node.POSyncMoveNode;
import org.processmining.partialorder.ptrace.model.PTrace;

/**
 * Create a partially ordered graph. This graph class is used
 * 
 * (1) to visualize a {@link PTrace}, (2) to store and visualize a partially
 * ordered alignment (by using {@link POSyncMoveNode}, {@link POLogMoveNode} and
 * {@link POModelMoveVisibleNode} as {@link PONode})
 * 
 * @author xlu
 * 
 */
public class PartialOrderGraph extends AbstractDirectedGraph<PONode, POEdge> {

	private Set<PONode> nodes;
	private Set<POEdge> edges;
	private String label;
	private int trace;

	public PartialOrderGraph(String label, int trace) {
		this.label = label;
		this.trace = trace;
		initLayout();

		nodes = new HashSet<PONode>();
		edges = new HashSet<POEdge>();
	}

	private void initLayout() {
		getAttributeMap().put(AttributeMap.LABEL, label);
		getAttributeMap().put(AttributeMap.PREF_ORIENTATION, SwingConstants.WEST);
	}

	public Set<PONode> getNodes() {
		return nodes;
	}

	public Set<POEdge> getEdges() {
		return edges;
	}

	/**
	 * Adds a node to the graph.
	 * 
	 * @param node
	 *            The node to add.
	 * @return
	 */
	public boolean addNode(PONode node) {
		boolean b = nodes.add(node);
		graphElementAdded(node);
		return b;
	}

	/**
	 * Adds an edge to this graph.
	 * 
	 * @param edge
	 *            The edge to add.
	 */
	public void addEdge(POEdge edge) {
		edges.add(edge);
		graphElementAdded(edge);
	}

	/**
	 * remove the node from the graph
	 */
	public void removeNode(DirectedGraphNode node) {
		if (node instanceof PONode) {
			removeNodeFromCollection(nodes, (PONode) node);
			nodes.remove(node);
		}
	}

	protected AbstractDirectedGraph<PONode, POEdge> getEmptyClone() {
		return new PartialOrderGraph(label, trace);
	}

	/**
	 * remove the node from the graph
	 */
	protected Map<? extends DirectedGraphElement, ? extends DirectedGraphElement> cloneFrom(
			DirectedGraph<PONode, POEdge> graph) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * remove the edge from the graph
	 */
	@SuppressWarnings("rawtypes")
	public void removeEdge(DirectedGraphEdge edge) {
		if (edge instanceof POEdge) {
			removeNodeFromCollection(edges, (POEdge) edge);
			edges.remove(edge);
		}

	}

	public int getTrace() {
		return trace;
	}

	public void setTrace(int trace) {
		this.trace = trace;
	}

	public POEdge getEdge(PONode source, PONode target) {
		for (POEdge e : this.getInEdges(target)) {
			if (e.getSource().equals(source)) {
				return e;
			}
		}
		return null;
	}

	public void removeEdges() {
		POEdge[] allEdges = edges.toArray(new POEdge[edges.size()]);
		for (POEdge e : allEdges) {
			removeEdge(e);
		}

	}

	public PONode getNode(String source) {
		for (PONode n : nodes) {
			if (n instanceof POModelMoveVisibleNode) {
				if (((POModelMoveVisibleNode) n).getTransition().getLabel().equals(source)) {
					return n;
				}

			} else {
				if (XConceptExtension.instance().extractName(((PONodeMove) n).getEvent()).equals(source)) {
					return n;
				}
			}
		}
		return null;
	}

	public PONode getNode(XEvent source) {
		for (PONode n : nodes) {
			if (n instanceof PONodeMove) {
				XEvent e = ((PONodeMove) n).getEvent();
				if (e != null) {
					XID graphNodeId = XIdentityExtension.instance().extractID(e);
					XID eventId = XIdentityExtension.instance().extractID(source);
					if (graphNodeId.equals(eventId)) {
						return n;
					}
				}

			}
		}
		return null;
	}

	public PONodeMove getNode(int stepIndex) {
		for (PONode n : nodes) {
			if (n instanceof PONodeMove) {
				int nodeStepIndex = ((PONodeMove) n).getStepIndex();
				if (nodeStepIndex == stepIndex) {
					return (PONodeMove) n;
				}
			}
		}
		return null;
	}

	public void retainEdges(Collection<POEdge> ddeps) {
		POEdge[] allEdges = edges.toArray(new POEdge[edges.size()]);
		for (POEdge e : allEdges) {
			if (!ddeps.contains(e)) {
				removeEdge(e);
			}
		}
	}

	public void removeEdges(Collection<POEdge> edges) {
		for (POEdge e : edges) {
			removeEdge(e);
		}
	}
}
