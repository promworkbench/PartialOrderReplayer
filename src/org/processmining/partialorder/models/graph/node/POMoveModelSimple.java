package org.processmining.partialorder.models.graph.node;

import java.awt.Dimension;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.shapes.RoundedRect;
import org.processmining.partialorder.models.graph.PONode;
import org.processmining.partialorder.models.graph.PartialOrderGraph;
import org.processmining.partialorder.plugins.vis.PGraphColorStyle;

public class POMoveModelSimple extends PONode {

	public POMoveModelSimple(PartialOrderGraph graph, String label) {
		super(graph);
		this.setLabel(label);
		initAttributeMap();
	}

	protected void initAttributeMap() {
		getAttributeMap().put(AttributeMap.SHAPE, new RoundedRect());
		getAttributeMap().put(AttributeMap.SIZE, new Dimension(50, 30));
		getAttributeMap().put(AttributeMap.BORDERWIDTH, 2);
		setColor(PGraphColorStyle.COLOR_MODEL_RELATION);
	}


	public String toStringType() {
		return "Model move";
	}

	public Object[][] getInfo() {
		return new Object[0][0];
	}

}
