package org.processmining.partialorder.models.palignment.move;

import org.deckfour.xes.model.XEvent;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.partialorder.models.palignment.Move;

public class MoveSyncImp extends MoveAbstract {

	public MoveSyncImp(int stepIndex, int eventIndex, XEvent evt, Transition origTrans) {
		super(stepIndex);
//		assert evt != null;
//		assert eventIndex >= 0;
//		assert origTrans != null;
		this.event = evt;
		this.eventIndex = eventIndex;
		this.transition = origTrans;
	}

	public boolean isLogMove() {
		return false;
	}

	public boolean isVisibleModelMove() {
		return false;
	}

	public boolean isSilentModelMove() {
		return false;
	}

	public boolean isModelMove() {
		return false;
	}

	public boolean isSyncMove() {
		return true;
	}
	
	public Move clone() {
		return new MoveSyncImp(moveIndex, eventIndex, event, transition);
	}

}
