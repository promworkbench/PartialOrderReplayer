package org.processmining.partialorder.ptrace.plugins.builder.alg;

import java.util.Date;

import org.deckfour.xes.model.XTrace;
import org.processmining.annotation.PTraceBuilderAlgorithm;
import org.processmining.partialorder.ptrace.param.PTraceParameter;

@PTraceBuilderAlgorithm
public class PTraceSequentialBuilderImp extends PTraceBlockstructuredBuilderAbstract {

	public PTraceSequentialBuilderImp(int traceIndex, XTrace t, PTraceParameter param) {
		super(traceIndex, t, param);
	}
//
//	public PTrace computePTrace(XTrace t, int traceIndex) {
//		/* init */
//		int traceSize = t.size();
//
//		/* Create a po trace skeleton */
//		PTrace poXTrace = new PTraceImp(t, traceIndex);
//
//		List<int[]> predecessors = new ArrayList<int[]>();
//
//		for (int i = 0; i < traceSize; i++) {
//			// activity i of trace
//			int pre = i - 1;
//			XEvent currentEvent = t.get(i);
//			//originalTrace.add(i);
//			//activities.add(act);
//
//			/* Add an event to the po trace */
//			poXTrace.addEvent(i);
//
//			if (i > 0) {
//				XEvent predecessor = t.get(pre);
//				PDependencyDataAware relation = new PDependencyDataAware(pre, i);
//				relation.putDependency(EnumDataDependency.OO_DiffValue, XTimeExtension.KEY_TIMESTAMP, predecessor
//						.getAttributes().get(XTimeExtension.KEY_TIMESTAMP),
//						currentEvent.getAttributes().get(XTimeExtension.KEY_TIMESTAMP));
//				relation.setDirect(true);
//				poXTrace.addDependency(relation, pre, i);
//
//				predecessors.add(new int[] { pre });
//			}
//		}
//
//		return poXTrace;
//	}

	protected boolean isSucceeding(Date currentDay, Date d) {
		// the next event (with date d) always succeed the current event with (currentDay)
		return true;
	}

}
