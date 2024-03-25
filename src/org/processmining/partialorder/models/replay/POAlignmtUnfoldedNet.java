package org.processmining.partialorder.models.replay;

import java.util.HashMap;
import java.util.Map;

import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetImpl;

public class POAlignmtUnfoldedNet extends PetrinetImpl {

	Map<Transition, Integer> mapTransToIndex;
	
	public POAlignmtUnfoldedNet(String label) {
		super(label);		
		mapTransToIndex = new HashMap<Transition, Integer>();
		
	}
	

	public Transition addTransition(String label, int stepIndex) {
		Transition t = addTransition(label);
		mapTransToIndex.put(t, stepIndex);
		return t;
	}
	
	
	public int getTransitionStepIndex(Transition t ){
		return mapTransToIndex.get(t);
	}

}
