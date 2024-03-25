package org.processmining.partialorder.plugins.replay;

import gnu.trove.list.TIntList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import nl.tue.astar.AStarThread;
import nl.tue.astar.util.PartiallyOrderedTrace;

import org.deckfour.xes.model.XLog;
import org.processmining.partialorder.models.replay.PartialAwarePILPDelegate;
import org.processmining.partialorder.ptrace.model.PLog;
import org.processmining.partialorder.ptrace.model.PTrace;
import org.processmining.partialorder.ptrace.model.PTraceExtended;
import org.processmining.partialorder.ptrace.model.imp.PTraceExtendedRecursionImp;
import org.processmining.partialorder.ptrace.param.PTraceParameter;
import org.processmining.plugins.astar.petrinet.PartialOrderBuilder;
import org.processmining.plugins.astar.petrinet.impl.AbstractPDelegate;

public class StandardPTraceConverter implements PartialOrderBuilder {

	@SuppressWarnings("unused")
	private PTraceParameter parameter;
	private PLog pLog;

	public StandardPTraceConverter(PLog log, PTraceParameter parameter) {
		this.pLog = log;
		this.parameter = parameter;
	}

	public PartiallyOrderedTrace getPartiallyOrderedTrace(XLog log, int traceIndex, AbstractPDelegate<?> delegate,
			TIntList unUsedIndices, TIntIntMap trace2orgTrace) {

		PartialAwarePILPDelegate paDelegate = (PartialAwarePILPDelegate) delegate;
		// XTrace t = log.get(traceIndex);

		// TODO
		/* Create a po trace skeleton */
		PTrace pTrace = pLog.get(traceIndex);
		PTraceExtended poXTrace = null;
		if (pTrace instanceof PTraceExtended) {
			poXTrace = (PTraceExtended) pTrace;
		} else {
			poXTrace = new PTraceExtendedRecursionImp(pLog.get(traceIndex));
		}
		
		for (Integer index : poXTrace.getEventIndices()) {
			int act = paDelegate.getActivityOf(poXTrace.getTraceIndex(), index);
			poXTrace.setEventActivity(index, act, act != AStarThread.NOMOVE);
		}

		paDelegate.putTraceToPOXTrace(traceIndex, poXTrace);
		//		poXTrace.setPOTrace(orgPOTrace);
		TIntIntMap org2new = new TIntIntHashMap();
		paDelegate.putTraceToOrgPartialTrace(traceIndex, poXTrace.getOriginalPTrace());
		//		org2new = new TIntIntHashMap();
		//TODO a proper way to include sorted
		PartiallyOrderedTrace result = poXTrace.getFilteredPTrace(org2new, true);

		for (int i : org2new.keys()) {
			if (org2new.get(i) >= 0) {
				paDelegate.putFilteredEventToOrgEventIndex(traceIndex, org2new.get(i), i);
			}
		}
		// XX: compute filtered po trace
		return result;
	}

}
