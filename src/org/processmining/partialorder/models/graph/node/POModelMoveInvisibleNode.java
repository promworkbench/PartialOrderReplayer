package org.processmining.partialorder.models.graph.node;

import java.awt.Dimension;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.shapes.RoundedRect;
import org.processmining.partialorder.models.graph.PartialOrderGraph;
import org.processmining.partialorder.models.palignment.Move;
import org.processmining.partialorder.plugins.vis.PGraphColorStyle;

public class POModelMoveInvisibleNode extends POModelMoveNode {
	

	public POModelMoveInvisibleNode(PartialOrderGraph graph, Move move) {
		super(graph, move);
		initAttributeMap();
	}


	protected void initAttributeMap() {		
		getAttributeMap().put(AttributeMap.SHAPE, new RoundedRect());
		getAttributeMap().put(AttributeMap.SIZE, new Dimension(30, 50));
		getAttributeMap().put(AttributeMap.BORDERWIDTH, 2);
		
				
//			getAttributeMap().put(AttributeMap.LABEL, String.valueOf(getTransition().getLabel()));
//		} else if (type.equals(StepTypes.MINVI)){
			getAttributeMap().put(AttributeMap.LABEL, String.valueOf(getStepIndex()));
			getAttributeMap().put(AttributeMap.FILLCOLOR, PGraphColorStyle.NODE_INVISIBLE_MOVE_COLOR);
//		}
		
	}


	public String toStringType() {
		return "Invisible model move";
	}

}
