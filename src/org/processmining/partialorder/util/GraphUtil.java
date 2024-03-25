package org.processmining.partialorder.util;

import java.util.HashSet;
import java.util.Set;

import org.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

public class GraphUtil {
	
	public static Set<Transition> getSuccessors(PetrinetGraph model, Transition t) {
		Set<Transition> result = new HashSet<Transition>();
		for( PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> outarc : model.getOutEdges(t)){
			if(outarc.getTarget() instanceof Place){
				for( PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> ooarc: model.getOutEdges(outarc.getTarget())){
					if(ooarc.getTarget() instanceof Transition){
						Transition post = (Transition) ooarc.getTarget();
						result.add(post);
					}
				}
				
			}
		}
		return result;
	}

	public static  Set<Transition> getPredessors(PetrinetGraph model, Transition t) {
		Set<Transition> result = new HashSet<Transition>();
		for( PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> inarc : model.getInEdges(t)){
			if(inarc.getSource() instanceof Place){
				for( PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> ininarc: model.getInEdges(inarc.getSource())){
					if(ininarc.getSource() instanceof Transition){
						Transition pre = (Transition) ininarc.getSource();
						result.add(pre);
					}
				}
				
			}
		}
		return result;
	}

	public static Set<Transition> getPredessors(
			AbstractDirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> graph,
			Transition t) {
		Set<Transition> result = new HashSet<Transition>();
		for( PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> inarc : graph.getInEdges(t)){
			if(inarc.getSource() instanceof Place){
				for( PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> ininarc: graph.getInEdges(inarc.getSource())){
					if(ininarc.getSource() instanceof Transition){
						Transition pre = (Transition) ininarc.getSource();
						result.add(pre);
					}
				}
				
			}
		}
		return result;
	}

	public static Set<Transition> getSuccessors(
			AbstractDirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> graph,
			Transition t) {
		Set<Transition> result = new HashSet<Transition>();
		for( PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> outarc : graph.getOutEdges(t)){
			if(outarc.getTarget() instanceof Place){
				for( PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> ooarc: graph.getOutEdges(outarc.getTarget())){
					if(ooarc.getTarget() instanceof Transition){
						Transition post = (Transition) ooarc.getTarget();
						result.add(post);
					}
				}
				
			}
		}
		return result;
	}
	
	

}
