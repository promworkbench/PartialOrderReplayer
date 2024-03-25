package org.processmining.partialorder.models.projection;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.SwingConstants;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.ExpandableSubNet;
import org.processmining.models.shapes.Ellipse;

public class DataElementNode extends PetrinetNode {

	
	
	public DataElementNode(
			AbstractDirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> net,
			ExpandableSubNet parent, String label) {
		super(net, parent, label);
		initLayout();
	}

	public DataElementNode(String label, POProjectedPetrinetGraph poPetrinetGraph) {
		this(poPetrinetGraph, null, label);
	}

	private void initLayout(){
		getAttributeMap().put(AttributeMap.SHAPE, new Ellipse());
		//getAttributeMap().put(AttributeMap.SHAPE, new RoundedRect());
		getAttributeMap().put(AttributeMap.SIZE, new Dimension(40, 40));
		getAttributeMap().put(AttributeMap.BORDERWIDTH, 2);
		
//		getAttributeMap().put(AttributeMap.LABEL, LogUtil.getName(event));
//		getAttributeMap().put(AttributeMap.SHOWLABEL, true);
		
		getAttributeMap().put(AttributeMap.SHOWLABEL, true);
		//getAttributeMap().put(AttributeMap.DASHPATTERN, new float[] {2, 2*2});
		getAttributeMap().put(AttributeMap.LABELHORIZONTALALIGNMENT, SwingConstants.CENTER);
		getAttributeMap().put(AttributeMap.LABELVERTICALALIGNMENT, SwingConstants.CENTER);
		getAttributeMap().put(AttributeMap.RESIZABLE, false);
		getAttributeMap().put(AttributeMap.AUTOSIZE, false);
		
		// Edge to data node pattern.
//		getAttributeMap().put(AttributeMap.EDGEEND, ArrowType.ARROWTYPE_SIMPLE);
//		getAttributeMap().put(AttributeMap.EDGECOLOR, new Color(169,169,169));
//		getAttributeMap().put(AttributeMap.DASHPATTERN, new float[] {2, 2*2});
	}

	public void setGraphVisVisible(boolean visible) {
		if (visible) {
			getAttributeMap().put(AttributeMap.SHOWLABEL, true);
			getAttributeMap().put(AttributeMap.FILLCOLOR, Color.MAGENTA);
			getAttributeMap().put(AttributeMap.STROKECOLOR, Color.MAGENTA);
		} else {
			getAttributeMap().put(AttributeMap.SHOWLABEL, false);
			getAttributeMap().put(AttributeMap.FILLCOLOR, new Color(0, 0, 0, 0));
			getAttributeMap().put(AttributeMap.STROKECOLOR, new Color(0, 0, 0, 0));
		}		
	}
}
