package org.processmining.partialorder.models.graph.node;

import org.deckfour.xes.extension.std.XIdentityExtension;
import org.deckfour.xes.model.XEvent;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.partialorder.models.graph.PONode;
import org.processmining.partialorder.models.graph.PartialOrderGraph;
import org.processmining.partialorder.models.palignment.Move;

/**
 * An abstract node representation for "a move" in the po alignment graph. The
 * possible extended nodes are {@link POSyncMoveNode}, {@link POModelMoveVisibleNode},
 * and {@link POLogMoveNode}
 * 
 * @author xlu
 * 
 */
public abstract class PONodeMove extends PONode {

	protected Move move;

	

	public PONodeMove(PartialOrderGraph graph, Move move) {
		super(graph);
		assert move != null;
		if(move.getEvent() !=  null){
//			assert XIdentityExtension.instance().extractID(move.getEvent()) != null;
		}
		this.move = move;
		initAttributeMap();
	}
	
	public abstract String toStringType();
	
	public Move getMove() {
		return move;
	}

	public void setMove(Move move) {
		this.move = move;
	}

	public int getStepIndex() {
		return move.getMoveIndex();
	}


	public int getEventIndex() {
		return move.getEventIndex();
	}

	public XEvent getEvent() {
		return move.getEvent();
	}


	public Transition getTransition() {
		return move.getTransition();
	}


	public boolean equals(Object o) {
		if (!(o instanceof PONodeMove)) {
			return false;
		}
		PONodeMove onode = (PONodeMove) o;

		if (!move.getClass().equals(onode.move.getClass()) ) {
			return false;
		}

		XEvent e = onode.getEvent();
		if (e != null && this.getEvent() != null) {
			return XIdentityExtension.instance().extractID(e).equals(XIdentityExtension.instance().extractID(getEvent()));
		}
		if (e == null && this.getEvent() == null) {
			//			int ostepindex = onode.getStepIndex();
			Transition otransition = onode.getTransition();
			return /* ostepindex == this.stepIndex && */otransition.equals(this.getTransition());
		}
		return false;

	}


}
