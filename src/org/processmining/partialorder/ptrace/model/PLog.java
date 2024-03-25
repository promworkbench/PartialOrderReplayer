package org.processmining.partialorder.ptrace.model;

import java.util.Collection;

import org.deckfour.xes.model.XLog;


public interface PLog extends Iterable<PTrace> {
	
	public PTrace get(int traceIndex);
	public void add(int traceIndex, PTrace trace);		
	public int size();
	public Collection<PTrace> getTraces();
	
	public XLog getXLog();

}
