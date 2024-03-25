package org.processmining.partialorder.zexperiment;

import org.deckfour.xes.extension.std.XIdentityExtension;
import org.deckfour.xes.id.XID;
import org.deckfour.xes.model.XEvent;

public abstract class MoveNoise {

//	public enum NoiseType {
//		CopyAdded, Removed, RemoveAdded //, Replaced, Swapped
//	}

	protected XEvent origEvent;
	protected int origIndex = -1;
	
	
//	NoiseType type;
	
//	public MoveNoise(XEvent e, int original, int newIndex, NoiseType type){
//		this.event = e;
//		this.origIndex = original;
//		this.newIndex = newIndex;
//		this.type = type;
//	}
	
//	public MoveNoise(XEvent e, int removedFromIndex, NoiseType type) {
//		this(e, removedFromIndex, -1, type);
//	}

	public XEvent getOrigEvent() {
		return origEvent;
	}

	public int getOrigIndex() {
		return origIndex;
	}
	
	public XID getOrigEventId() {
		return XIdentityExtension.instance().extractID(origEvent);
	}

//	public int getNewIndex() {
//		return newIndex;
//	}

//	public NoiseType getType() {
//		return type;
//	}
}


