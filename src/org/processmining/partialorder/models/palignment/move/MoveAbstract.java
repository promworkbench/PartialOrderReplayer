package org.processmining.partialorder.models.palignment.move;

import org.deckfour.xes.model.XEvent;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.partialorder.models.palignment.Move;

public abstract class MoveAbstract implements Move {

	protected XEvent event;
	protected int eventIndex;
	
	protected Transition transition;
	
	protected int moveIndex;
	
	public MoveAbstract(int moveIndex) {
		this.moveIndex = moveIndex;
	}
	
	public abstract Move clone();
	
	public int getEventIndex() {
		return eventIndex;
	}
	public void setEventIndex(int eventIndex){
		this.eventIndex = eventIndex;
	}	
	
	public XEvent getEvent() {
		return event;
	}
	public void setEvent(XEvent event) {
		this.event = event;
	}
	public Transition getTransition() {
		return transition;
	}
	public void setTransition(Transition transition) {
		this.transition = transition;
	}
	
	public int getMoveIndex(){
		return moveIndex;
	}
	
	
	
	
	
	
}
