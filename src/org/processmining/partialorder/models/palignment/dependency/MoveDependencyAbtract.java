package org.processmining.partialorder.models.palignment.dependency;

import org.processmining.partialorder.models.palignment.Move;
import org.processmining.partialorder.models.palignment.MoveDependency;

public abstract class MoveDependencyAbtract implements MoveDependency {

	boolean isDirect;
	private Move source;
	private Move target;
	
	public MoveDependencyAbtract(Move source, Move target){
		this.source = source;
		this.target = target;
	}
	
	public boolean isDirect() {
		return isDirect;
	}

	public void setDirect(boolean b) {
		isDirect = b;
	}

	public Move getSource() {
		return source;
	}

	public Move getTarget() {
		return target;
	}



}
