package org.processmining.partialorder.ptrace.model.imp;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.tue.astar.util.PartiallyOrderedTrace;

import org.deckfour.xes.model.XTrace;
import org.processmining.partialorder.models.dependency.PDependency;
import org.processmining.partialorder.ptrace.model.PTrace;
import org.processmining.partialorder.ptrace.model.PTraceExtended;
import org.processmining.partialorder.util.PartialUtil;

public class PTraceExtendedRecursionImp extends PTraceExtendedAbstract implements PTraceExtended {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8390457850892668691L;

	public PTraceExtendedRecursionImp(XTrace trace, int traceIndex) {
		super(trace, traceIndex);
	}

	public PTraceExtendedRecursionImp(PTrace pTrace) {
		super(pTrace);
	}

	protected PartiallyOrderedTrace getPartiallyOrderedTrace(TIntIntMap org2new, boolean filterNoMoves, boolean sorted) {
		//TIntIntMap org2new = new TIntIntHashMap();
		int newi = 0;

		TIntList activities = new TIntArrayList();
		List<int[]> predecessors = new ArrayList<int[]>();

		for (int i = 0; i < this.size(); i++) {
			// Add only events that have visible activity if filterNoMoves
			if (isMove[i] || !filterNoMoves) {
				TIntList pre = new TIntArrayList();

				activities.add(index2act[i]);
				org2new.put(i, newi++);

				// Fill pre with all predecessors 
				getPreds(pre, i, org2new, filterNoMoves);
				int[] preArray = pre.toArray();
				Arrays.sort(preArray);
				predecessors.add(preArray);

			} else {
				org2new.put(i, -1);
			}
		}

		PartiallyOrderedTrace potrace = new PartiallyOrderedTrace(PartialUtil.getPOTraceName(trace, traceIndex),
				activities.toArray(), predecessors.toArray(new int[0][]));

		if (sorted) {
			potrace = getSortedPartiallyOrderedTrace(potrace, org2new);

		}

		return potrace;
	}

	/**
	 * Get the first is-move ancestors of the predecessor
	 */
	private void getPreds(TIntList pre, int i, TIntIntMap org2new, boolean filterNoMoves) {
		for (PDependency relation : this.getInEdges(i)) {
			if (relation.isDirect()) {
				int predecessor = relation.getSource();
				if (isMove[predecessor] || !filterNoMoves) {
					int newi = org2new.get(predecessor);
					if (!pre.contains(newi)) {
						pre.add(newi);
					}
				} else {
					//for(DataAwareRelation prerelation : this.getInEdges(predecessor)){
					getPreds(pre, predecessor, org2new, filterNoMoves);
					//}
				}
			}
		}
	}

}
