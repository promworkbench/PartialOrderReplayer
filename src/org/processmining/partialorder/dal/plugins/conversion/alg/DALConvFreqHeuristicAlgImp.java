package org.processmining.partialorder.dal.plugins.conversion.alg;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.annotation.DALConversionAlgorithm;
import org.processmining.extension.XDataExtension;
import org.processmining.framework.plugin.annotations.KeepInProMCache;
import org.processmining.partialorder.dal.models.EnumDataAccessType;
import org.processmining.partialorder.dal.param.DALConversionParameters;
import org.processmining.partialorder.util.DebugUtil;

@KeepInProMCache
@DALConversionAlgorithm
public class DALConvFreqHeuristicAlgImp extends DALConvAlgAbstract {

	public String toString() {
		return "Use Frequency Heuristics to Convert a Log to DAL";
	}

	public XLog convert(XLog log, DALConversionParameters param) {
		/* Create XEventClasses info */
		XLogInfo info = XLogInfoFactory.createLogInfo(log, param.getClassifier());

		XEventClasses eclasses = info.getEventClasses();

		/*
		 * maps: EventClass string -> (data key -> number of events as input /
		 * as output)
		 */
		Map<String, Map<String, Integer>> numInput = new HashMap<String, Map<String, Integer>>();
		Map<String, Map<String, Integer>> numOutput = new HashMap<String, Map<String, Integer>>();

		/*
		 * map: EventClass string -> (data key -> I/O)
		 */
		Map<String, Map<String, EnumDataAccessType>> map = new HashMap<String, Map<String, EnumDataAccessType>>();

		/* Init maps */
		for (XEventClass c : eclasses.getClasses()) {
			numInput.put(c.toString(), new HashMap<String, Integer>());
			numOutput.put(c.toString(), new HashMap<String, Integer>());
			map.put(c.toString(), new HashMap<String, EnumDataAccessType>());
		}

		Set<String> datakeys = new HashSet<String>();

		for (XTrace trace : log) {
			/*
			 * map: data key -> current value
			 */
			Map<String, XAttribute> currentDataValue = new HashMap<String, XAttribute>();

			for (XEvent event : trace) {
				XEventClass eclass = eclasses.getClassOf(event);

				for (Entry<String, XAttribute> entry : event.getAttributes().entrySet()) {
					String key = entry.getKey();
					XAttribute value = entry.getValue();

					if (isBasicAttribute(value)) {
						continue; /* skip if basic */
					}
					datakeys.add(key);

					/*
					 * Three cases 1. The first data access
					 */
					if (!currentDataValue.containsKey(key)) {
						if(param.isAttrOfStartInput()){
							updateCount(numInput, eclass, key, 1);
						} else {
							updateCount(numOutput, eclass, key, 1);
						}
						
					} else {
						XAttribute preValue = currentDataValue.get(key);

						if (preValue.equals(value)) {
							/* 2. If the next data access with same value */
							updateCount(numInput, eclass, key, 1);
						} else {
							/* 3. If the next data access with different value */
							updateCount(numOutput, eclass, key, 1);
						}

					}
					/* Update data key value */
					currentDataValue.put(key, value);
				}

			}
		}

		DebugUtil.print(numInput);
		DebugUtil.print(numOutput);

		/*
		 * r: ration of write if >= r% of (eventclass, data) is a
		 * write-operation, then (e, d, w), else (e, d, r);
		 */
		int ratio = param.getRatio();

		for (XEventClass eclass : eclasses.getClasses()) {

			for (String data : datakeys) {
				int numI = getMapValue(numInput, eclass, data);
				int numO = getMapValue(numOutput, eclass, data);

				if (numI > 0 || numO > 0) {
					double r = ((double) numO / (numI + numO)) * 100.0;
					System.out.println("[" + eclass.toString() + "," + data + "] = " + r);
					if (r >= ratio) {
						map.get(eclass.toString()).put(data, EnumDataAccessType.O);
					} else {
						map.get(eclass.toString()).put(data, EnumDataAccessType.I);
					}
				}

			}
		}
		DebugUtil.printIOmap(map);

		param.setMapEvent2DataKey2IO(map);

		XLog newLog = converLog(log, eclasses, map);

		return newLog;
	}

	private XLog converLog(XLog origLog, XEventClasses eclasses, Map<String, Map<String, EnumDataAccessType>> map) {
		XLog log = (XLog) origLog.clone();
		for (XTrace t : log) {
			for (XEvent e : t) {
				String eclassString = eclasses.getClassOf(e).toString();
				if (!map.containsKey(eclassString)) {
					continue;
				}
				
				Map<String, EnumDataAccessType> mapKey2io = map.get(eclassString);
				Set<String> dataKeys = new HashSet<String>(e.getAttributes().keySet());
				for (String key : dataKeys) {
					if (mapKey2io.containsKey(key)) {
						EnumDataAccessType type = mapKey2io.get(key);
						if (type.equals(EnumDataAccessType.I)) {
							XDataExtension.instance().assignInputAttributes(e, e.getAttributes().get(key));
						} else { // type.equals(EnumDataAccessType.O)
							XDataExtension.instance().assignOutputAttributes(e, e.getAttributes().get(key));
						}
					}
				}

			}
		}
		return log;
	}

	private int getMapValue(Map<String, Map<String, Integer>> map, XEventClass eclass, String data) {
		if (map.get(eclass.toString()).containsKey(data)) {
			return map.get(eclass.toString()).get(data);
		}
		return 0;
	}

	private void updateCount(Map<String, Map<String, Integer>> map, XEventClass eclass, String key, int i) {
		String eclassString = eclass.toString();
		if (map.get(eclassString).containsKey(key)) {
			/* map has previous value stored */
			int count = map.get(eclassString).get(key);
			count += i;
			map.get(eclassString).put(key, count);
		} else {
			/* map has previous value 0 */
			map.get(eclassString).put(key, i);
		}

	}

}
