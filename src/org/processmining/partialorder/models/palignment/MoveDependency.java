package org.processmining.partialorder.models.palignment;

import org.processmining.partialorder.models.dependency.Dependency;
import org.processmining.partialorder.models.dependency.PDependency;

public interface MoveDependency extends Dependency<Move> {

	public boolean isModelDependency();
	public boolean isLogDependency();
	public boolean isSyncDependency();
	
	public PDependency getEventDependency();
	

	
}
