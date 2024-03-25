package org.processmining.partialorder.models.dependency;


public class DependencyImp<T> implements Dependency<T>{

	protected boolean isDirectlyFollowed = false;
	
	protected T source;
	protected T target;
	
	public DependencyImp(T source, T target){
		this.source = source;
		this.target = target;
	}
	
	
	public boolean isDirect() {
		return isDirectlyFollowed;
	}

	public T getSource() {
		return source;
	}

	public T getTarget() {
		return target;
	}


	public void setDirect(boolean b) {
		isDirectlyFollowed = b;
	}
	


}
