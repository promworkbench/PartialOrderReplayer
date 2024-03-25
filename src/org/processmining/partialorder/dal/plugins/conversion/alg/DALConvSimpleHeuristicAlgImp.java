package org.processmining.partialorder.dal.plugins.conversion.alg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.annotation.DALConversionAlgorithm;
import org.processmining.extension.XDataExtension;
import org.processmining.framework.plugin.annotations.KeepInProMCache;
import org.processmining.partialorder.dal.models.EnumDataAccessType;
import org.processmining.partialorder.dal.models.EventAttrEntity;
import org.processmining.partialorder.dal.param.DALConversionParameters;

@KeepInProMCache
@DALConversionAlgorithm
public class DALConvSimpleHeuristicAlgImp extends DALConvAlgAbstract {

	public String toString() {
		return "Use Simple Heuristics to Convert a Log to DAL";
	}

	/**
	 * 
	 */

	public XLog convert(XLog log, DALConversionParameters param) {
		XLog newLog = (XLog) log.clone();
		newLog.clear();
		for (XTrace trace : log) {

			// build a map: attribute key -> list of events that contain this attribute key
			Map<String, List<EventAttrEntity>> map = buildAttributeEventListMatrix(trace);

			// build a map : event index -> ( attribute -> {input, output})
			Map<Integer, Map<XAttribute, EnumDataAccessType>> eventmap = computeInputOrOutput(trace, map);

			// create a new trace with input and output attributes
			XTrace newTrace = convertTrace(trace, eventmap);

			// test
			if (newTrace.size() != trace.size()) {
				System.out.println("Unequal trace size");
			}
			newLog.add(newTrace);
		}
		return newLog;
	}

	private Map<String, List<EventAttrEntity>> buildAttributeEventListMatrix(XTrace trace) {
		Map<String, List<EventAttrEntity>> map = new HashMap<String, List<EventAttrEntity>>();
		// For each trace, build a map: AN -> a list of events each of which contains AN 
		for (int i = 0; i < trace.size(); i++) {
			XEvent event = trace.get(i);
			for (Entry<String, XAttribute> attrEntry : event.getAttributes().entrySet()) {
				// if attribute is a basic attribute (e.g. concept, timestamp, resource) then skip
				if (isBasicAttribute(attrEntry.getValue())) {
					continue;
				}

				// init an entry in the map if the attribute key have not been seen before. 
				if (!map.containsKey(attrEntry.getKey())) {
					map.put(attrEntry.getKey(), new ArrayList<EventAttrEntity>());
				}

				// create a comp that contains the event and the attribute and registers 
				// whether the value is different then the previous 
				List<EventAttrEntity> listComp = map.get(attrEntry.getKey());
				EventAttrEntity comp = new EventAttrEntity(event, attrEntry.getValue());
				comp.setEventIndex(i);

				if (listComp.isEmpty() || isDifferentThanPrevious(listComp, comp)) {
					comp.setDiffThanPrev(true);
				} else {
					comp.setDiffThanPrev(false);
				}
				listComp.add(comp);
			}
		}
		return map;
	}

	private boolean isDifferentThanPrevious(List<EventAttrEntity> listComp, EventAttrEntity comp) {
		return listComp.get(listComp.size() - 1).getAttribute().compareTo(comp.getAttribute()) != 0;
	}

	private Map<Integer, Map<XAttribute, EnumDataAccessType>> computeInputOrOutput(XTrace trace,
			Map<String, List<EventAttrEntity>> map) {

		Map<Integer, Map<XAttribute, EnumDataAccessType>> result = new HashMap<Integer, Map<XAttribute, EnumDataAccessType>>();

		for (Entry<String, List<EventAttrEntity>> entry : map.entrySet()) {

			List<EventAttrEntity> list = entry.getValue();

			for (EventAttrEntity e : list) {

				if (!result.containsKey(e.getEventIndex())) {
					result.put(e.getEventIndex(), new HashMap<XAttribute, EnumDataAccessType>());
				}
				EnumDataAccessType type = e.isDiffThanPrev() ? EnumDataAccessType.O : EnumDataAccessType.I;

				result.get(e.getEventIndex()).put(e.getAttribute(), type);
			}

		}

		return result;
	}

	private XTrace convertTrace(XTrace trace, Map<Integer, Map<XAttribute, EnumDataAccessType>> eventmap) {
		XTrace newTrace = (XTrace) trace.clone();
		newTrace.clear();
		for (int i = 0; i < trace.size(); i++) {

			XEvent newEvent = (XEvent) trace.get(i).clone();

			if (eventmap.containsKey(i)) {

				for (Entry<XAttribute, EnumDataAccessType> attrTupple : eventmap.get(i).entrySet()) {
					if (attrTupple.getValue().equals(EnumDataAccessType.I)) {
						XDataExtension.instance().assignInputAttributes(newEvent, attrTupple.getKey());
					} else {
						XDataExtension.instance().assignOutputAttributes(newEvent, attrTupple.getKey());
					}

				}
			}
			newTrace.add(newEvent);
		}
		return newTrace;
	}

}
