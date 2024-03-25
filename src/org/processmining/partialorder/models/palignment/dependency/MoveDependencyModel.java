package org.processmining.partialorder.models.palignment.dependency;

import org.processmining.partialorder.models.dependency.PDependency;
import org.processmining.partialorder.models.palignment.Move;

public class MoveDependencyModel extends MoveDependencyAbtract {



	public MoveDependencyModel(Move source, Move target) {
		super(source, target);
	}

	public boolean isModelDependency() {
		return true;
	}

	public boolean isLogDependency() {
		return false;
	}

	public boolean isSyncDependency() {
		return false;
	}

	public PDependency getEventDependency() {
		return null;
	}

}
