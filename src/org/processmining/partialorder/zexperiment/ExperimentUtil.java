package org.processmining.partialorder.zexperiment;

import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XIdentityExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.id.XID;
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.processmining.extension.XDataExtension;

public class ExperimentUtil {
	
	public static void addIdentifiersToEventsAndRecord(XLog log) {

		LogNoiseRecorder recorder = new LogNoiseRecorder();
		for (XTrace t : log) {
			assignId(t, recorder);
			for (XEvent e : t) {
				assignId(e, recorder);
			}
		}
	}
	
	public static void addIdentifiersToEventsAndRecord(XLog log, LogNoiseRecorder recorder) {
		@SuppressWarnings("deprecation")
		Date d = new Date(2014, 4, 30);

		for (XTrace t : log) {
			assignId(t, recorder);
			for (XEvent e : t) {
				assignId(e, recorder);
				XTimeExtension.instance().assignTimestamp(e, d);
			}
		}
	}
	
	public static void assignId(XAttributable element, LogNoiseRecorder recorder) {
		XIdentityExtension extension = XIdentityExtension.instance();
		XID id = extension.extractID(element);
		if (id == null) {
			id = recorder.generateNewIdAndStore();
			extension.assignID(element, id);
		} else {
			recorder.storeId(id);
		}
	}

	public static void removeEvents(XLog log, LogNoiseRecorder recorder, int n, PrintWriter out) {
		if (n <= 0) {
			return;
		}
		for (int i = 0; i < log.size(); i++) {
			XTrace t = log.get(i);
			int k = 0;
			while (k < n) {
				int removeIndex = getRandomIndex(t);
				XEvent origEvent = t.get(removeIndex);
				if(!recorder.isAddedNoise(i, origEvent)){
					t.remove(origEvent);
					recorder.addRemovedEvent(i, origEvent);
					out.println(i + " | Removed Event [" + XConceptExtension.instance().extractName(origEvent) 
							+ "] id " + XIdentityExtension.instance().extractID(origEvent));
					k++;
				}
				
			}
		}
	}

	/**
	 * For each trace in the log, copy n number of events and add them as noise
	 * to the trace. Record the noise added in the recorder
	 * 
	 * @param log
	 *            The log to which the noise is added
	 * @param recorder
	 *            The recorder in which the noise is stored
	 * @param n
	 *            The number of (noise) events per trace to be added.
	 */
	public static void addEvents(XLog log, LogNoiseRecorder recorder, int n, PrintWriter out) {
		copyOrReindexEvents(log, recorder, n, false, out);
	}

	public static void reindexEvents(XLog log, LogNoiseRecorder recorder, int n, PrintWriter out) {
		copyOrReindexEvents(log, recorder, n, true, out);
	}

	public static void copyOrReindexEvents(XLog log, LogNoiseRecorder recorder, int n, boolean remove, PrintWriter out) {
		if (n <= 0) {
			return;
		}
		for (int i = 0; i < log.size(); i++) {
			XTrace t = log.get(i);

			Map<XEvent, XEvent> mapNoiseEvent2OrigEvent = new HashMap<XEvent, XEvent>();
			for (int k = 0; k < n; k++) {
				/*
				 * Note that adding noise events can be copied from the same
				 * event
				 */
				XEvent origEvent = getRandomEvent(t);
				XEvent newEvent = cloneEventWithNewNameAndIdWithoutIOs(origEvent, recorder, k);
				mapNoiseEvent2OrigEvent.put(newEvent, origEvent);

				if (remove) {
					t.remove(origEvent);
					recorder.addRemovedEvent(i, origEvent);
				}
			}

			for (XEvent noiseEvent : mapNoiseEvent2OrigEvent.keySet()) {
				XEvent origEvent = mapNoiseEvent2OrigEvent.get(noiseEvent);
				int pastToIndex = getRandomIntBetween(0, t.size());
				
				out.println(i + " | Added event [" + XConceptExtension.instance().extractName(origEvent) + "] at index [" + pastToIndex + "]");

				XEvent predecessor = getRandomPredecessor(t, pastToIndex);
				XEvent successor = getRandomSuccessor(t, pastToIndex);

				updateNoiseEventDataDependencies(recorder, i, noiseEvent, predecessor, successor, out);
				

				recorder.addAddedEvent(i, origEvent, noiseEvent);
				t.add(pastToIndex, noiseEvent);
//				if (predecessor != null)
//					recorder.addAddedDependency(i, predecessor, noiseEvent);
//				if (successor != null)
//					recorder.addAddedDependency(i, noiseEvent, successor);
			}
		}
	}

	private static void updateNoiseEventDataDependencies(LogNoiseRecorder recorder, int traceIndex, XEvent noiseEvent, XEvent predecessor, XEvent successor, PrintWriter out) {
		XDataExtension.instance().clearInputAttributes(noiseEvent);
		XDataExtension.instance().clearOutputAttributes(noiseEvent);

		if (predecessor != null) {
			XAttributeMap outputs = XDataExtension.instance().extractOutputAttributes(predecessor);
			if (outputs != null && outputs.keySet().size() > 0) {
				String key = getRandonKey(outputs);
				XAttribute input = (XAttribute) outputs.get(key).clone();
				XDataExtension.instance().assignInputAttributes(noiseEvent, input);
				out.println(traceIndex + " | Added pred [" + XConceptExtension.instance().extractName(predecessor)
						+ "] with key [" + key + "]");
				recorder.addAddedDependency(traceIndex, predecessor, noiseEvent);
				
			}
		}
		if (successor != null) {

			XAttributeMap inputs = XDataExtension.instance().extractInputAttributes(successor);
			if (inputs != null && inputs.keySet().size() > 0) {
				String inputkey = getRandonKey(inputs);
				XAttribute output = (XAttribute) inputs.get(inputkey).clone();
				XDataExtension.instance().assignOutputAttributes(noiseEvent, output);
				out.println(traceIndex + " | Added succ [" + XConceptExtension.instance().extractName(successor)
						+ "] with key [" + inputkey + "]");
				recorder.addAddedDependency(traceIndex, noiseEvent, successor);
			}
		}
	}

	private static XEvent getRandomSuccessor(XTrace t, int pastToIndex) {
		return getRandomEventBetween(t, pastToIndex + 1, t.size() - 1);
	}

	private static XEvent getRandomPredecessor(XTrace t, int pastToIndex) {
		return getRandomEventBetween(t, 0, pastToIndex - 1);
	}

	private static XEvent cloneEventWithNewNameAndIdWithoutIOs(XEvent origEvent, LogNoiseRecorder recorder, int k) {
		XEvent newEvent = (XEvent) origEvent.clone();
		XID newEventID = recorder.generateNewIdAndStore();
		XIdentityExtension.instance().assignID(newEvent, newEventID);
//		XConceptExtension.instance().assignName(newEvent, "Noise"+k);
		newEvent.getAttributes().put("Noise", new XAttributeLiteralImpl("Noise", "Noise"));
		return newEvent;
	}

	public static void removeData(XLog log, LogNoiseRecorder recorder, int n, PrintWriter logger) {

		if (n <= 0) {
			return;
		}
		for (int i = 0; i < log.size(); i++) {
			XTrace t = log.get(i);

			for (int k = 0; k < n; k++) {
				XEvent e = getRandomEvent(t);
				XAttributeMap map = getRandomMapFromEvent(e);
				String dataKey = getRandonKey(map);
				map.remove(dataKey);
			}
		}
	}

	public static void addRemovedData(XLog log, LogNoiseRecorder recorder, int n, PrintWriter logger) {
		addDataNoise(log, recorder, n, true, logger);
	}

	public static void addCopiedData(XLog log, LogNoiseRecorder recorder, int n, PrintWriter logger) {
		addDataNoise(log, recorder, n, false, logger);
	}

	public static void addDataNoise(XLog log, LogNoiseRecorder recorder, int n, boolean remove, PrintWriter out) {

		if (n <= 0) {
			return;
		}
		for (int i = 0; i < log.size(); i++) {
			XTrace t = log.get(i);

			for (int k = 0; k < n; k++) {
				XEvent e = getRandomEvent(t);
				XAttributeMap map = getRandomMapFromEvent(e);
				XAttribute attribute = copyRandomDataAttributeFromMap(map);
				if (remove) {
					map.remove(attribute.getKey());
				}
				XAttributeMap pastToMap = null;
				while (pastToMap == null || pastToMap.equals(map)) {
					XEvent pastToEvent = getRandomEvent(t);
					pastToMap = getRandomMapFromEvent(pastToEvent);
				}
				pastToMap.put(attribute.getKey(), attribute);

			}
		}
	}

	private static XAttribute copyRandomDataAttributeFromMap(XAttributeMap map) {
		String dataKey = getRandonKey(map);
		XAttribute attribute = (XAttribute) map.get(dataKey).clone();
		return attribute;
	}

	private static String getRandonKey(XAttributeMap map) {
		String[] datakeys = map.keySet().toArray(new String[map.keySet().size()]);
		int dataKeyI = getRandomIntBetween(0, datakeys.length - 1);
		String dataKey = datakeys[dataKeyI];
		return dataKey;
	}

	private static XAttributeMap getRandomMapFromEvent(XEvent e) {
		XAttributeMap map = XDataExtension.instance().extractOutputAttributes(e);
		/* Choose randomly between the input map or output map */
		boolean isInputmap = Math.random() >= 0.5;
		if (map == null || isInputmap) {
			map = XDataExtension.instance().extractInputAttributes(e);
			isInputmap = true;
		}
		return map;
	}

	private static XEvent getRandomEvent(XTrace t) {
		return getRandomEventBetween(t, 0, t.size() - 1);
	}

	private static XEvent getRandomEventBetween(XTrace t, int min, int max) {
		int index = getRandomIntBetween(min, max);
		if (index < 0) {
			return null;
		}
		return t.get(index);
	}

	private static int getRandomIndex(XTrace t) {
		return getRandomIntBetween(0, t.size() - 1);
	}

	private static int getRandomIntBetween(int min, int max) {
		if (max < min) {
			return -1;
		}
		if (max == min) {
			return min;
		}
		int range = (max - min) + 1;
		return (int) (Math.random() * range) + min;

	}


}
