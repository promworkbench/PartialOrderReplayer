package org.processmining.partialorder.ptrace.plugins.builder.alg;

import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.processmining.annotation.PTraceBuilderAlgorithm;
import org.processmining.extension.XDataExtension;
import org.processmining.partialorder.models.dependency.PDependencyDataAware;
import org.processmining.partialorder.models.dependency.PDependencyDataAware.EnumDataDependency;
import org.processmining.partialorder.ptrace.model.PTrace;
import org.processmining.partialorder.ptrace.model.imp.PTraceImp;
import org.processmining.partialorder.ptrace.param.PTraceParameter;

@PTraceBuilderAlgorithm
public class PTraceDataAwareBuilderImp extends PTraceBuilderAlgAbstract {

	public PTraceDataAwareBuilderImp(int traceIndex, XTrace t, PTraceParameter param) {
		super(traceIndex, t, param);
	}

	public PTrace computePTrace(XTrace t, int traceIndex) {
		int traceSize = t.size();
		PTrace ptrace = new PTraceImp(t, traceIndex);

		for (int i = 0; i < traceSize; i++) {
			XEvent currentEvent = t.get(i);

			/* Add an event to the po trace */
			ptrace.addEvent(i);

			for (int j = i - 1; 0 <= j; j--) {
				XEvent predecessor = t.get(j);
				PDependencyDataAware relation = null;
				/* Get data dependency if share data attribute(s) */
				relation = getPredecessorRelation(i, j, currentEvent, predecessor);

				if (relation != null) {
					ptrace.addDependency(relation, j, i);
				} // else, no relation, skip
			}
		}
		return ptrace;
	}

	@SuppressWarnings("unused")
	private String getNewTraceName(XTrace t, int traceIndex) {
		String traceName = XConceptExtension.instance().extractName(t);
		if (traceName == null || traceName.isEmpty()) {
			traceName = "Trace " + traceIndex;
		}
		return traceName;
	}

	@SuppressWarnings("unused")
	private boolean removeAncestorsFromTransitiveClosure(boolean[] transPreds, int predIndex, int[] prePreds) {
		boolean isTCEdge = transPreds[predIndex] == false;

		/* Set parents of predecessor as not an edge in transitive closure */
		transPreds[predIndex] = true;
		if (prePreds != null) {
			for (int pre : prePreds) {
				transPreds[pre] = true;
			}
		}

		return isTCEdge;

	}

	private PDependencyDataAware getPredecessorRelation(int curIndex, int preIndex, XEvent cur, XEvent predecessor) {
		PDependencyDataAware relation = new PDependencyDataAware(preIndex, curIndex);
		XAttributeMap preO = XDataExtension.instance().extractOutputAttributes(predecessor);
		XAttributeMap preI = XDataExtension.instance().extractInputAttributes(predecessor);
		XAttributeMap curI = XDataExtension.instance().extractInputAttributes(cur);
		XAttributeMap curO = XDataExtension.instance().extractOutputAttributes(cur);

		Set<String> shareOI = shareKeys(preO, curI);
		Set<String> shareIO = shareKeys(preI, curO);
		Set<String> shareII = shareKeys(preI, curI);
		Set<String> shareOO = shareKeys(preO, curO);

		if (shareOI == null && shareIO == null && shareII == null && shareOO == null) {
			return null;
		}

		if (shareOI != null) {
			for (String key : shareOI) {
				addRelation(relation, "OI", key, preO.get(key), curI.get(key));
			}
		}
		if (shareIO != null) {
			for (String key : shareIO) {
				addRelation(relation, "IO", key, preI.get(key), curO.get(key));
			}
		}
		if (shareII != null) {
			for (String key : shareII) {
				if (!preI.get(key).equals(curI.get(key))) {
					addRelation(relation, "II", key, preI.get(key), curI.get(key));
				}
			}
		}
		if (shareOO != null) {
			for (String key : shareOO) {
				if (!preO.get(key).equals(curO.get(key))) {
					addRelation(relation, "OO", key, preO.get(key), curO.get(key));
				}
			}
		}
		if (!relation.hasDepedency()) {
			return null;
		}

		return relation;
	}

	private void addRelation(PDependencyDataAware relation, String type, String key, XAttribute a, XAttribute b) {
		EnumDataDependency dtype = null;
		if (type.equals("OI")) {
			if (a.equals(b)) {
				dtype = EnumDataDependency.OI_SameValue;
			} else {
				dtype = EnumDataDependency.OI_DiffValue;
			}
		} else if (type.equals("IO")) {
			if (a.equals(b)) {
				dtype = EnumDataDependency.IO_SameValue;
			} else {
				dtype = EnumDataDependency.IO_DiffValue;
			}
		} else if (type.equals("II")) {
			if (a.equals(b)) {
				dtype = EnumDataDependency.II_SameValue;
			} else {
				dtype = EnumDataDependency.II_DiffValue;
			}
		} else if (type.equals("OO")) {
			if (a.equals(b)) {
				dtype = EnumDataDependency.OO_SameValue;
			} else {
				dtype = EnumDataDependency.OO_DiffValue;
			}
		}

		relation.putDependency(dtype, key, a, b);

	}

	private Set<String> shareKeys(XAttributeMap e1, XAttributeMap e2) {
		if (e1 == null || e2 == null) {
			return null;
		}
		Set<String> sharedKeys = new HashSet<String>();
		for (String key : e1.keySet()) {
			if (e2.containsKey(key)) {
				sharedKeys.add(key);
			}
		}
		return sharedKeys;
	}
}
