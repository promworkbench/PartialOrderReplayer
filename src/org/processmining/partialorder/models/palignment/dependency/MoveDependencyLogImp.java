package org.processmining.partialorder.models.palignment.dependency;

import org.processmining.partialorder.models.dependency.PDependency;
import org.processmining.partialorder.models.palignment.Move;

public class MoveDependencyLogImp extends MoveDependencyAbtract implements MoveDependencyLog {
	PDependency eventDependency;

	public MoveDependencyLogImp(Move source, Move target) {
		super(source, target);
	}

	public MoveDependencyLogImp(Move source, Move target, PDependency r) {
		super(source, target);
		//REMOVE
		if(r == null){
			System.err.println("Log relation is null");
		}
		eventDependency = r;
	}

	public boolean isModelDependency() {
		return false;
	}

	public boolean isLogDependency() {
		return true;
	}

	public boolean isSyncDependency() {
		return false;
	}

	public PDependency getEventDependency() {
		return eventDependency;
	}

}
