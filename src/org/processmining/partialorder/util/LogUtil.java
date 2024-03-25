package org.processmining.partialorder.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XElement;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.extension.XDataExtension;

public class LogUtil {

	public static String getName(XElement xobject) {
		return XConceptExtension.instance().extractName(xobject);
	}

	public final static int INPUT_INDEX = 0;
	public final static int OUTPUT_INDEX = 1;
	public final static int INOUTPUT_INDEX = 2;

	public static List<Set<String>> getAllEventIOAttributeKeys(XLog log) {
		Set<String> input = new HashSet<String>();
		Set<String> output = new HashSet<String>();
		Set<String> total = new HashSet<String>();
		for (XTrace trace : log) {
			for (XEvent event : trace) {
				XAttributeMap map = XDataExtension.instance().extractInputAttributes(event);
				if (map != null) {
					input.addAll(map.keySet());
					total.addAll(map.keySet());
				}
				map = XDataExtension.instance().extractOutputAttributes(event);
				if (map != null) {
					output.addAll(map.keySet());
					total.addAll(map.keySet());
				}
			}
		}
		List<Set<String>> res = new ArrayList<Set<String>>(3);
		res.add(0, input);
		res.add(1, output);
		res.add(2, total);
		return res;
	}

	//	public static Set<String> getAllEventInputAttributeKeys(XLog log){
	//		Set<String> result = new HashSet<String>();
	//		for(XTrace trace : log){
	//			for(XEvent event : trace){
	//				result.addAll(XDataExtension.instance().extractInputAttributes(event).keySet());
	//				//result.addAll(XDataExtension.instance().extractOutputAttributes(event).keySet());
	//			}
	//		}
	//		return result;
	//	}
	//	
	//	public static Set<String> getAllEventOutputAttributeKeys(XLog log){
	//		Set<String> result = new HashSet<String>();
	//		for(XTrace trace : log){
	//			for(XEvent event : trace){
	//				//result.addAll(XDataExtension.instance().extractInputAttributes(event).keySet());
	//				result.addAll(XDataExtension.instance().extractOutputAttributes(event).keySet());
	//			}
	//		}
	//		return result;
	//	}

	public static List<Integer> getTraceIndeces(XLog log) {
		List<Integer> indeces = new ArrayList<Integer>();

		for (int i = 0; i < log.size(); i++) {
			indeces.add(i);
		}
		return indeces;
	}

	public static List<String> getTraceNames(XLog log) {
		List<String> indeces = new ArrayList<String>();

		for (int i = 0; i < log.size(); i++) {
			indeces.add(XConceptExtension.instance().extractName(log.get(i)));
		}
		return indeces;
	}

	public static int getTraceIndexByName(XLog log, String name) {

		for (int i = 0; i < log.size(); i++) {
			if (XConceptExtension.instance().extractName(log.get(i)).equals(name)) {
				return i;
			}
		}
		throw new IllegalArgumentException("Trace name " + name + " not contained in the log");
	}

}
