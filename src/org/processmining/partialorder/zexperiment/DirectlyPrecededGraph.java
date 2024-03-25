package org.processmining.partialorder.zexperiment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.processmining.partialorder.models.dependency.Dependency;
import org.processmining.partialorder.models.graph.node.PONodeMove;

import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class DirectlyPrecededGraph extends DirectedSparseGraph<String, Dependency<String>>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5410147498252568013L;
	
	private Map<String, Boolean> mapTrans2Activated = new HashMap<String, Boolean>();
	private List<Set<Dependency<String>>> choiceDependencies = new ArrayList<Set<Dependency<String>>>();
	
	public void deactivateAllTransitions(){
		for(String v : this.getVertices()){
			activateTransition(v, false);
		}
	}
	
//	public void activateTransition(AbstractPOAlignNode move){
//		String l = getContainedLabel(move);
//		if(l != null){
//			activateTransition(l, true);
//		} else {
//			System.out.println("Try to activate : " + l + ", but not found");
//		}
//		
//	}
	
	public String getContainedLabel(PONodeMove move){
		String l = null;
		if(move.getTransition() != null){
			l = move.getTransition().getLabel();
		} else {
			// TODO this is adhoc. Change!
			l = XConceptExtension.instance().extractName(move.getEvent());
		}
		return l;
	}
	
	private void activateTransition(String transitionLabel, boolean activated){
		if(this.getVertices().contains(transitionLabel)){
			mapTrans2Activated.put(transitionLabel, activated);
		} else {
			//throw new IllegalArgumentException();
		}
	}
	
	
	public boolean isActivated(String transitionLabel) throws Exception{
		if(this.getVertices().contains(transitionLabel)){
			if(mapTrans2Activated.containsKey(transitionLabel)){
				return mapTrans2Activated.get(transitionLabel);
			} else {
				boolean defaultActivated = true;
				mapTrans2Activated.put(transitionLabel, defaultActivated);
				return defaultActivated;
			}
			
		}
		throw new IllegalArgumentException();
	}

	public Set<Dependency<String>> getActivatedDirPrecedings() {
		Set<Dependency<String>> activated = new HashSet<Dependency<String>>();
		try {
			for (Dependency<String> edge : this.getEdges()) {
				if (isActivated(edge)) {
					activated.add(edge);
				}
			}
		} catch (Exception e) {
			// This will never happen because the labels of nodes of edges are legal. 
		}
		return activated;
	}

	private boolean isActivated(Dependency<String> edge) throws Exception {
		return isActivated(edge.getSource()) && isActivated(edge.getTarget());
	}

	public void addChoiceDepedencies(Set<Dependency<String>> xorDepedencies) {
		this.choiceDependencies.add(xorDepedencies);		
	}

	public Set<Dependency<String>> getChoiceDependencySet(Dependency<String> e) {
		for(Set<Dependency<String>> choiceSet : this.choiceDependencies){
			if(choiceSet.contains(e)){
				return choiceSet;
			}
		}
		return null;
	}
	
	

	

}
