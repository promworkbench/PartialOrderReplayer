package org.processmining.partialorder.ptrace.param;


public class PAlignmentParameter extends PTraceParameter {

	private boolean isComputeLasy = true;


	public PAlignmentParameter(PTraceType type) {
		super(type);
	}

	public PAlignmentParameter() {
		super();
	}
	
	public boolean isComputeLasy(){
		return isComputeLasy;
	}
	
	public void setComputeLasy(boolean b){
		isComputeLasy = b;
	}

	

	
	
}
