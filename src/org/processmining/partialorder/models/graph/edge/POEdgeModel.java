package org.processmining.partialorder.models.graph.edge;

import java.awt.Color;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.AttributeMap.ArrowType;
import org.processmining.partialorder.models.graph.POEdge;
import org.processmining.partialorder.models.graph.PONode;
import org.processmining.partialorder.plugins.vis.PGraphColorStyle;

public class POEdgeModel extends POEdge {

	public POEdgeModel(PONode source, PONode target) {
		super(source, target);
	}

	public void initAttributeMap() {
		getAttributeMap().put(AttributeMap.EDGEEND, ArrowType.ARROWTYPE_TECHNICAL);
		getAttributeMap().put(AttributeMap.EDGEENDFILLED, true);
		getAttributeMap().put(AttributeMap.EDGECOLOR, PGraphColorStyle.COLOR_MODEL_RELATION);
		getAttributeMap().put(AttributeMap.LINEWIDTH, (float) 1.5);
		
		getAttributeMap().put(AttributeMap.LABEL, "M");
		getAttributeMap().put(AttributeMap.SHOWLABEL, true);
		getAttributeMap().put(AttributeMap.LABELCOLOR, new Color(169,169,169));
		
	}

}
