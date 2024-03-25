package org.processmining.partialorder.models.palignment.move;

import org.apache.commons.math3.exception.NullArgumentException;
import org.deckfour.xes.model.XEvent;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.partialorder.models.palignment.Move;

public class MoveLogImp extends MoveAbstract {
	

	public MoveLogImp(int moveIndex, int eventIndex, XEvent evt) {
		super(moveIndex);
		
//		assert evt != null;
		
		this.event = evt;
		this.eventIndex = eventIndex;
	}

	public boolean isLogMove() {
		return true;
	}

	public boolean isVisibleModelMove() {
		return false;
	}

	public boolean isSilentModelMove() {
		return false;
	}

	public boolean isSyncMove() {
		return false;
	}

	public boolean isModelMove() {
		return false;
	}
	
	@Override
	public Transition getTransition(){
		throw new NullArgumentException();
	}

	public Move clone() {
		return new MoveLogImp(this.moveIndex, this.eventIndex, this.event);
	}

	
}
