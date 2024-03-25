package org.processmining.partialorder.util;

import org.deckfour.xes.extension.std.XIdentityExtension;
import org.deckfour.xes.id.XID;
import org.deckfour.xes.id.XIDFactory;
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

public class XIdentityUtil {
	
	public static void addIdentifiersToTracesAndEvents(XLog log) {
		XIdentityExtension extension = XIdentityExtension.instance();
		for (XTrace t : log) {
			assignId(t, extension);
			for (XEvent e : t) {
				assignId(e, extension);
			}
		}
	}

	public static void assignId(XAttributable element, XIdentityExtension extension) {
		XID id = extension.extractID(element);
		if (id == null) {
			id = XIDFactory.instance().createId(); // Should be unique
		} 
		extension.assignID(element, id);
	}
}
