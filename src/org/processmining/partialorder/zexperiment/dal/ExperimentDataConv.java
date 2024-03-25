package org.processmining.partialorder.zexperiment.dal;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.partialorder.dal.models.EnumDataAccessType;
import org.processmining.partialorder.dal.param.DALConversionParameters;
import org.processmining.partialorder.dal.plugins.conversion.DALConversionAlg;
import org.processmining.partialorder.dal.plugins.conversion.alg.DALConvFreqHeuristicAlgImp;
import org.processmining.partialorder.zexperiment.ExperimentUtil;
import org.processmining.partialorder.zexperiment.LogNoiseRecorder;

//@Plugin(name = "Data Conversion Experiment", returnLabels = { "Experiment result" }, returnTypes = { XLog.class }, parameterLabels = {
//		"Petri net", "Event Log", "Mapping", "Replay Algorithm", "Parameters" }, help = "Partial Aware Replay.", userAccessible = true)
public class ExperimentDataConv {

//	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Xixi Lu", email = "x.lu@tue.nl", pack = "")
//	@PluginVariant(variantLabel = "Data Conversion Experiment", requiredParameterLabels = { 1 })
	public XLog replayLog(final UIPluginContext context, XLog log) {
		PrintWriter out = null;
		try {
			out = new PrintWriter("log_DataConv.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		
		/* 
		 * Change the log 
		 */
		XLog testLog = (XLog) log.clone();
		
		LogNoiseRecorder recorder = new LogNoiseRecorder();
		
		
		
		ExperimentUtil.addEvents(testLog, recorder, 0, out);
		ExperimentUtil.removeEvents(testLog, recorder, 0, out);
//		ExperimentUtil.removeEvents(testLog, recorder, 0, out);
		
//		ExperimentUtil.addData(testLog, recorder, 0, false, out);
//		ExperimentUtil.removeData(testLog, recorder, 0,false, out);
//		ExperimentUtil.addRemovedData(testLog, recorder, 0, false,out);
		
		/*
		 * Execute conversion
		 */
		DALConversionAlg converter = new DALConvFreqHeuristicAlgImp();
		DALConversionParameters param = new DALConversionParameters();
		XLog newlog = converter.convert(testLog, param);

		/*
		 * Verify result
		 */
		Map<String, Map<String, EnumDataAccessType>> resMap = param.getMapEvent2DataKey2IO();
		Map<String, Map<String, EnumDataAccessType>> solMap = TestDataConvAlg.getTestDataMap();

		if(!resMap.keySet().containsAll(solMap.keySet()) || !solMap.keySet().containsAll(resMap.keySet())){
			out.println("Event classes do not match");
		}
		Set<String> eclasses = resMap.keySet();
		eclasses.retainAll(solMap.keySet());
		
		int pos = 0;
		int neg = 0;
		for(String eclass : eclasses){
			Set<String> datakeys = resMap.get(eclass).keySet();
			if(!resMap.get(eclass).keySet().containsAll(solMap.get(eclass).keySet()) ||
					!resMap.get(eclass).keySet().containsAll(solMap.get(eclass).keySet())) {
				out.println("E[ " +eclass +   " ] data do not match");
			}

			datakeys.retainAll(solMap.get(eclass).keySet());
			for(String key : datakeys){
				EnumDataAccessType resType = resMap.get(eclass).get(key);
				EnumDataAccessType solType = solMap.get(eclass).get(key);
				
				if(!resType.equals(solType)){
					neg++;
					out.println("["+ eclass +  "," + key +"] expected " + solType + "/ got " + resType);
				} else {
					pos++;
				}
			}
			
		}
		out.println("Pos : " + pos);
		out.println("Neg : " + neg);
			
		out.close();
		
		return newlog;
	}

}
