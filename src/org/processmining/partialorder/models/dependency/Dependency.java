package org.processmining.partialorder.models.dependency;

public interface Dependency<T> {
	
	public boolean isDirect();
	
	public void setDirect(boolean b);
	
	public T getSource();
	public T getTarget();
	

}
