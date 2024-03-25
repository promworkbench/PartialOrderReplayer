package org.processmining.partialorder.dal.models;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;

public class EventAttrEntity {

	private XEvent event;
	private XAttribute attribute;
	private boolean isDiffThanPrev;
	private int eventIndex;
	
	
	public EventAttrEntity(XEvent event, XAttribute value) {
		this.event = event;
		this.attribute = value;
		this.setDiffThanPrev(false);
		this.setEventIndex(-1);
	}


	public XEvent getEvent() {
		return event;
	}


	public void setEvent(XEvent event) {
		this.event = event;
	}


	public XAttribute getAttribute() {
		return attribute;
	}


	public void setAttribute(XAttribute attribute) {
		this.attribute = attribute;
	}


	public boolean isDiffThanPrev() {
		return isDiffThanPrev;
	}


	public void setDiffThanPrev(boolean isDiffThanPrev) {
		this.isDiffThanPrev = isDiffThanPrev;
	}


	public void setEventIndex(int i) {
		eventIndex = i;
		
	}
	
	public int getEventIndex(){
		return eventIndex;
	}
	
	public String toString(){
		return String.valueOf(eventIndex) 
				+ " " + XConceptExtension.instance().extractName(event) 
				+ " " + attribute.getKey()
				+ " " + this.isDiffThanPrev;
	}

}
