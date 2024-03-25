package org.processmining.partialorder.zexperiment.dal;

import java.util.HashMap;
import java.util.Map;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.annotation.DALConversionAlgorithm;
import org.processmining.extension.XDataExtension;
import org.processmining.framework.plugin.annotations.KeepInProMCache;
import org.processmining.partialorder.dal.models.EnumDataAccessType;
import org.processmining.partialorder.dal.param.DALConversionParameters;
import org.processmining.partialorder.dal.plugins.conversion.alg.DALConvAlgAbstract;

@KeepInProMCache
@DALConversionAlgorithm
public class TestDataConvAlg extends DALConvAlgAbstract {
	
	public String toString(){
		return "Convert the log from CPN model (SBP) to DAL using specification";
	}

	
	public XLog convert(XLog log, DALConversionParameters param) {
		//Map<String, Map<String, EnumDataAccessType>> mapEvent2DataKey2IO = param.getMapEvent2DataKey2IO();
		Map<String, Map<String, EnumDataAccessType>> mapEvent2DataKey2IO = getTestDataMap();
		
		System.out.println("XXXXX" + log.size());
		XLog newLog = (XLog) log.clone();
		newLog.clear();
		for (XTrace trace : log) {
			XTrace newTrace = (XTrace) trace.clone();
			newTrace.clear();
			
			for (int i = 0; i < trace.size(); i++) {
				XEvent e = trace.get(i);
				XEvent newEvent = (XEvent) trace.get(i).clone();
				String eString = XConceptExtension.instance().extractName(e);

//				if (newEvent.containsKey(i)) {
				
				Map<String, EnumDataAccessType> data2IO = mapEvent2DataKey2IO.get(eString);
				
				
					for ( XAttribute a : e.getAttributes().values()) {
						XAttribute newa= (XAttribute) a.clone();
						if(data2IO.containsKey(a.getKey())){
							EnumDataAccessType type = data2IO.get(a.getKey());
							if (type.equals(EnumDataAccessType.I)) {
								XDataExtension.instance().assignInputAttributes(newEvent, newa);
							} else if (type.equals(EnumDataAccessType.O)) {
								XDataExtension.instance().assignOutputAttributes(newEvent,newa);
							}
						}

					}
//				}
				newTrace.add(newEvent);
			}
			newLog.add(newTrace);
			
		}
		
		return newLog;
	}

	
	// TODO: remove, test
	public static Map<String, Map<String, EnumDataAccessType>> getTestDataMap() {
		Map<String, Map<String, EnumDataAccessType>> mapEvent2DataKey2IO = new HashMap<String, Map<String,EnumDataAccessType>>();
		
		//
		HashMap<String,EnumDataAccessType> data2IO = new HashMap<String, EnumDataAccessType>();
		data2IO.put("Patient", EnumDataAccessType.O);		
		mapEvent2DataKey2IO.put("Appointment", data2IO);
		
		data2IO = new HashMap<String, EnumDataAccessType>();
		data2IO.put("Patient", EnumDataAccessType.I);	
		data2IO.put("Verification", EnumDataAccessType.O);
		mapEvent2DataKey2IO.put("Check History", data2IO);
		
		
		data2IO = new HashMap<String, EnumDataAccessType>();
		data2IO.put("Patient", EnumDataAccessType.I);	
		data2IO.put("RTest", EnumDataAccessType.O);
		mapEvent2DataKey2IO.put("Radiology", data2IO);
		
		
		data2IO = new HashMap<String, EnumDataAccessType>();
		data2IO.put("Patient", EnumDataAccessType.I);	
		data2IO.put("LTest", EnumDataAccessType.O);
		mapEvent2DataKey2IO.put("LabTest", data2IO);
		
		
		data2IO = new HashMap<String, EnumDataAccessType>();
		data2IO.put("Patient", EnumDataAccessType.I);	
		data2IO.put("LTest", EnumDataAccessType.I);
		data2IO.put("RTest", EnumDataAccessType.I);
		data2IO.put("Verification", EnumDataAccessType.I);
		data2IO.put("Evaluation", EnumDataAccessType.O);
		mapEvent2DataKey2IO.put("Evaluate", data2IO);
		
		
		data2IO = new HashMap<String, EnumDataAccessType>();
		data2IO.put("Patient", EnumDataAccessType.I);	
		data2IO.put("Evaluation", EnumDataAccessType.I);	
		data2IO.put("Status", EnumDataAccessType.O);
		mapEvent2DataKey2IO.put("TreatHome", data2IO);
		
		data2IO = new HashMap<String, EnumDataAccessType>();
		data2IO.put("Patient", EnumDataAccessType.I);	
		data2IO.put("Evaluation", EnumDataAccessType.O);	
		data2IO.put("Status", EnumDataAccessType.I);
		mapEvent2DataKey2IO.put("Re-eval", data2IO);
		
		
		data2IO = new HashMap<String, EnumDataAccessType>();
		data2IO.put("Patient", EnumDataAccessType.I);	
		data2IO.put("Operation", EnumDataAccessType.O);	
		data2IO.put("Evaluation", EnumDataAccessType.I);
		mapEvent2DataKey2IO.put("Operate", data2IO);
		
		
		data2IO = new HashMap<String, EnumDataAccessType>();
		data2IO.put("Patient", EnumDataAccessType.I);	
		data2IO.put("Operation", EnumDataAccessType.I);	
		data2IO.put("Status", EnumDataAccessType.O);
		mapEvent2DataKey2IO.put("Nursing", data2IO);
		
		return mapEvent2DataKey2IO;
	}

}
