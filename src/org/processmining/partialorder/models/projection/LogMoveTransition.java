package org.processmining.partialorder.models.projection;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.SwingConstants;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.partialorder.models.graph.shape.ShapeLogMove;

public class LogMoveTransition extends Transition {

//	public LogMoveTransition(
//			AbstractDirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> net,
//			ExpandableSubNet parent, String label) {
//		super(net, parent, label);
//		initLayout();
//
//	}

	public LogMoveTransition(String label,
			AbstractDirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> net) {
		super(label, net);
		initLayout();
	}

	private void initLayout() {
		getAttributeMap().put(AttributeMap.SHAPE, new ShapeLogMove());
		//getAttributeMap().put(AttributeMap.SHAPE, new RoundedRect());
		getAttributeMap().put(AttributeMap.SIZE, new Dimension(50, 50));
		getAttributeMap().put(AttributeMap.BORDERWIDTH, 2);

		getAttributeMap().put(AttributeMap.LABEL, this.getLabel());
		getAttributeMap().put(AttributeMap.SHOWLABEL, true);
		getAttributeMap().put(AttributeMap.LABELHORIZONTALALIGNMENT, SwingConstants.CENTER);
		getAttributeMap().put(AttributeMap.LABELVERTICALALIGNMENT, SwingConstants.CENTER);
		getAttributeMap().put(AttributeMap.RESIZABLE, false);
		getAttributeMap().put(AttributeMap.AUTOSIZE, false);
	}

//	public LogMoveTransition(String label, POPetrinetGraph poPetrinetGraph) {
//		this(poPetrinetGraph, null, label);
//	}

	public void setGraphVisVisible(boolean visible) {
		if (visible) {
			getAttributeMap().put(AttributeMap.SHOWLABEL, true);
			getAttributeMap().put(AttributeMap.FILLCOLOR, Color.CYAN);
			getAttributeMap().put(AttributeMap.STROKECOLOR, Color.DARK_GRAY);
		} else {
			getAttributeMap().put(AttributeMap.SHOWLABEL, false);
			getAttributeMap().put(AttributeMap.FILLCOLOR, new Color(0, 0, 0, 0));
			getAttributeMap().put(AttributeMap.STROKECOLOR, new Color(0, 0, 0, 0));
		}
	}

}
