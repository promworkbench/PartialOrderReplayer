package org.processmining.partialorder.util;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;

public class ControlUtil {

	public static String getLowLevelLogConceptName(XLog log) {
		
		return XConceptExtension.instance().extractName(log) + " system level";
	}

}
