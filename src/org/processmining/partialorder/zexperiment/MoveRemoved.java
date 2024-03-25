package org.processmining.partialorder.zexperiment;

import org.deckfour.xes.model.XEvent;

public class MoveRemoved extends MoveNoise  {
	
	public MoveRemoved(XEvent originalEvent){
		this.origEvent = originalEvent;
	}

}
