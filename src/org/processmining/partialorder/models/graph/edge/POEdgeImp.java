package org.processmining.partialorder.models.graph.edge;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.AttributeMap.ArrowType;
import org.processmining.partialorder.models.graph.POEdge;
import org.processmining.partialorder.models.graph.PONode;
import org.processmining.partialorder.plugins.vis.PGraphColorStyle;

public class POEdgeImp extends POEdge {

//	private float width = (float) 1.5;
	public POEdgeImp(PONode source, PONode target) {
		super(source, target);
	}

	public POEdgeImp(PONode source, PONode target, float width) {
		this(source, target);
//		this.width = (float) (width + 1.0);
		getAttributeMap().put(AttributeMap.LINEWIDTH,  width);
		
	}

	public void initAttributeMap() {
		getAttributeMap().put(AttributeMap.EDGEEND, ArrowType.ARROWTYPE_TECHNICAL);
		getAttributeMap().put(AttributeMap.EDGEENDFILLED, true);
		getAttributeMap().put(AttributeMap.EDGECOLOR, PGraphColorStyle.COLOR_LINEAR_EDGE);
		getAttributeMap().put(AttributeMap.LABEL, "");
//		System.out.println(width);
	}
	
	

}
