package org.processmining.extension;
import java.net.URI;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.info.XGlobalAttributeNameMap;
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeContainer;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

/**
 * This extension provides input attributes and output attributes 
 * for logs, traces and events 
 *
 * It defines two attributes:
 * - data:input: whose nested attributes are input attributes 
 * read by logs, traces or events
 * 
 * - data:output: whose nested attributes are output attributes
 * written by logs, traces or events
 * 
 * 
 */
public class XDataExtension  extends XExtension {
	

	private static final long serialVersionUID = 6205629952637565602L;

	/**
	 * Unique URI of this extension.
	 */
	public static final URI EXTENSION_URI = URI
			.create("TODO");
	
	/**
	 * Keys for the attributes
	 */
	public static final String KEY_INPUT = "data:input";
	public static final String KEY_OUTPUT = "data:output";
	
	/**
	 * Attribute prototypes
	 */
	public static XAttributeLiteral ATTR_INPUT;
	public static XAttributeLiteral ATTR_OUTPUT;
	
	/**
	 * Singleton instance of this extension.
	 */
	private transient static XDataExtension singleton = new XDataExtension();
	
	
//	/**
//	 * Default value for input and output header attribute
//	 */
//	public static final String VALUE_DEFAULT_HEADER = "MULTIPLE";
	/**
	 * Provides access to the singleton instance.
	 * 
	 * @return Singleton extension.
	 */
	public static XDataExtension instance() {
		return singleton;
	}

	private Object readResolve() {
		return singleton;
	}
	
	/**
	 * Private constructor
	 */
	private XDataExtension() {
		super("Data", "data", EXTENSION_URI);
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		ATTR_INPUT = factory.createAttributeLiteral(KEY_INPUT, "__INVALID__", this);
		ATTR_OUTPUT = factory.createAttributeLiteral(KEY_OUTPUT, "__INVALID__", this);
		
		this.logAttributes.add((XAttribute) ATTR_INPUT.clone());
		this.logAttributes.add((XAttribute) ATTR_OUTPUT.clone());
		this.traceAttributes.add((XAttribute) ATTR_INPUT.clone());
		this.traceAttributes.add((XAttribute) ATTR_OUTPUT.clone());
		this.eventAttributes.add((XAttribute) ATTR_INPUT.clone());
		this.eventAttributes.add((XAttribute) ATTR_OUTPUT.clone());
		// register aliases
		XGlobalAttributeNameMap.instance().registerMapping(
			XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_INPUT, "Input Attributes");
		XGlobalAttributeNameMap.instance().registerMapping(
			XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_OUTPUT, "Output Attributes");
	}
	
	/**
	 * Retrieves all input attributes of a log as a XAttributeMap
	 * 
	 * @param log
	 *            Log to retrieve input attributes for.
	 * @return The XAttributeMap containing input attributes.
	 */
	public XAttributeMap extractInputAttributes(XLog log){
		return extractInputAttributesPrivate(log);
	}
	
	/**
	 * Retrieves all input attributes of a trace as a XAttributeMap
	 * @param trace
	 *            Trace to retrieve input attributes for.
	 * @return The XAttributeMap containing input attributes.
	 */
	public XAttributeMap extractInputAttributes(XTrace trace){
		return extractInputAttributesPrivate(trace);
	}
	
	/**
	 * Retrieves all input attributes of an event as a XAttributeMap
	 * 
	 * @param event
	 *            Event to retrieve input attributes for.
	 * @return The XAttributeMap containing input attributes.
	 */
	public XAttributeMap extractInputAttributes(XEvent event){
		return extractInputAttributesPrivate(event);
	}

	/*
	 * Retrieves all input attributes of an element as a XAttributeMap
	 * 
	 * @param element
	 *            Element to retrieve input attributes for.
	 * @return The XAttributeMap containing input attributes.
	 */
	private XAttributeMap extractInputAttributesPrivate(XAttributable element){
		XAttribute attribute = element.getAttributes().get(KEY_INPUT);
		if (attribute == null) {
			return null;
		} else {
			return attribute.getAttributes();
		}
	}
	
	/**
	 * Retrieves all output attributes of a log as a XAttributeMap
	 * 
	 * @param log
	 *            Log to retrieve output attributes for.
	 * @return The XAttributeMap containing output attributes.
	 */
	public XAttributeMap extractOutputAttributes(XLog log){
		return extractOutputAttributesPrivate(log);
	}
	
	/**
	 * Retrieves all output attributes of a trace as a XAttributeMap
	 * 
	 * @param trace
	 *            Trace to retrieve output attributes for.
	 * @return The XAttributeMap containing output attributes.
	 */
	public XAttributeMap extractOutputAttributes(XTrace trace){
		return extractOutputAttributesPrivate(trace);
	}

	/**
	 * Retrieves all output attributes of an event as a XAttributeMap
	 * 
	 * @param event
	 *            Event to retrieve output attributes for.
	 * @return The XAttributeMap containing output attributes.
	 */
	public XAttributeMap extractOutputAttributes(XEvent event){
		return extractOutputAttributesPrivate(event);
	}

	/*
	 * Retrieves all output attributes of an element as a XAttributeMap
	 * 
	 * @param element
	 *            Element to retrieve output attributes for.
	 * @return The XAttributeMap containing output attributes.
	 */
	private XAttributeMap extractOutputAttributesPrivate(XAttributable element){
		XAttribute attribute = element.getAttributes().get(KEY_OUTPUT);
		if (attribute == null) {
			return null;
		} else {
			return attribute.getAttributes();
		}
	}
	
	
	public void clearInputAttributes(XEvent e){
		if(extractInputAttributes(e) != null){
			extractInputAttributes(e).clear();
		}
	}
	
	public void clearOutputAttributes(XEvent e){
		if(extractOutputAttributes(e) != null){
			extractOutputAttributes(e).clear();
		}
	}
	
	
	/**
	 * Add an input attribute to a log 
	 * 
	 * @param log
	 *            Log to add the input attribute to.
	 * @param attribute
	 * 			  The input attribute to be added.
	 */
	public void assignInputAttributes(XLog log, XAttribute attribute){
		assignInputAttributesPrivate(log, attribute);
	}
	
	/**
	 * Add an input attribute to a trace 
	 * 
	 * @param trace
	 *            Trace to add the input attribute to.
	 * @param attribute
	 * 			  The input attribute to be added.
	 */
	public void assignInputAttributes(XTrace trace, XAttribute attribute){
		assignInputAttributesPrivate(trace, attribute);
	}

	/**
	 * Add an input attribute to an event 
	 * 
	 * @param event
	 *            Event to add the input attribute to.
	 * @param attribute
	 * 			  The input attribute to be added.
	 */
	public void assignInputAttributes(XEvent event, XAttribute attribute){
		assignInputAttributesPrivate(event, attribute);
	}

	/*
	 * Add an input attribute to an element 
	 * 
	 * @param element
	 *            Element to add the input attribute to.
	 * @param attribute
	 * 			  The input attribute to be added.
	 */
	private void assignInputAttributesPrivate(XAttributable element, XAttribute attribute){
		XAttribute inputAttr = element.getAttributes().get(KEY_INPUT);
		if (inputAttr == null){
			// Add an input attribute as container
			XFactory factory = XFactoryRegistry.instance().currentDefault();
			XAttributeContainer newInputAttr = factory.createAttributeContainer(KEY_INPUT, this);
			newInputAttr.getAttributes().put(attribute.getKey(), attribute);
			element.getAttributes().put(KEY_INPUT, newInputAttr);
			
		} else if (!(inputAttr instanceof XAttributeContainer)) {
			// Change the input attribute to a container
			XFactory factory = XFactoryRegistry.instance().currentDefault();
			XAttributeContainer newInputAttr = factory.createAttributeContainer(KEY_INPUT, this);
			
			newInputAttr.getAttributes().put(attribute.getKey(), attribute);
			newInputAttr.getAttributes().putAll(inputAttr.getAttributes());
			
			element.getAttributes().put(KEY_INPUT, newInputAttr);
			
		} else {
			inputAttr.getAttributes().put(attribute.getKey(), attribute);
		}			
				
	}
	
	
	/**
	 * Add an output attribute to a log 
	 * 
	 * @param log
	 *            Log to add the output attribute to.
	 * @param attribute
	 * 			  The output attribute to be added.
	 */
	public void assignOutputAttributes(XLog log, XAttribute attribute){
		assignOutputAttributesPrivate(log, attribute);
	}
	
	/**
	 * Add an output attribute to a trace 
	 * 
	 * @param trace
	 *            Trace to add the output attribute to.
	 * @param attribute
	 * 			  The output attribute to be added.
	 */
	public void assignOutputAttributes(XTrace trace, XAttribute attribute){
		assignOutputAttributesPrivate(trace, attribute);
	}

	/**
	 * Add an output attribute to an event 
	 * 
	 * @param event
	 *            Event to add the output attribute to.
	 * @param attribute
	 * 			  The output attribute to be added.
	 */
	public void assignOutputAttributes(XEvent event, XAttribute attribute){
		assignOutputAttributesPrivate(event, attribute);
	}

	/*
	 * Add an output attribute to an element 
	 * 
	 * @param element
	 *            Element to add the output attribute to.
	 * @param attribute
	 * 			  The output attribute to be added.
	 */
	private void assignOutputAttributesPrivate(XAttributable element, XAttribute attribute){
		XAttribute outputAttr = element.getAttributes().get(KEY_OUTPUT);
		if (outputAttr == null){
			// Add output attribute
			XFactory factory = XFactoryRegistry.instance().currentDefault();
			XAttributeContainer newOutputAttr = factory.createAttributeContainer(KEY_OUTPUT, this);
			newOutputAttr.getAttributes().put(attribute.getKey(), attribute);
			element.getAttributes().put(KEY_OUTPUT, newOutputAttr);
		} else if (!(outputAttr instanceof XAttributeContainer)) {
			// Change the input attribute to a container
			XFactory factory = XFactoryRegistry.instance().currentDefault();
			XAttributeContainer newOutputAttr = factory.createAttributeContainer(KEY_OUTPUT, this);
			
			newOutputAttr.getAttributes().put(attribute.getKey(), attribute);
			newOutputAttr.getAttributes().putAll(outputAttr.getAttributes());
			
			element.getAttributes().put(KEY_OUTPUT, newOutputAttr);
			
		} else {
			outputAttr.getAttributes().put(attribute.getKey(), attribute);
		}
	}
}
