package org.processmining.partialorder.models.graph.node;

import java.awt.Color;
import java.awt.Dimension;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.shapes.Ellipse;
import org.processmining.partialorder.models.graph.PONode;
import org.processmining.partialorder.models.graph.PartialOrderGraph;

public class POPlaceSimple extends PONode {

	public POPlaceSimple(PartialOrderGraph graph, String label) {
		super(graph);
		this.setLabel(label);
		initAttributeMap();
	}

	protected void initAttributeMap() {
		getAttributeMap().put(AttributeMap.SHAPE, new Ellipse());
		getAttributeMap().put(AttributeMap.SIZE, new Dimension(20, 20));
		getAttributeMap().put(AttributeMap.BORDERWIDTH, 2);

		getAttributeMap().put(AttributeMap.SHOWLABEL, true);
		setColor(Color.WHITE);
	}


	public String toStringType() {
		return "Place";
	}

	public Object[][] getInfo() {
		return new Object[0][0];
	}


}
