package org.processmining.partialorder.ptrace.model.imp;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nl.tue.astar.util.PartiallyOrderedTrace;

import org.deckfour.xes.model.XTrace;
import org.processmining.partialorder.models.dependency.PDependency;
import org.processmining.partialorder.ptrace.model.PTrace;
import org.processmining.partialorder.ptrace.model.PTraceExtended;
import org.processmining.partialorder.util.PartialUtil;

public abstract class PTraceExtendedAbstract extends PTraceImp implements PTraceExtended {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8693888525236913537L;
	protected boolean[] isMove;
	protected int[] index2act;

	public PTraceExtendedAbstract(XTrace trace, int traceIndex) {
		super(trace, traceIndex);
		isMove = new boolean[trace.size()];
		index2act = new int[trace.size()];
	}

	public PTraceExtendedAbstract(PTrace ptrace) {
		this(ptrace.getTrace(), ptrace.getTraceIndex());
		for (Integer i : ptrace.getEventIndices()) {
			this.addVertex(i);
		}
		for (PDependency dep : ptrace.getDependencies()) {
			this.addDependency(dep, dep.getSource(), dep.getTarget());
		}
	}

	public boolean addEvent(int index, int act, boolean isVisible) {
		isMove[index] = isVisible;
		index2act[index] = act;
		return this.addVertex(index);
	}

	public void setEventActivity(Integer eventIndex, int act, boolean b) {
		if (this.getEventIndices().contains(eventIndex)) {
			isMove[eventIndex] = b;
			index2act[eventIndex] = act;
		}
	}

	/**
	 * Get a filtered po-trace in PartiallyOrderedTrace for A*-Alignment
	 * implementation
	 * 
	 * @param org2new
	 *            A (empty) map that is going to be filled in this function from
	 *            the original index of an event to the new index in the
	 *            returned po-trace
	 * @param sorted
	 *            If true, then sort the to-be-returned po-trace (for better
	 *            comparison)
	 * @return A po-trace.
	 */
	public PartiallyOrderedTrace getFilteredPTrace(TIntIntMap org2new, boolean sorted) {
		PartiallyOrderedTrace po = getPartiallyOrderedTrace(org2new, true, sorted);
		return po;
	}

	protected abstract PartiallyOrderedTrace getPartiallyOrderedTrace(TIntIntMap org2new, boolean filterNoMoves,
			boolean sorted);
	
	
	
	
	/**
	 * Get a sorted potrace and update the org2new mapping
	 * 
	 * @param potrace
	 *            The po-trace to be sorted and returned as a sorted potrace
	 * @param org2new
	 *            The mapping from original event index to new index in potrace
	 *            (updated)
	 * @return
	 */
	public PartiallyOrderedTrace getSortedPartiallyOrderedTrace(PartiallyOrderedTrace potrace, TIntIntMap org2new) {

		// At new predecessors[i] is the old predecessors[indeces.get(i)]
		// i.o.w. : i is the new index whereas indeces.get(i) is the old index of activity
		List<Integer> indeces = new ArrayList<Integer>();
		for (int i = 0; i < potrace.getSize(); i++) {
			indeces.add(i);
		}
		// Sort the indices based on the predecessor length and lexicographical ordering.
		Collections.sort(indeces, new PredecessorsComparator(potrace));
		// Then sort again based on the activity number
		Collections.sort(indeces, new ActivityComparator(potrace));

		// Update the predecessors with the new indices.
		int[][] newMyPredecessors = new int[indeces.size()][];
		for (int i = 0; i < indeces.size(); i++) {
			int oldIndex = indeces.get(i);
			int[] pred = new int[potrace.getPredecessors(oldIndex).length];
			for (int j = 0; j < pred.length; j++) {
				pred[j] = indeces.indexOf(potrace.getPredecessors(oldIndex)[j]);
			}
			Arrays.sort(pred);
			newMyPredecessors[i] = pred;
		}
		// Update activities and the org2new mapping
		int[] sortedActs = new int[potrace.getSize()];
		// For each org index, the current org2new.get(org) is the index in the unsorted po-trace
		for (int org : org2new.keys()) {
			int unsortedIndex = org2new.get(org);
			if (unsortedIndex != -1) {
				/*
				 * The unsortedIndex corresponds to a value (old index) in the
				 * array indices whereas the new index of org is the index of
				 * this old index in indices.
				 */
				for (int i = 0; i < indeces.size(); i++) {
					if (indeces.get(i) == unsortedIndex) {
						org2new.put(org, i);
						sortedActs[i] = potrace.get(unsortedIndex);
						//System.out.println("UPDATE sorted index [o: "+org + ",uns:" + unsortedIndex + ",s:" +i);
					}
				}
			}
		}

		return new PartiallyOrderedTrace(PartialUtil.getPOTraceName(trace, traceIndex), sortedActs, newMyPredecessors);
	}

	/**
	 * Get the unfiltered po-trace
	 * 
	 * @return a po-trace
	 */
	public PartiallyOrderedTrace getOriginalPTrace() {
		return getPartiallyOrderedTrace(new TIntIntHashMap(), false, false);
	}

	/**
	 * Get the sorted, unfiltered po-trace
	 * 
	 * @return a po-trace
	 */
	public PartiallyOrderedTrace getOriginalSortedPOrace() {
		return getPartiallyOrderedTrace(new TIntIntHashMap(), false, true);
	}

	
	
	
	

	/*
	 * The ActivityComparator is used to sort activities based on the activity
	 * integer of ILP delegate
	 */
	protected class ActivityComparator implements Comparator<Integer> {
		private final PartiallyOrderedTrace trace;

		public ActivityComparator(PartiallyOrderedTrace poTrace) {
			this.trace = poTrace;
		}

		@Override
		public int compare(Integer i1, Integer i2) {
			return trace.get(i1) - trace.get(i2);
		}

	}

	/*
	 * The PredecessorsComparator is used to sort the predecessors based on (1)
	 * the length (2) the lexico-graphical ordering of predecessor indices
	 */
	protected class PredecessorsComparator implements Comparator<Integer> {
		private PartiallyOrderedTrace trace;

		public PredecessorsComparator(PartiallyOrderedTrace poTrace) {
			this.trace = poTrace;
		}

		@Override
		public int compare(Integer i1, Integer i2) {
			if (trace.getPredecessors(i1).length != trace.getPredecessors(i2).length) {
				return trace.getPredecessors(i1).length - trace.getPredecessors(i2).length;
			}
			for (int j = 0; j < trace.getPredecessors(i1).length; j++) {
				if (trace.getPredecessors(i1)[j] != trace.getPredecessors(i2)[j]) {
					return trace.getPredecessors(i1)[j] - trace.getPredecessors(i2)[j];
				}
			}
			return 0;
		}

	}
}
