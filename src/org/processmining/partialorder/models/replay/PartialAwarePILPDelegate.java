package org.processmining.partialorder.models.replay;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import nl.tue.astar.util.PartiallyOrderedTrace;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.model.XLog;
import org.processmining.models.graphbased.directed.petrinet.InhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.ResetNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.partialorder.ptrace.model.PTrace;
import org.processmining.plugins.astar.petrinet.impl.PILPDelegate;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;

public class PartialAwarePILPDelegate extends PILPDelegate {

	public PartialAwarePILPDelegate(Petrinet net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost, int delta, int threads,
			Marking... set) {
		super((PetrinetGraph) net, log, classes, map, mapTrans2Cost, mapEvClass2Cost,
				new HashMap<Transition, Integer>(0), delta, threads, set);
	}

	public PartialAwarePILPDelegate(ResetNet net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost, int delta, int threads,
			Marking... set) {
		super((PetrinetGraph) net, log, classes, map, mapTrans2Cost, mapEvClass2Cost,
				new HashMap<Transition, Integer>(0), delta, threads, set);
	}

	public PartialAwarePILPDelegate(InhibitorNet net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost, int delta, int threads,
			Marking... set) {
		super((PetrinetGraph) net, log, classes, map, mapTrans2Cost, mapEvClass2Cost,
				new HashMap<Transition, Integer>(0), delta, threads, set);
	}

	public PartialAwarePILPDelegate(ResetInhibitorNet net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost, int delta, int threads,
			Marking... set) {
		super((PetrinetGraph) net, log, classes, map, mapTrans2Cost, mapEvClass2Cost,
				new HashMap<Transition, Integer>(0), delta, threads, set);
	}

	/**
	 * The following constructors accept mapping from sync moves to cost
	 */

	public PartialAwarePILPDelegate(Petrinet net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost,
			Map<Transition, Integer> mapSync2Cost, int delta, int threads, Marking... set) {
		super((PetrinetGraph) net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, mapSync2Cost, delta, threads, set);
	}

	public PartialAwarePILPDelegate(ResetNet net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost,
			Map<Transition, Integer> mapSync2Cost, int delta, int threads, Marking... set) {
		super((PetrinetGraph) net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, mapSync2Cost, delta, threads, set);
	}

	public PartialAwarePILPDelegate(InhibitorNet net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost,
			Map<Transition, Integer> mapSync2Cost, int delta, int threads, Marking... set) {
		super((PetrinetGraph) net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, mapSync2Cost, delta, threads, set);
	}

	public PartialAwarePILPDelegate(ResetInhibitorNet net, XLog log, XEventClasses classes, TransEvClassMapping map,
			Map<Transition, Integer> mapTrans2Cost, Map<XEventClass, Integer> mapEvClass2Cost,
			Map<Transition, Integer> mapSync2Cost, int delta, int threads, Marking... set) {
		super((PetrinetGraph) net, log, classes, map, mapTrans2Cost, mapEvClass2Cost, mapSync2Cost, delta, threads, set);
	}
	
	
	/* 
	 * 
	 * XXA: each trace index: a mapping : event index in filtered trace -> event index in original trace
	 * 
	 */
	protected final TIntObjectMap<TIntIntMap> t2filtEIndex2orgEIndex = 
			new TIntObjectHashMap<TIntIntMap>();
	
	protected final TIntObjectMap<PartiallyOrderedTrace> t2OrigPartTrace = 
			new TIntObjectHashMap<PartiallyOrderedTrace>();
	protected final TIntObjectMap<PTrace> t2POXTrace = 
			new TIntObjectHashMap<PTrace>();

	public void putFilteredEventToOrgEventIndex(int trace, int indexEventInFilteredTrace, int indexEventInOrgTrace) {
		if(!t2filtEIndex2orgEIndex.containsKey(trace)){
			t2filtEIndex2orgEIndex.put(trace, new TIntIntHashMap());
		}
		t2filtEIndex2orgEIndex.get(trace).put(indexEventInFilteredTrace, indexEventInOrgTrace);
	}
	

	public int getTraceOriginalEventIndex(int traceIndex, int filteredEventIndex) {
		if(t2filtEIndex2orgEIndex.containsKey(traceIndex) 
				&& t2filtEIndex2orgEIndex.get(traceIndex).containsKey(filteredEventIndex)){
				return t2filtEIndex2orgEIndex.get(traceIndex).get(filteredEventIndex);
			
		}
		return -1;
	}
	


	public void putTraceToOrgPartialTrace(int trace, PartiallyOrderedTrace original) {
		
		t2OrigPartTrace.put(trace, original);
	}
	
	public PartiallyOrderedTrace getOrgPartialTrace(int trace) {
		if(t2OrigPartTrace.containsKey(trace)){
			return t2OrigPartTrace.get(trace);
		} 
		return null;
	}
	
	public Collection<PartiallyOrderedTrace> getOrgPartialTraces() {
		//if(t2OrigPartTrace.containsKey(trace)){
			return t2OrigPartTrace.valueCollection();
		//} 
		//return null;
	}
	

	public void putTraceToPOXTrace(int trace, PTrace poXTrace) {
		t2POXTrace.put(trace, poXTrace);
		
	}
	

	public PTrace getOrgPartialXTrace(int i) {
		if(t2POXTrace.containsKey(i)){
			return t2POXTrace.get(i);
		}
		return null;
	}
	
	/* 
	 * end XXA =======================================================================================
	 * 
	 */




}
