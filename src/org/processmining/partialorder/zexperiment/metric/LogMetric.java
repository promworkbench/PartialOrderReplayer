package org.processmining.partialorder.zexperiment.metric;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Map.Entry;

public class LogMetric extends AbstractMetric {

	TIntObjectMap<ExpMetricPerTrace> mapIndex2TraceMetric = new TIntObjectHashMap<ExpMetricPerTrace>();

	public void addTraceMetric(int traceIndex, ExpMetricPerTrace traceMetric) {
		for (Entry<MetricProperty, Integer> entry : traceMetric.getNumbersOfProperties().entrySet()) {
			MetricProperty property = entry.getKey();
			Integer value = entry.getValue();
			this.incrementProp(property, value);
		}

		mapIndex2TraceMetric.put(traceIndex, traceMetric);

	}
	
	public ExpMetricPerTrace getTraceMetric(int traceIndex){
		if(mapIndex2TraceMetric.containsKey(traceIndex)){
			return mapIndex2TraceMetric.get(traceIndex);
		}
		return null;
	}

}
