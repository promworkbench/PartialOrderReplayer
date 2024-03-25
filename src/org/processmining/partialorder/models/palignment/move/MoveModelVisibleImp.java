package org.processmining.partialorder.models.palignment.move;

import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.partialorder.models.palignment.Move;

public class MoveModelVisibleImp extends MoveModel {

	public MoveModelVisibleImp(int stepIndex, Transition origTrans) {
		super(stepIndex, origTrans);
	}

	public boolean isVisibleModelMove() {
		return true;
	}

	public boolean isSilentModelMove() {
		return false;
	}

	public Move clone() {
		return new MoveModelVisibleImp(moveIndex, transition);
	}
}
