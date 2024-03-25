package org.processmining.partialorder.models.projection;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.AttributeMap.ArrowType;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.ResetInhibitorNetImpl;
//import org.processmining.models.pnetprojection.PetrinetGraphP;

public class POProjectedPetrinetGraph extends ResetInhibitorNetImpl {
// TODO: see if include Arya package 
// extends PetrinetGraphP {

	protected Set<LogMoveTransition> logMoves; 
	protected Set<DataElementNode> dataNodes;
	


	public Set<DataElementNode> getDataNodes() {
		return dataNodes;
	}


	public POProjectedPetrinetGraph(String label) {
		super(label);
		logMoves = new HashSet<LogMoveTransition>();
		dataNodes = new HashSet<DataElementNode>();
	}
	
	public Set<LogMoveTransition> getLogMoveTransitions() {
		return logMoves;
	}

	
	public LogMoveTransition addLogMoveTransition(String label) {
		LogMoveTransition lm = new LogMoveTransition(label, this);
		logMoves.add(lm);
		graphElementAdded(lm);
		return lm;
	}
	
	public DataElementNode addDataElemNode(String label) {
		DataElementNode dnode = new DataElementNode(label, this);
		dataNodes.add(dnode);
		graphElementAdded(dnode);
		return dnode;
	}


	public Arc addLogMoveArc(Transition pre, LogMoveTransition t) {
		Arc a = addArcPrivate(pre, t, 1, null);	
		return a;
	}


	public Arc addLogMoveArc(LogMoveTransition t, Transition post) {
		Arc a = addArcPrivate( t, post, 1, null);	
		return a;		
	}
	
	public Arc AddDataAccessArc(Transition t, DataElementNode dnode){
		Arc a = addArcPrivate( t, dnode, 1, null);	
		initDataAccessArc(a);
		return a;
	}
	
	public Arc AddDataAccessArc(DataElementNode dnode, Transition t){
		Arc a = addArcPrivate( dnode,t,  1, null);	
		initDataAccessArc(a);
		return a;
	}
	
	public Set<PetrinetNode> getNodes(){
		Set<PetrinetNode> nodes = new HashSet<PetrinetNode>();
		nodes.addAll(this.places);
		nodes.addAll(this.transitions);
		nodes.addAll(this.logMoves);
		nodes.addAll(this.dataNodes);
		return nodes;
	}
	
	public static void initDataAccessArc(Arc a){
		a.getAttributeMap().put(AttributeMap.EDGEEND, ArrowType.ARROWTYPE_SIMPLE);
		a.getAttributeMap().put(AttributeMap.EDGECOLOR, new Color(169,169,169));
		a.getAttributeMap().put(AttributeMap.DASHPATTERN, new float[] {2, 2*2});
	}


}
