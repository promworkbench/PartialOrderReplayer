package org.processmining.partialorder.zunused;
//package org.processmining.partialorder.plugins.replay.pobuilder;
//
//import gnu.trove.list.TIntList;
//import gnu.trove.map.TIntIntMap;
//import gnu.trove.map.hash.TIntIntHashMap;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import nl.tue.astar.AStarThread;
//import nl.tue.astar.util.PartiallyOrderedTrace;
//
//import org.deckfour.xes.extension.std.XTimeExtension;
//import org.deckfour.xes.model.XEvent;
//import org.deckfour.xes.model.XLog;
//import org.deckfour.xes.model.XTrace;
//import org.processmining.partialorder.models.alignment.PartialAwarePILPDelegate;
//import org.processmining.partialorder.models.trace.DataAwareDependency;
//import org.processmining.partialorder.models.trace.DataAwareDependency.EnumDataDependency;
//import org.processmining.partialorder.models.trace.PTrace;
//import org.processmining.partialorder.models.trace.PTraceImp;
//import org.processmining.plugins.astar.petrinet.PartialOrderBuilder;
//import org.processmining.plugins.astar.petrinet.impl.AbstractPDelegate;
//
//public class SequentialPOBuilder  implements PartialOrderBuilder {
//
//	public PartiallyOrderedTrace getPartiallyOrderedTrace(XLog log, int trace, AbstractPDelegate<?> delegate,
//			TIntList unUsedIndices, TIntIntMap trace2orgTrace) {
//		/* init */
//		PartialAwarePILPDelegate paDelegate = (PartialAwarePILPDelegate) delegate;
//		XTrace t = log.get(trace);
//		int traceSize = t.size();
//	
//		/* Create a po trace skeleton */
//		PTrace poXTrace = new PTraceImp(t, trace);
//
//
//		List<int[]> predecessors = new ArrayList<int[]>();
//
//		for (int i = 0; i < traceSize; i++) {
//			// activity i of trace
//			int pre = i-1;
//			XEvent currentEvent = t.get(i);
//			int currentAct = delegate.getActivityOf(trace, i);
//			//originalTrace.add(i);
//			//activities.add(act);
//
//			/* Add an event to the po trace */
//			poXTrace.addEvent(i, currentAct, currentAct != AStarThread.NOMOVE);
//
//			if (i > 0) {
//				XEvent predecessor = t.get(pre);
//				DataAwareDependency relation = new DataAwareDependency(pre, i);
//				relation.putDependency(EnumDataDependency.OO_DiffValue, XTimeExtension.KEY_TIMESTAMP, predecessor
//						.getAttributes().get(XTimeExtension.KEY_TIMESTAMP),
//						currentEvent.getAttributes().get(XTimeExtension.KEY_TIMESTAMP));
//				relation.setTransitiveClosure(true);
//				poXTrace.addDependency(relation, pre, i);
//	
//				predecessors.add(new int[]{pre});
//			}
//		}
//
//		paDelegate.putTraceToPOXTrace(trace, poXTrace);
//
//		TIntIntMap org2new = new TIntIntHashMap();
//		paDelegate.putTraceToOrgPartialTrace(trace, poXTrace.getOriginalPTrace());
//		org2new = new TIntIntHashMap();
//
//		PartiallyOrderedTrace result = poXTrace.getFilteredPTrace(org2new, true);
//		
//		for(int i : org2new.keys()){
//			if (org2new.get(i) >= 0){
//				paDelegate.putFilteredEventToOrgEventIndex(trace, org2new.get(i), i);	
//			}
//		}		
//		// XX: compute filtered po trace
//		
//
//		return result;
//	}
//
//}
