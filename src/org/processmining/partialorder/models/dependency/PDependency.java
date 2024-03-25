package org.processmining.partialorder.models.dependency;

public interface PDependency extends Dependency<Integer> {

	
	public RelationType getRelationType();
	
	public void setRelationType(RelationType type);
	
	
}
