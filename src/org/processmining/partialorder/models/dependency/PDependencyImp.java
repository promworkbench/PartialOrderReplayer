package org.processmining.partialorder.models.dependency;

public class PDependencyImp implements PDependency {

	protected int preIndex; // The event index of the predecessor
	protected int targetIndex; // the event index of the successor
	
	protected boolean isDirect = true; 
	
	protected RelationType type;
	
	

	
	public PDependencyImp(int sourceId, int targetId){
		preIndex = sourceId;
		targetIndex = targetId;
	}
	
	PDependencyImp(int sourceId, int targetId, boolean isDirect){
		preIndex = sourceId;
		targetIndex = targetId;
		this.isDirect = isDirect;
	}
	
	public boolean isDirect() {
		return isDirect;
	}

	public void setDirect(boolean b) {
		isDirect = b;
		
	}

	public Integer getSource() {
		return preIndex;
	}

	public Integer getTarget() {
		return targetIndex;
	}

	public RelationType getRelationType() {
		return this.type;
	}

	public void setRelationType(RelationType type) {
		this.type = type;		
	}



}
