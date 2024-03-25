package org.processmining.extension;

import java.util.List;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.id.XID;
import org.deckfour.xes.id.XIDFactory;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeID;
import org.deckfour.xes.model.XAttributeList;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;

public class XPOLogExtenstion {

	public static final String KEY_PO_ID = "po:id";

	private transient static XPOLogExtenstion singleton = new XPOLogExtenstion();
	XFactory factory = XFactoryRegistry.instance().currentDefault();

	public static XPOLogExtenstion instance() {
		return singleton;
	}

	private XIDFactory idfactory = XIDFactory.instance();

	protected XPOLogExtenstion() {
//		super("Po", "po", EXTENSION_URI);

	}

	public XID assignPOid(XEvent current) {
		XID id = idfactory.createId();
		current.getAttributes().put(KEY_PO_ID,
				factory.createAttributeID(KEY_PO_ID, id, null));
		return id;
	}

	public XID extractPOid(XEvent current) {
		XAttribute attr = current.getAttributes().get(KEY_PO_ID);
		XID id = ((XAttributeID) attr).getValue();
		return id;
	}

	public void putAllCauseDeps(XAttributeMap traceMap,
			List<Causality> traceListCauseDeps) {

		XAttributeList list = factory.createAttributeList("po:dependencies", null);
		for (Causality causeDep : traceListCauseDeps) {
			XAttributeID sourceId = factory.createAttributeID("po:source",
					causeDep.source, null);
			XAttributeID targetId = factory.createAttributeID("po:target",
					causeDep.target, null);

			sourceId.getAttributes().put(targetId.getKey(), targetId);
			list.addToCollection(sourceId);

		}

		traceMap.put(list.getKey(), list);
	}
}
