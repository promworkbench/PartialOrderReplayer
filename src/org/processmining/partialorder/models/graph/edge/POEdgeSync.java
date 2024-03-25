package org.processmining.partialorder.models.graph.edge;

import java.awt.Color;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.AttributeMap.ArrowType;
import org.processmining.partialorder.models.dependency.PDependency;
import org.processmining.partialorder.models.graph.POEdge;
import org.processmining.partialorder.models.graph.PONode;
import org.processmining.partialorder.plugins.vis.PGraphColorStyle;

public class POEdgeSync extends POEdge {

	public POEdgeSync(PONode source, PONode target, PDependency dep) {
		super(source, target, dep);
	}

	public void initAttributeMap() {
		getAttributeMap().put(AttributeMap.EDGEEND, ArrowType.ARROWTYPE_TECHNICAL);
		getAttributeMap().put(AttributeMap.EDGEENDFILLED, true);
		getAttributeMap().put(AttributeMap.EDGECOLOR, PGraphColorStyle.COLOR_SYNC_RELATION);
		getAttributeMap().put(AttributeMap.LINEWIDTH, (float) 1.5);
		
		
		//getAttributeMap().put(AttributeMap.LABELALONGEDGE, true);
		
		getAttributeMap().put(AttributeMap.LABEL, "LM");
		getAttributeMap().put(AttributeMap.SHOWLABEL, true);
		getAttributeMap().put(AttributeMap.LABELCOLOR, new Color(169,169,169));	
	}
	

}
