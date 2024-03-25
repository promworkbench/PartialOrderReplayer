package org.processmining.partialorder.ptrace.model;

import gnu.trove.map.TIntIntMap;
import nl.tue.astar.util.PartiallyOrderedTrace;

public interface PTraceExtended extends PTrace {

//	/**
//	 * To add a new vertex in the partially ordered trace.
//	 * @param index The index of event in the linear trace
//	 * @param act	The activity number of event given by the (ILP) delegate
//	 * @param b		The visibility of the event according to the (ILP) delegate
//	 * @return
//	 */
//	boolean addEvent(int eventIndex, int act, boolean visibility);
	

	PartiallyOrderedTrace getOriginalPTrace();

	PartiallyOrderedTrace getFilteredPTrace(TIntIntMap org2new, boolean b);
	
	void setEventActivity(Integer eventIndex, int act, boolean b);
	
	
}
