package org.processmining.partialorder.ptrace.model.imp;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.processmining.partialorder.models.dependency.PDependency;
import org.processmining.partialorder.ptrace.model.PTrace;

import edu.uci.ics.jung.graph.DirectedSparseGraph;

/**
 * A graphical data model of the partially ordered trace. Information stored in
 * this class includes (1) the original linear trace, (2) the trace index in the
 * log (3) a list of boolean indicating the event is a visible move (or not),
 * and (4) a list of activity number according to the delegate.
 * <p/>
 * The class has functions to get a (un)sorted and (un)filtered potrace in
 * PartiallyOrderedTrace for A* alignment
 * 
 * @author xlu
 * 
 */
public class PTraceImp extends DirectedSparseGraph<Integer, PDependency> implements PTrace {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8185758290517552207L;
	protected XTrace trace;
	protected int traceIndex;

	/**
	 * Constructor to get a new PartiallyOrderedXTrace graph with empty nodes
	 * and edges
	 * 
	 * @param trace
	 *            The linear XTrace of this partially ordered trace
	 * @param traceIndex
	 *            The index of the linear XTrace in the log
	 */
	public PTraceImp(XTrace trace, int traceIndex) {
		this.trace = trace;
		this.traceIndex = traceIndex;
	}

	
	public PDependency getDependency(int source, int target) {
		for (PDependency e : this.getInEdges(target)) {
			if (e.getSource() == source) {
				return e;
			}
		}
		return null;
	}

	
	public Set<Integer> getStartEventIndices() {
		Set<Integer> result = new HashSet<Integer>();
		for (Integer i : this.getVertices()) {
			if (this.getInEdges(i).isEmpty()) {
				result.add(i);
			}
		}
		return result;
	}

	
	public Set<Integer> getEndEventIndices() {
		Set<Integer> result = new HashSet<Integer>();
		for (Integer i : this.getVertices()) {
			if (this.getOutEdges(i).isEmpty()) {
				result.add(i);
			}
		}
		return result;
	}

	public boolean addEvent(int eventIndex) {
		return this.addVertex(eventIndex);
	}

	
	public XTrace getTrace() {
		return trace;
	}

	
	public int getTraceIndex() {
		return traceIndex;
	}

	public void setTraceIndex(int i) {
		traceIndex = i;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("[");
		for (Integer i : this.getVertices()) {
			s.append(i + ",");
		}
		s.append("] [");
		for (PDependency e : this.getEdges()) {
			s.append("(" + e.getSource() + "," + e.getTarget() + ")");
		}

		return s.toString();
	}

	public Collection<Integer> getEventIndices() {
		return this.getVertices();
	}

	public Collection<PDependency> getDependencies() {
		return this.getEdges();
	}

	public int size() {
		return this.vertices.size();
	}

	public boolean addDependency(PDependency relation, int sourceEventIndex, int targetEventIndex) {
		return this.addEdge(relation, sourceEventIndex, targetEventIndex);
	}

	public XEvent getEvent(int index) {
		return this.getTrace().get(index);
	}

	public Iterator<XEvent> iterator() {
		return this.trace.iterator();
	}

	public Collection<Integer> getPredecessorIndices(int index) {
		Set<Integer> preds = new HashSet<Integer>();
		if (this.getVertices().contains(index)) {
			for (PDependency in : this.getInEdges(index)) {
				preds.add(in.getSource());
			}
		}
		return preds;
	}

	public Collection<Integer> getSuccessorIndices(int index) {
		Set<Integer> succs = new HashSet<Integer>();
		if (this.getVertices().contains(index)) {
			for (PDependency out : this.getOutEdges(index)) {
				succs.add(out.getTarget());
			}
		}
		return succs;
	}

}
