package org.processmining.partialorder.ptrace.plugins.builder.alg;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.processmining.extension.XPartialOrderExtension;
import org.processmining.partialorder.models.dependency.PDependency;
import org.processmining.partialorder.ptrace.model.PTrace;
import org.processmining.partialorder.ptrace.param.PTraceParameter;

public abstract class PTraceBuilderAlgAbstract implements PTraceBuilderAlg {
	XTrace t;
	int traceIndex;
	PTraceParameter param;

	public PTraceBuilderAlgAbstract(int traceIndex, XTrace t, PTraceParameter param) {
		this.t = t;
		this.traceIndex = traceIndex;
		this.param = param;
	}

	/**
	 * This method computes a p-trace for the given XTrace and update the XTrace
	 * with dependencies
	 */
	public PTrace call() throws Exception {
		PTrace ptrace = computePTrace(t, traceIndex);
		if (param.isComputeRelation()){
			PTraceUtil.computeCompleteRelations(ptrace);
		} else if(param.isComputeTransReduction()){
			PTraceUtil.computeAndSetTransitiveReduction(ptrace); 
		} 
//		updateTraceWithPartialOrderInfo(t, ptrace);
		return ptrace;
	}
	
	

	public abstract PTrace computePTrace(XTrace t, int traceIndex);

	
	private void updateTraceWithPartialOrderInfo(XTrace t, PTrace p) {
		for (PDependency d : p.getDependencies()) {
			XEvent pred = t.get(d.getSource());
			XEvent succ = t.get(d.getTarget());
			XPartialOrderExtension.instance().addDependency(t, pred, succ);
		}
	}

}
