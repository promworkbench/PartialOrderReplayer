package org.processmining.partialorder.models.graph.edge;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.AttributeMap.ArrowType;
import org.processmining.partialorder.models.graph.POEdge;
import org.processmining.partialorder.models.graph.PONode;
import org.processmining.partialorder.plugins.vis.PGraphColorStyle;

public class POEdgeTransitive extends POEdge {

	public POEdgeTransitive(PONode source, PONode target){
		super(source, target);
		setDirect(false);
	}

	public void initAttributeMap() {
		getAttributeMap().put(AttributeMap.EDGEEND, ArrowType.ARROWTYPE_TECHNICAL);
		getAttributeMap().put(AttributeMap.DASHPATTERN, new float[] {2, 2*2});
		getAttributeMap().put(AttributeMap.EDGEENDFILLED, true);
		getAttributeMap().put(AttributeMap.EDGECOLOR, PGraphColorStyle.COLOR_TRANSITIVE_RELATION);
		getAttributeMap().put(AttributeMap.LABELCOLOR, PGraphColorStyle.LABEL_COLOR);		
	}
}
