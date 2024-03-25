package org.processmining.partialorder.models.graph.node;

import java.util.ArrayList;
import java.util.List;

import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.partialorder.models.graph.PartialOrderGraph;
import org.processmining.partialorder.models.palignment.Move;
import org.processmining.partialorder.util.GraphUtil;

public abstract class POModelMoveNode extends PONodeMove {

	public POModelMoveNode(PartialOrderGraph graph, Move move) {
		super(graph, move);
	}

	public String toStringType() {
		return "Model move";
	}
	
	public Object[][] getInfo() {
		List<Object[]> infoSingleNode = new ArrayList<Object[]>();
		infoSingleNode.add(new Object[]{"Trace index ", getTrace()} );
		infoSingleNode.add(new Object[]{"Step nr.", getStepIndex()});
		infoSingleNode.add(new Object[]{"Step type", toStringType()});
	
			Transition t = getTransition();
			if(t != null){
	//			int size = t.getVisiblePredecessors().size() + t.getVisibleSuccessors().size() + 1;
	//			Object[][] infoSingleNode = new Object[size][2];
				
				infoSingleNode.add(new Object[]{"Transition", t.getLabel() == null ? "Tow" : t.getLabel()});
				infoSingleNode.add(new Object[]{"Invisible", String.valueOf(t.isInvisible())});
				
				for( Transition pre : GraphUtil.getPredessors(t.getGraph(), t)){
					Object[] values = new Object[]{"Predecessor", pre.getLabel()};
					infoSingleNode.add(values);				
				}
				for( Transition suc : GraphUtil.getSuccessors(t.getGraph(), t)){
					Object[] values = new Object[]{"Successor", suc.getLabel()};
					infoSingleNode.add(values);				
				}
				
			}
			return infoSingleNode.toArray(new Object[0][]);
		
	}

}
