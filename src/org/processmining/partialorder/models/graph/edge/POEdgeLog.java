package org.processmining.partialorder.models.graph.edge;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.AttributeMap.ArrowType;
import org.processmining.partialorder.models.dependency.PDependency;
import org.processmining.partialorder.models.graph.POEdge;
import org.processmining.partialorder.models.graph.PONode;
import org.processmining.partialorder.plugins.vis.PGraphColorStyle;

public class POEdgeLog extends POEdge {

	public POEdgeLog(PONode source, PONode target, PDependency dep) {
		super(source, target, dep);
		setDirect(dep != null ? dep.isDirect() : true);
	}

	public void initAttributeMap() {
		getAttributeMap().put(AttributeMap.EDGEEND, ArrowType.ARROWTYPE_TECHNICAL);
		getAttributeMap().put(AttributeMap.EDGEENDFILLED, true);
		getAttributeMap().put(AttributeMap.EDGECOLOR, PGraphColorStyle.COLOR_LOG_RELATION);
		getAttributeMap().put(AttributeMap.LABELCOLOR, PGraphColorStyle.LABEL_COLOR);
		
		if (isDirect()) {
			getAttributeMap().put(AttributeMap.SHOWLABEL, true);
			getAttributeMap().put(AttributeMap.LABEL, "L");
			getAttributeMap().put(AttributeMap.LINEWIDTH, (float) 1.5);
			

		} else {
			getAttributeMap().put(AttributeMap.SHOWLABEL, false);
			getAttributeMap().put(AttributeMap.LINEWIDTH, (float) 1.0);
		}

	}

}
