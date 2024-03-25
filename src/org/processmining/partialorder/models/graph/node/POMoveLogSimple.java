package org.processmining.partialorder.models.graph.node;

import java.awt.Dimension;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.shapes.RoundedRect;
import org.processmining.partialorder.models.graph.PONode;
import org.processmining.partialorder.models.graph.PartialOrderGraph;
import org.processmining.partialorder.plugins.vis.PGraphColorStyle;

public class POMoveLogSimple extends PONode {

	public POMoveLogSimple(PartialOrderGraph graph, String label) {
		super(graph);
		this.setLabel(label);
		initAttributeMap();
	}

	protected void initAttributeMap() {
//		getAttributeMap().put(AttributeMap.SHAPE, new ShapeLogMove());
		getAttributeMap().put(AttributeMap.SHAPE, new RoundedRect());
		getAttributeMap().put(AttributeMap.SIZE, new Dimension(60, 30));
		getAttributeMap().put(AttributeMap.BORDERWIDTH, 2);

		getAttributeMap().put(AttributeMap.SHOWLABEL, true);
		setColor(PGraphColorStyle.COLOR_LOG_RELATION);
	}

	public String toStringType() {
		return "Log move";
	}

	public Object[][] getInfo() {
		return new Object[0][0];
	}

	

}
