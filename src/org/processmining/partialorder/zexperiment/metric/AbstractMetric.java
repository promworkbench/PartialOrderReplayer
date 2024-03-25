package org.processmining.partialorder.zexperiment.metric;

import java.util.HashMap;
import java.util.Map;

public class AbstractMetric {
	
	private Map<MetricProperty, Integer> map2Num;
	
	public enum MetricProperty {
		TP_SYNC_MOVE, 
		FP_SYNC_MOVE, 
		FN_SYNC_MOVE, 
		
		TP_LOG_MOVE, 
		FP_LOG_MOVE,  
		FN_LOG_MOVE,
		
		TP_MODEL_MOVE, 
		FP_MODEL_MOVE, 
		FN_MODEL_MOVE,
		
		TP_DFOLLOWED, 
		FP_DFOLLOWED,  
		FN_DFOLLOWED
	}
	
	public int getNumberOfProperty(MetricProperty key){
		
		if(map2Num.containsKey(key)){
			return map2Num.get(key);
		}
		return 0;
	}

	
	public AbstractMetric(){
		map2Num = new HashMap<MetricProperty, Integer>();
	}
	
	public void incrementPropByOne(MetricProperty property) {
		incrementProp(property, 1);
	}

	public void incrementProp(MetricProperty property, int value) {
		if(!map2Num.containsKey(property)){
			map2Num.put(property, value);
		} else {
			int v = map2Num.get(property);
			v += value;
			map2Num.put(property, v);
		}
		
	}
	
	public Map<MetricProperty, Integer> getNumbersOfProperties(){
		return map2Num;
	}
}
