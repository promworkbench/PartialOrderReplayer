package org.processmining.partialorder.ptrace.plugins.builder.alg;

import java.util.concurrent.Callable;

import org.deckfour.xes.model.XTrace;
import org.processmining.annotation.PTraceBuilderAlgorithm;
import org.processmining.partialorder.ptrace.model.PTrace;

@PTraceBuilderAlgorithm
public interface PTraceBuilderAlg extends Callable<PTrace> {	
	
	public PTrace computePTrace(XTrace t, int traceIndex);
	
	
	

}
