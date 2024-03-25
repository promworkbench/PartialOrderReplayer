package org.processmining.partialorder.ptrace.model;

import java.util.Collection;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.processmining.partialorder.models.dependency.PDependency;




public interface PTrace extends Iterable<XEvent> {

	/**
	 * @return The linear trace
	 */
	public XTrace getTrace();
	public XEvent getEvent(int index);

	/**
	 * @return The index of the linear trace in the log
	 */
	public int getTraceIndex();
	public void setTraceIndex(int i);
	

	/**
	 * @return The number of events
	 */
	public int size();

	public Collection<Integer> getEventIndices();
	
	/**
	 * @return A set of indices of events that have no predecessors.
	 */
	public Collection<Integer> getStartEventIndices();
	
	/**
	 * @return A set of indices of event that have no successors.
	 */
	public Collection<Integer> getEndEventIndices();

	
	public Collection<PDependency> getDependencies();
	
	/**
	 * 
	 * @param source
	 *            The index of preceding event
	 * @param target
	 *            The index of succeeding event
	 * @return A data relation between the two events, or null if there is no
	 *         relation
	 */
	public PDependency getDependency(int sourceEventIndex, int targetEventIndex);
	public Collection<Integer> getPredecessorIndices(int index);
	public Collection<Integer> getSuccessorIndices(int index);


	public boolean addEvent(int eventIndex);
	public boolean addDependency(PDependency relation, int sourceEventIndex, int targetEventIndex);

	


//	Collection<XEvent> getEvents();
	
	
//	XID getTraceId();
//	void setTraceId(XID id);
	
}

