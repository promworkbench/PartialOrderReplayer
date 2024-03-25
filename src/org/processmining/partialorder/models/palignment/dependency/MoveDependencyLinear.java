package org.processmining.partialorder.models.palignment.dependency;

import org.processmining.partialorder.models.dependency.PDependency;
import org.processmining.partialorder.models.palignment.Move;


public class MoveDependencyLinear extends MoveDependencyAbtract {
	private int stepIndex;

	public MoveDependencyLinear(Move source, Move target) {
		super(source, target);
	}

	public MoveDependencyLinear(Move source, Move target, int stepIndex) {
		super(source, target);
		this.setStepIndex(stepIndex);
	}

	public boolean isModelDependency() {
		return false;
	}

	public boolean isLogDependency() {
		return false;
	}

	public boolean isSyncDependency() {
		return false;
	}

	public int getStepIndex() {
		return stepIndex;
	}

	public void setStepIndex(int stepIndex) {
		this.stepIndex = stepIndex;
	}

	public PDependency getEventDependency() {
		return null;
	}



}
