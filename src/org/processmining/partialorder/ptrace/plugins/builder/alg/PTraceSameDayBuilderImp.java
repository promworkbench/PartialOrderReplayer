package org.processmining.partialorder.ptrace.plugins.builder.alg;

import java.util.Calendar;
import java.util.Date;

import org.deckfour.xes.model.XTrace;
import org.processmining.partialorder.ptrace.param.PTraceParameter;

//@PTraceBuilderAlgorithm
public class PTraceSameDayBuilderImp extends PTraceBlockstructuredBuilderAbstract {

	public PTraceSameDayBuilderImp(int traceIndex, XTrace t, PTraceParameter param) {
		super(traceIndex, t, param);
	}

	protected boolean isSucceeding(Date currentDay, Date d) {
		if(currentDay == null || d == null){
			return true;
		}
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(currentDay);
		cal2.setTime(d);
		boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
		                  cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
		return !sameDay;
	}

	
}
