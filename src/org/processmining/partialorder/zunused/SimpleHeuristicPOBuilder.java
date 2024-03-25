package org.processmining.partialorder.zunused;
//package org.processmining.partialorder.plugins.replay.pobuilder;
//
//import gnu.trove.list.TIntList;
//import gnu.trove.list.array.TIntArrayList;
//import gnu.trove.map.TIntIntMap;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map.Entry;
//
//import nl.tue.astar.AStarThread;
//import nl.tue.astar.util.PartiallyOrderedTrace;
//
//import org.deckfour.xes.extension.std.XConceptExtension;
//import org.deckfour.xes.model.XAttribute;
//import org.deckfour.xes.model.XAttributeMap;
//import org.deckfour.xes.model.XEvent;
//import org.deckfour.xes.model.XLog;
//import org.processmining.partialorder.models.alignment.PartialAwarePILPDelegate;
//import org.processmining.partialorder.models.extension.XDataExtension;
//import org.processmining.plugins.astar.petrinet.PartialOrderBuilder;
//import org.processmining.plugins.astar.petrinet.impl.AbstractPDelegate;
//
//public class SimpleHeuristicPOBuilder implements PartialOrderBuilder {
//
//	public PartiallyOrderedTrace getPartiallyOrderedTrace(XLog log, int trace, AbstractPDelegate<?> delegate,
//			TIntList unUsedIndices, TIntIntMap trace2orgTrace) {
//		PartialAwarePILPDelegate paDelegate = (PartialAwarePILPDelegate) delegate;
//		
//		
//		int traceSize = log.get(trace).size();
//		String traceName = XConceptExtension.instance().extractName(log.get(trace));
//		if (traceName == null || traceName.isEmpty()) {
//			traceName = "Trace " + trace;
//		}
//		
//		// all activities
//		TIntList activities = new TIntArrayList(traceSize);
//		TIntList orgActivities = new TIntArrayList(traceSize);
//		
//		// 1. build the filtered linear trace and 
//		// a mapping from an event index in the filtered trace to original trace
//		TIntList originalTrace = new TIntArrayList();
//		TIntList filteredTrace = new TIntArrayList();
//		for (int i = 0; i < traceSize; i++) {
//			originalTrace.add(i);
//			orgActivities.add(delegate.getActivityOf(trace, i));
//			// activity i of trace
//			int act = delegate.getActivityOf(trace, i);
//			if (act != AStarThread.NOMOVE) {
//				
//				activities.add(act);
//				filteredTrace.add(i);
//				/*
//				 *  XXA**TODO
//				 */
//				paDelegate.putFilteredEventToOrgEventIndex(trace, activities.lastIndexOf(act), i);
//				/* 
//				 * XXA end
//				 */
//			}
//		}
//		
//		// predecessors[i] holds all predecessors of event at index i
//		List<int[]> predecessors = new ArrayList<int[]>();
//		buildPartialTrace(log, trace, filteredTrace, predecessors);
//		
//		// record original partially ordered trace
//		List<int[]> orgPredecessors = new ArrayList<int[]>();
//		buildPartialTrace(log, trace, originalTrace, orgPredecessors);
//		PartiallyOrderedTrace original = new PartiallyOrderedTrace(traceName, orgActivities.toArray(), orgPredecessors.toArray(new int[0][]));
//		
//		
//		paDelegate.putTraceToOrgPartialTrace(trace, original);
//			
//		PartiallyOrderedTrace result;
//		// predecessors[i] holds all predecessors of event at index i
//		result = new PartiallyOrderedTrace(traceName, activities.toArray(), predecessors.toArray(new int[0][]));
//		return result;
//	}
//
//
//
//	private void buildPartialTrace(XLog log, int traceIndex, TIntList trace, List<int[]> predecessors) {
//		
//		for (int i = 0; i < trace.size(); i++) {
//					
//				
//			// first event
//			if(i == 0){
//				predecessors.add(null);
//				continue;
//			}			
//			int orgIndex = trace.get(i);
//			XEvent event = log.get(traceIndex).get(orgIndex);
//			XAttributeMap eventInputAttr = XDataExtension.instance().extractInputAttributes(event);
//			
//			// for each event i, init pre which is the list of event i predecessors
//			TIntList pre = new TIntArrayList();
//			
//			// for each event pi before i, if pi.output and i.input != empty, then pi is predecessor of i
//			for (int pi = 0; pi < i; pi++ ){
//				
//				int orgPreIndex = trace.get(pi);
//				XEvent preEvent = log.get(traceIndex).get(orgPreIndex);
//				XAttributeMap preEventOutputAttr = XDataExtension.instance().extractOutputAttributes(preEvent);
//				
//				if (areOverlapping(preEventOutputAttr, eventInputAttr)) {
//					pre.add(pi);
//				} 
//			}			
//			if(pre.size() == 0){
//				predecessors.add(null);
//			} else {
//				predecessors.add(pre.toArray());
//			}
//		
//		}
//		
//	}
//
//	private boolean areOverlapping(XAttributeMap preEventOutputAttr, XAttributeMap eventInputAttr) {
//		if(preEventOutputAttr == null) {
//			return false;
//		}
//		if(eventInputAttr == null) {
//			return false;
//		}
//		for (Entry<String, XAttribute> outEntry : preEventOutputAttr.entrySet()){
//			String attrKey = outEntry.getKey();
//			if(!eventInputAttr.containsKey(attrKey)){
//				continue;
//			} else {
//				System.out.println("Share " + attrKey);
//				XAttribute outAttr = outEntry.getValue();
//				XAttribute inAttr = eventInputAttr.get(attrKey);
//				if(outAttr.equals(inAttr)){
//					System.out.println("	overlap in value ");
//					return true;
//				}
//				System.out.println("	NOT overlap in value ");
//			}
//			
//		}
//		return false;
//	}
//
//}
