package org.processmining.partialorder.ptrace.plugins.builder.alg;

import java.util.Date;

import org.deckfour.xes.model.XTrace;
import org.processmining.partialorder.ptrace.param.PTraceParameter;

public class PTraceSameTimeBuilderImp extends PTraceBlockstructuredBuilderAbstract {

	public PTraceSameTimeBuilderImp(int traceIndex, XTrace t, PTraceParameter param) {
		super(traceIndex, t, param);
	}

	protected boolean isSucceeding(Date currentDay, Date d) {
		if(currentDay == null || d == null){
			return true;
		}
		return currentDay.before(d);
	}

}
