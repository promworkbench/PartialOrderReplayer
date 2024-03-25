package org.processmining.partialorder.ptrace.plugins.builder.alg;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.processmining.partialorder.models.dependency.DependencyFactory;
import org.processmining.partialorder.ptrace.model.PTrace;
import org.processmining.partialorder.ptrace.model.imp.PTraceImp;
import org.processmining.partialorder.ptrace.param.PTraceParameter;

//@PTraceBuilderAlgorithm
public abstract class PTraceBlockstructuredBuilderAbstract extends PTraceBuilderAlgAbstract {



	public PTraceBlockstructuredBuilderAbstract(int traceIndex, XTrace t, PTraceParameter param) {
		super(traceIndex, t, param);
	}

	// Assume events of t sorted by timestamp
	public PTrace computePTrace(XTrace t, int traceIndex) {
		int traceSize = t.size();
		PTrace ptrace = new PTraceImp(t, traceIndex);
		if(traceSize == 0){
			return ptrace;
		}
		
		Date currentDay = XTimeExtension.instance().extractTimestamp(t.get(0));
		Set<Integer> prevSet = new HashSet<Integer>();
		Set<Integer> currSet = new HashSet<Integer>();
		
		for (int i = 0; i < traceSize; i++) {
			XEvent e = t.get(i);
			Date d = XTimeExtension.instance().extractTimestamp(e);
			ptrace.addEvent(i);
		
			if (isSucceeding(currentDay, d)){
				prevSet = currSet;
				currSet = new HashSet<Integer>();
				currentDay = d;
			} 
			currSet.add(i);
			
			for(Integer pred : prevSet){
				ptrace.addDependency(DependencyFactory.createSimpleDirectDependency(pred, i), pred, i);
			}
		}		
		return ptrace;
	}

	protected abstract boolean isSucceeding(Date currentDay, Date d);
	

	
}
