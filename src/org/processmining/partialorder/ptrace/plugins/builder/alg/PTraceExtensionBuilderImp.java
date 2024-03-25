package org.processmining.partialorder.ptrace.plugins.builder.alg;

import java.util.Collection;
import java.util.Map;

import org.deckfour.xes.id.XID;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeID;
import org.deckfour.xes.model.XAttributeList;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.processmining.partialorder.models.dependency.DependencyFactory;
import org.processmining.partialorder.ptrace.model.PTrace;
import org.processmining.partialorder.ptrace.model.imp.PTraceImp;
import org.processmining.partialorder.ptrace.param.PTraceParameter;

import gnu.trove.map.hash.THashMap;

public class PTraceExtensionBuilderImp extends PTraceBuilderAlgAbstract implements PTraceBuilderAlg {

	public PTraceExtensionBuilderImp(int traceIndex, XTrace t, PTraceParameter param) {
		super(traceIndex, t, param);
	}

	public PTrace computePTrace(XTrace t, int traceIndex) {
		int traceSize = t.size();

		PTrace ptrace = new PTraceImp(t, traceIndex);

		if (traceSize == 0) {
			return ptrace;
		}

		Map<XID, Integer> mapId2Index = new THashMap<>();

		for (int i = 0; i < traceSize; i++) {
			XEvent e = t.get(i);
			ptrace.addEvent(i);
			XAttribute xid = e.getAttributes().get("po:id");
			if (xid != null && xid instanceof XAttributeID) {
				mapId2Index.put(((XAttributeID) xid).getValue(), i);
			}			
		}
		XAttribute listDeps = t.getAttributes().get("po:dependencies");
		if(listDeps != null && listDeps instanceof XAttributeList) {
			Collection<XAttribute> deps = ((XAttributeList)listDeps).getCollection();
			for(XAttribute source : deps){
				XID sourceId = ((XAttributeID)source).getValue();
				XID targetId = ((XAttributeID)source.getAttributes().get("po:target")).getValue();
				int sourceIndex = mapId2Index.get(sourceId);
				int targetIndex = mapId2Index.get(targetId);
				ptrace.addDependency(DependencyFactory.createSimpleDirectDependency(sourceIndex, targetIndex), sourceIndex, targetIndex);
				
			}
			
		}
	
		
		return ptrace;
//		return null;
	}

}
