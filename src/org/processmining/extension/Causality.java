package org.processmining.extension;

import org.deckfour.xes.id.XID;

public class Causality {
	public Causality(XID predId, XID currentId) {
		this.source = predId;
		this.target = currentId;
	}
	

	public XID source;
	public XID target;
}
