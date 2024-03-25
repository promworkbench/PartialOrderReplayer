package org.processmining.partialorder.ptrace.plugins.builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.extension.std.XIdentityExtension;
import org.deckfour.xes.id.XID;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.extension.XPartialOrderExtension;
import org.processmining.partialorder.models.dependency.DependencyXID;
import org.processmining.partialorder.models.dependency.PDependencyDataAware;
import org.processmining.partialorder.ptrace.model.PLog;
import org.processmining.partialorder.ptrace.model.PTrace;
import org.processmining.partialorder.ptrace.model.imp.PLogImp;
import org.processmining.partialorder.ptrace.model.imp.PTraceImp;
import org.processmining.partialorder.ptrace.plugins.builder.alg.PTraceUtil;

public class XLogWithParOExtensionToPLogConverter {

	/**
	 * This function converts a log extended with partial order info to a PLog
	 */
	public static PLog convertLog(XLog log, boolean isComputeTransitive) {
		PLog plog = new PLogImp(log);
		for (int i = 0; i < log.size(); i++) {
			XTrace t = log.get(i);
			PTrace p = convertTrace(t, i, isComputeTransitive);
			plog.add(i, p);
		}
		return plog;
	}
	
	private static PTrace convertTrace(XTrace trace, int index, boolean isComputeTransitive) {
		PTrace ptrace = new PTraceImp(trace, index);
		Map<XID, Integer> mapId2Index = new HashMap<XID, Integer>();

		// Store events
		for (int i = 0; i < trace.size(); i++) {
			ptrace.addEvent(i);
			XID eid = XIdentityExtension.instance().extractID(trace.get(i));
			mapId2Index.put(eid, i);
		}
		// Store dependencies
		List<DependencyXID> dependencies = XPartialOrderExtension.instance().extractDependencies(trace);
		for (DependencyXID dep : dependencies) {
			int predIndex = mapId2Index.get(dep.getSource());
			int succIndex = mapId2Index.get(dep.getTarget());
			ptrace.addDependency(new PDependencyDataAware(predIndex, succIndex), predIndex, succIndex);
		}
		
		if(isComputeTransitive){
			PTraceUtil.computeAndSetTransitiveReduction(ptrace);
		}
		
		return ptrace;
	}
	
	
	
}
