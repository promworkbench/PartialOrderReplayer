package org.processmining.partialorder.ptrace.model.imp;

import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import nl.tue.astar.util.PartiallyOrderedTrace;

import org.deckfour.xes.model.XTrace;
import org.processmining.partialorder.models.dependency.PDependencyDataAware;
import org.processmining.partialorder.models.dependency.PDependency;
import org.processmining.partialorder.ptrace.model.PTrace;
import org.processmining.partialorder.util.PartialUtil;

public class PTraceExtendedProjectionImp extends PTraceExtendedAbstract {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3458880632231080765L;

	public PTraceExtendedProjectionImp(XTrace trace, int traceIndex) {
		super(trace, traceIndex);
	}

	public PTraceExtendedProjectionImp(PTrace pTrace) {
		super(pTrace);
	}

	protected PartiallyOrderedTrace getPartiallyOrderedTrace(TIntIntMap org2new, boolean filterNoMoves, boolean sorted) {
		//TIntIntMap org2new = new TIntIntHashMap();
	
		TIntList activities = new TIntArrayList();
		List<int[]> predecessors = new ArrayList<int[]>();
		
		UnweightedShortestPath<Integer, PDependency> dist = new UnweightedShortestPath<Integer, PDependency>(this);
		
		DirectedSparseGraph<Integer, PDependency> graph = new DirectedSparseGraph<Integer, PDependency>();
		
		for(Integer i : this.getVertices()){
			if (isMove[i] || !filterNoMoves) {
				graph.addVertex(i);
			}
		}
		for(Integer source : graph.getVertices()){
			for(Integer target: graph.getVertices()){
				if(dist.getDistance(source, target) != null){
					graph.addEdge(new PDependencyDataAware(source, target), source, target);
				} 
			}
		}
		
		Collection<PDependency> edges = new ArrayList<>(graph.getEdges());
		for(PDependency e : edges){
			Integer source = graph.getSource(e);
			Integer target = graph.getDest(e);
			graph.removeEdge(e);
			UnweightedShortestPath<Integer, PDependency> newdist = new UnweightedShortestPath<Integer, PDependency>(graph);
			if(newdist.getDistance(source, target) == null){
				graph.addEdge(e, source, target);
			} 		
		}		
		
		
		
		
		int newi = 0;
		for (int i = 0; i < this.size(); i++) {
			// Add only events that have visible activity if filterNoMoves
			if (graph.getVertices().contains(i)) {

				activities.add(index2act[i]);
				org2new.put(i, newi++);

				// Fill pre with all predecessors 

				TIntList pre = new TIntArrayList();
				for(PDependency dep : graph.getInEdges(i)){
					pre.add(org2new.get(dep.getSource()));
				}

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


}
