package org.processmining.partialorder.models.palignment.dependency;

import org.processmining.partialorder.models.dependency.PDependency;
import org.processmining.partialorder.models.palignment.Move;

public class MoveDependencySync extends MoveDependencyAbtract implements MoveDependencyLog {
	PDependency eventDependency;
	
	public MoveDependencySync(Move source, Move target) {
		super(source, target);
	}

	public MoveDependencySync(Move source, Move target, PDependency r) {
		super(source, target);
		eventDependency = r;
	}

	public boolean isModelDependency() {
		return true;
	}

	public boolean isLogDependency() {
		return true;
	}

	public boolean isSyncDependency() {
		return true;
	}

	public PDependency getEventDependency() {
		return eventDependency;
	}
	
	

}
