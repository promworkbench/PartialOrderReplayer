package org.processmining.partialorder.models.palignment.move;

import org.deckfour.xes.model.XEvent;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

public abstract class MoveModel extends MoveAbstract {
	
	public MoveModel(int moveIndex, Transition t){
		super(moveIndex);
		//REMOVE
		if(t == null){
			try {
				throw new Exception("Move model with null transition");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.transition = t;
	}

	public boolean isLogMove(){
		return false;
	}
	public boolean isModelMove(){
		return true;
	}
	
	public boolean isSyncMove(){
		return false;
	}
	
	
	@Override
	public XEvent getEvent(){
		return null;
	}
	

	public int getEventIndex(){
		return -2;
	}
	
	
}
