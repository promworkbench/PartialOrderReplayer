package org.processmining.partialorder.models.palignment;

import org.deckfour.xes.model.XEvent;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

public interface Move {
	
	public boolean isLogMove();
	public boolean isVisibleModelMove();
	public boolean isSilentModelMove();
	public boolean isModelMove();
	public boolean isSyncMove();
	
	public XEvent getEvent();
//	public void setEvent(XEvent event);
	public int getEventIndex();
//	public void setEventIndex(int eventIndex);
	
	public Transition getTransition();
//	public void setTransition(Transition transition);
	
	public int getMoveIndex();
	public void setEvent(XEvent xEvent);
	public Move clone();
	
	
	

}
