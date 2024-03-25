package org.processmining.partialorder.util;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

public class PartialUtil {

	public static String getPartialLogName(XLog log) {
		
		return XConceptExtension.instance().extractName(log) + "-partial";
	}
	
	public static String getPOTraceName(XTrace trace, int traceIndex){
		String name = XConceptExtension.instance().extractName(trace);
		if (name == null || name.isEmpty()) {
			name = "Trace " + traceIndex;
		}
		return name;
	}

	public static String getPOTraceName(int traceIndex) {

		return "Trace " + traceIndex;
	}

	
}
