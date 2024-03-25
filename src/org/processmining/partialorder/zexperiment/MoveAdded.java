package org.processmining.partialorder.zexperiment;

import org.deckfour.xes.extension.std.XIdentityExtension;
import org.deckfour.xes.id.XID;
import org.deckfour.xes.model.XEvent;

public class MoveAdded extends MoveNoise {
	
	private XEvent newEvent;
	private int newIndex;
	
	public MoveAdded(XEvent originalEvent, XEvent newEvent){
		this.origEvent = originalEvent;
		this.newEvent = newEvent;
	}
	
	public void setNewIndex(int newIndex) {
		this.newIndex = newIndex;
	}

	public XEvent getNewEvent() {
		return newEvent;
	}


	public int getNewIndex() {
		return newIndex;
	}
	
	public XID getNewEventId(){
		return XIdentityExtension.instance().extractID(newEvent);
	}


	

}
