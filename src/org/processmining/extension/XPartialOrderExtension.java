package org.processmining.extension;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.extension.std.XIdentityExtension;
import org.deckfour.xes.id.XID;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeBoolean;
import org.deckfour.xes.model.XAttributeContainer;
import org.deckfour.xes.model.XAttributeID;
import org.deckfour.xes.model.XAttributeList;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeBooleanImpl;
import org.deckfour.xes.model.impl.XAttributeContainerImpl;
import org.deckfour.xes.model.impl.XAttributeIDImpl;
import org.deckfour.xes.model.impl.XAttributeListImpl;
import org.processmining.partialorder.models.dependency.DependencyXID;

public class XPartialOrderExtension extends XExtension {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5947708638378447412L;

	public static final URI EXTENSION_URI = URI.create("TODO");

	private static final String KEY_ISPARTIALORDER = "po:isPartialOrder";
	private static final String KEY_DEPENDENCIES = "po:dependencies";
	private static final String KEY_DEPENDENCY = "po:dependency";
	private static final String KEY_PREDESSOR = "po:pred";
	private static final String KEY_SUCCESSOR = "po:succ";


	private transient static XPartialOrderExtension singleton = new XPartialOrderExtension();

	public static XPartialOrderExtension instance() {
		return singleton;
	}

	protected XPartialOrderExtension() {
		super("Po", "po", EXTENSION_URI);

	}

	public boolean isPartiallyOrderedLog(XLog log) {
		XAttributeMap logAttributes = log.getAttributes();
		if (logAttributes.keySet().contains(KEY_ISPARTIALORDER)) {
			XAttributeBoolean isPartialOrder = (XAttributeBoolean) logAttributes.get(KEY_ISPARTIALORDER);
			return isPartialOrder.getValue();
		}
		return false;
	}

	public void assignLogPartiallyOrdered(XLog log, boolean isPartialOrder) {
		XAttributeMap logAttributes = log.getAttributes();
		XAttribute isPartialOrderAttr = new XAttributeBooleanImpl(KEY_ISPARTIALORDER, isPartialOrder, this);
		logAttributes.put(KEY_ISPARTIALORDER, isPartialOrderAttr);
	}

	public void addDependency(XTrace t, XEvent pred, XEvent succ) {
		XAttributeList dependencyList = getDependencyList(t);
		XAttributeContainer dependencyContainer = getDependencyContainter(pred, succ);
		dependencyList.addToCollection(dependencyContainer);		
	}

	private XAttributeList getDependencyList(XTrace t) {
		XAttributeMap traceAttributes = t.getAttributes();
		if (!traceAttributes.containsKey(KEY_DEPENDENCIES)) {
			traceAttributes.put(KEY_DEPENDENCIES, new XAttributeListImpl(KEY_DEPENDENCIES, this));
		}
		return (XAttributeList) traceAttributes.get(KEY_DEPENDENCIES);
	}
	
	private XAttributeContainer getDependencyContainter(XEvent pred, XEvent succ) {
		XAttributeContainer dependencyContainer = new XAttributeContainerImpl(KEY_DEPENDENCY, this);
		XAttributeID predIdAttr = getAttributeXID(pred, KEY_PREDESSOR, this);
		XAttributeID succIdAttr = getAttributeXID(succ, KEY_SUCCESSOR, this);
//		dependencyContainer.addToCollection(predIdAttr); // doesn't work
//		dependencyContainer.addToCollection(succIdAttr);
		dependencyContainer.getAttributes().put(KEY_PREDESSOR, predIdAttr);
		dependencyContainer.getAttributes().put(KEY_SUCCESSOR, succIdAttr);
		return dependencyContainer;
	}
	
	private XAttributeID getAttributeXID(XEvent event, String attributeKey, XExtension extension) {
		XID id = (XID) XIdentityExtension.instance().extractID(event).clone();
		return new XAttributeIDImpl(attributeKey, id, this);
	}	

	public List<DependencyXID> extractDependencies(XTrace trace) {
		List<DependencyXID> result = new ArrayList<DependencyXID>();
		XAttributeList dependencyList = getDependencyList(trace);
		for(XAttribute attr : dependencyList.getCollection()){
			DependencyXID dep = extractDependency((XAttributeContainer) attr);
			result.add(dep);
		}	
		return result;
	}

	private DependencyXID extractDependency(XAttributeContainer attr) {
		XID predId = extractIdFromDependency(attr, KEY_PREDESSOR);
		XID succId = extractIdFromDependency(attr, KEY_SUCCESSOR);
		return new DependencyXID(predId, succId);
	}

	private XID extractIdFromDependency(XAttributeContainer attr, String key) {
		assert attr.getAttributes().containsKey(key);
		return ((XAttributeID) attr.getAttributes().get(key)).getValue();
	}


}
