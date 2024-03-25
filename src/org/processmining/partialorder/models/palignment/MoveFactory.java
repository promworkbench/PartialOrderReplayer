package org.processmining.partialorder.models.palignment;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.partialorder.models.palignment.move.MoveLogImp;
import org.processmining.partialorder.models.palignment.move.MoveModelInvisibleImp;
import org.processmining.partialorder.models.palignment.move.MoveModelVisibleImp;
import org.processmining.partialorder.models.palignment.move.MoveSyncImp;
import org.processmining.plugins.petrinet.replayresult.StepTypes;


public class MoveFactory {

	public static Move createMove(StepTypes type, int stepIndex, int eventIndex, XEvent evt, Object origTrans) {
		Move move = null;
		try {
			if (type.equals(StepTypes.L)) {
				move = createLogMove(stepIndex, eventIndex, evt);
				
			} else if (type.equals(StepTypes.MINVI)) {
				move = createInvisibleModelMove(stepIndex, (Transition) origTrans);
				
			} else if (type.equals(StepTypes.MREAL)) {
				move = createVisibleModelMove(stepIndex, (Transition) origTrans);
				
			} else if (type.equals(StepTypes.LMGOOD)) {
				move = createSyncMove(stepIndex, eventIndex, evt, (Transition) origTrans);
				
			} else {
				throw new UnsupportedOperationException("Move type [" + type + "] not supported yet");
			}
		} catch (UnsupportedOperationException  e) {
			e.printStackTrace();
		}
		return move;
	}
	
	
	
	public static Move createSyncMove(int stepIndex, int eventIndex, XEvent evt, Transition origTrans) {
		return new MoveSyncImp(stepIndex, eventIndex, evt, origTrans);
	}

	public static Move createVisibleModelMove(int stepIndex, Transition origTrans) {
		return new MoveModelVisibleImp(stepIndex, origTrans);
	}
	
	public static Move createInvisibleModelMove(int stepIndex, Transition origTrans) {
		return new MoveModelInvisibleImp(stepIndex, origTrans);
	}

	public static Move createLogMove(int stepIndex, int eventIndex, XEvent evt){
		return new MoveLogImp(stepIndex, eventIndex, evt);
	}


	// TODO To be changed to new class match
	public static Move createSyncMoveBetweenEvents(Integer key, XEvent event, Integer value, XEvent event2) {
		return new MoveSyncImp(-1, key, event, new Transition(XConceptExtension.instance().extractName(event2), null));
	}


}
