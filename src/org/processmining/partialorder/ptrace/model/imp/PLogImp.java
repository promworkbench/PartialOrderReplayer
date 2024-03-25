package org.processmining.partialorder.ptrace.model.imp;

import java.util.ArrayList;
import java.util.Collection;

import org.deckfour.xes.model.XLog;
import org.processmining.partialorder.ptrace.model.PLog;
import org.processmining.partialorder.ptrace.model.PTrace;

public class PLogImp extends ArrayList<PTrace> implements PLog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6916477463037159210L;

	private XLog originalLog;
	
	public PLogImp(XLog log){
		this.originalLog = log;
	}
	
	public Collection<PTrace> getTraces() {
		return this;
	}

	public XLog getXLog() {
		return originalLog;
	}

	
//	public PTrace get(int traceIndex) {
//		return this.get(traceIndex);
//	}

//	public PTrace getPTrace(XID traceId) {
//		for(PTrace ptrace : this){
//			if(ptrace.getTraceId().equals(traceId)){
//				return ptrace;
//			}
//		}
//		return null;
//	}

//	public void add(int traceIndex, PTrace trace) {
//		this.add(traceIndex, trace);
//	}

}
