package org.processmining.partialorder.models.palignment.move;

import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.partialorder.models.palignment.Move;

public class MoveModelInvisibleImp extends MoveModel {

	public MoveModelInvisibleImp(int moveIndex, Transition origTrans) {
		super(moveIndex, origTrans);
	}

	public boolean isVisibleModelMove() {
		return false;
	}

	public boolean isSilentModelMove() {
		return true;
	}

	public Move clone() {
		return new MoveModelInvisibleImp(moveIndex, transition);
	}

}
