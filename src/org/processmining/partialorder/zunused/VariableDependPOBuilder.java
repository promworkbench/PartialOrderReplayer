package org.processmining.partialorder.zunused;
//package org.processmining.partialorder.plugins.replay.pobuilder;
//
//import gnu.trove.list.TIntList;
//import gnu.trove.list.array.TIntArrayList;
//import gnu.trove.map.TIntIntMap;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import nl.tue.astar.AStarThread;
//import nl.tue.astar.util.PartiallyOrderedTrace;
//
//import org.deckfour.xes.extension.std.XConceptExtension;
//import org.deckfour.xes.extension.std.XTimeExtension;
//import org.deckfour.xes.model.XAttributeMap;
//import org.deckfour.xes.model.XEvent;
//import org.deckfour.xes.model.XLog;
//import org.processmining.partialorder.confs.PNPartialOrderBuilderParameter;
//import org.processmining.partialorder.confs.PNPartialOrderBuilderParameter.EnumLevelDependent;
//import org.processmining.partialorder.models.alignment.PartialAwarePILPDelegate;
//import org.processmining.partialorder.models.extension.XDataExtension;
//import org.processmining.plugins.astar.petrinet.PartialOrderBuilder;
//import org.processmining.plugins.astar.petrinet.impl.AbstractPDelegate;
//
//public class VariableDependPOBuilder implements PartialOrderBuilder {
//
//	private PNPartialOrderBuilderParameter parameter;
//
//	public VariableDependPOBuilder(PNPartialOrderBuilderParameter parameter) {
//		this.parameter = parameter;
//	}
//
//	public PartiallyOrderedTrace getPartiallyOrderedTrace(XLog log, int trace, AbstractPDelegate<?> delegate,
//			TIntList unUsedIndices, TIntIntMap trace2orgTrace) {
//		PartialAwarePILPDelegate paDelegate = (PartialAwarePILPDelegate) delegate;
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
//				 * XXA**TODO
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
//		// record original partially ordered trace
//		List<int[]> orgPredecessors = new ArrayList<int[]>();
//
//		if (true) {
//			buildPartialTraceLinearSameTime(log, trace, filteredTrace, predecessors);
//			buildPartialTraceLinearSameTime(log, trace, originalTrace, orgPredecessors);
//		} else {
//			buildPartialTrace(log, trace, filteredTrace, predecessors);
//			buildPartialTrace(log, trace, originalTrace, orgPredecessors);
//		}
//		PartiallyOrderedTrace original = new PartiallyOrderedTrace(traceName, orgActivities.toArray(),
//				orgPredecessors.toArray(new int[0][]));
//
//		paDelegate.putTraceToOrgPartialTrace(trace, original);
//
//		PartiallyOrderedTrace result;
//		// predecessors[i] holds all predecessors of event at index i
//		result = new PartiallyOrderedTrace(traceName, activities.toArray(), predecessors.toArray(new int[0][]));
//		return result;
//	}
//
//	private void buildPartialTrace(XLog log, int traceIndex, TIntList trace, List<int[]> predecessors) {
//		for (int i = 0; i < trace.size(); i++) {
//
//			// first event
//			if (i == 0) {
//				predecessors.add(null);
//				continue;
//			}
//			int orgIndex = trace.get(i);
//			XEvent event = log.get(traceIndex).get(orgIndex);
//			XAttributeMap eventInputAttr = XDataExtension.instance().extractInputAttributes(event);
//			XAttributeMap eventOutputAttr = XDataExtension.instance().extractOutputAttributes(event);
//
//			// for each event i, init pre which is the list of event i predecessors
//			TIntList pre = new TIntArrayList();
//
//			// for each event pi before i, if pi.output and i.input != empty, then pi is predecessor of i
//			for (int pi = 0; pi < i; pi++) {
//
//				int orgPreIndex = trace.get(pi);
//				XEvent preEvent = log.get(traceIndex).get(orgPreIndex);
//				XAttributeMap preEventOutputAttr = XDataExtension.instance().extractOutputAttributes(preEvent);
//				XAttributeMap preEventInputAttr = XDataExtension.instance().extractInputAttributes(preEvent);
//
//				if (usedSameDataElement(preEventInputAttr, preEventOutputAttr, eventInputAttr, eventOutputAttr)) {
//					pre.add(pi);
//				}
//			}
//			if (pre.size() == 0) {
//				predecessors.add(null);
//			} else {
//				predecessors.add(pre.toArray());
//			}
//		}
//	}
//
//	private void buildPartialTraceLinearSameTime(XLog log, int traceIndex, TIntList trace, List<int[]> predecessors) {
//
//		Date lastTime = null;
//		TIntList pre = new TIntArrayList();
//		int previousIndex = -1;
//
//		for (int i = 0; i < trace.size(); i++) {
//			int orgIndex = trace.get(i);
//			XEvent event = log.get(traceIndex).get(orgIndex);
//
//			Date timestamp = XTimeExtension.instance().extractTimestamp(event);
//
//			if (lastTime == null) {
//				// first event
//				predecessors.add(null);
//			} else if (!parameter.isAreSameTimestampesParallel()) {
//				predecessors.add(new int[] { i - 1 });
//
//			} else if (timestamp.equals(lastTime)) {
//				// timestamp is the same as the last event.
//				if (previousIndex >= 0) {
//					predecessors.add(new int[] { previousIndex });
//				} else {
//					predecessors.add(null);
//				}
//			} else {
//				// timestamp is different from the last event.
//				predecessors.add(pre.toArray());
//				previousIndex = i;
//				pre = new TIntArrayList();
//			}
//			pre.add(i);
//			lastTime = timestamp;
//
//		}
//
//	}
//
//	//  (I) events share attribute decision
//	//	(1) input	Decision	TRUE	 =>	output	Decision	TRUE	Different in meaning if swapped
//	//	(1) input	Decision	FALSE	 =>	output	Decision	TRUE	Different in meaning & result if swapped 
//	//	(2) input	Decision	TRUE	 ||	input	Decision	TRUE	No difference based on this attribute
//	//	(2) input	Decision	FALSE =>/||	input	Decision	TRUE	Different in result if swapped
//	//	(3) output	Decision	TRUE	 =>	input	Decision	TRUE	Different in meaning if swapped
//	//	(3) output	Decision	FALSE	 =>	input	Decision	TRUE	Different in meaning & result if swapped
//	//	(4) output	Decision	TRUE	 ||	output	Decision	TRUE	No difference based on this attribute
//	//	(4) output	Decision	FALSE =>/||	output	Decision	TRUE	Different in result if swapped
//
//	//  (II) events do not share any attributes but share dependent attributes
//	//  e.g. verification (true) <=> decision (true)
//	//  [idea] use correlations between attributes => 
//	//  e1: output-Verification => e2:output-Decision. 
//
//	private boolean usedSameDataElement(XAttributeMap e1i, XAttributeMap e1o, XAttributeMap e2i, XAttributeMap e2o) {
//		if (e1i == null && e1o == null && e2i == null && e2o == null) {
//			return false;
//		}
//		// Case (1)
//		Set<String> sharedKeys = new HashSet<String>();
//		if (shareKeys(e1i, e2o, sharedKeys)) {
//			return true;
//		}
//		// Case (2)
//		sharedKeys.clear();
//		if (shareKeys(e1i, e2i, sharedKeys)) {
//			if (parameter.getUncertaintyLevel().equals(EnumLevelDependent.DATA_VALUE_DEPENDENT)
//					&& haveDifferentValue(e1i, e2i, sharedKeys)) {
//				return true;
//			}
//		}
//		// Case (3)
//		sharedKeys.clear();
//		if (shareKeys(e1o, e2i, sharedKeys)) {
//			return true;
//		}
//		// Case (4)
//		sharedKeys.clear();
//		if (shareKeys(e1o, e2o, sharedKeys)) {
//			if (parameter.getUncertaintyLevel().equals(EnumLevelDependent.DATA_VALUE_DEPENDENT)
//					&& haveDifferentValue(e1o, e2o, sharedKeys)) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	// (True <=> there is a key, e1.get(key) =/= e2.get(key)) => dependency
//	private boolean haveDifferentValue(XAttributeMap e1, XAttributeMap e2, Set<String> sharedKeys) {
//		for (String key : sharedKeys) {
//			if (!e1.get(key).equals(e2.get(key))) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	private boolean shareKeys(XAttributeMap e1, XAttributeMap e2, Set<String> sharedKeys) {
//		if (e1 == null || e2 == null) {
//			return false;
//		}
//		for (String key : e1.keySet()) {
//			if (e2.containsKey(key)) {
//				sharedKeys.add(key);
//			}
//		}
//		return sharedKeys.size() > 0;
//	}
//
//}
