package org.processmining.partialorder.models.dependency;

public class DependencyFactory {
	
	
	
	public static PDependency createSimpleDirectDependency(int sourceId, int targetId){
		return new PDependencyImp(sourceId, targetId, true);
	}

	public static PDependency createDirectlyDependency(int sourceId, int targetId){
		PDependency dep = new PDependencyImp(sourceId, targetId, true);
		dep.setRelationType(RelationType.Directly);
		return dep;
	}
	
	public static PDependency createConcurrentDependency(int sourceId, int targetId){
		PDependency dep = new PDependencyImp(sourceId, targetId, true);
		dep.setRelationType(RelationType.Concurrent);
		return dep;
	}
	
	public static PDependency createEventuallyDependency(int sourceId, int targetId){
		PDependency dep = new PDependencyImp(sourceId, targetId, true);
		dep.setRelationType(RelationType.Directly);
		return dep;
	}
	
	public static PDependency createUncertainDependency(int sourceId, int targetId){
		PDependency dep = new PDependencyImp(sourceId, targetId, true);
		dep.setRelationType(RelationType.Directly);
		return dep;
	}
}
