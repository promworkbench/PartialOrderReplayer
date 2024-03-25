package org.processmining.partialorder.models.graph.node;

import java.awt.Dimension;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.shapes.RoundedRect;
import org.processmining.partialorder.models.graph.PartialOrderGraph;
import org.processmining.partialorder.models.palignment.Move;
import org.processmining.partialorder.plugins.vis.PGraphColorStyle;

/**
 * A POModelMoveNode in the partially ordered graph represents a move in the
 * model A POModelMoveNode node is assigned the specific shape
 * {@link RoundedRect}
 * 
 * @author xlu
 */
public class POModelMoveVisibleNode extends POModelMoveNode {

	public POModelMoveVisibleNode(PartialOrderGraph graph, Move move) {
		super(graph, move);
	}

	protected void initAttributeMap() {
		getAttributeMap().put(AttributeMap.SHAPE, new RoundedRect());
		getAttributeMap().put(AttributeMap.SIZE, new Dimension(30, 50));
		getAttributeMap().put(AttributeMap.BORDERWIDTH, 2);
		getAttributeMap().put(AttributeMap.LABEL, String.valueOf(getTransition().getLabel()));
		setColor(PGraphColorStyle.COLOR_MODEL_RELATION);
	}

	public String toStringType() {
		return "Visible model move";
	}

}
