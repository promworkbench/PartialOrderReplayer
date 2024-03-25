package org.processmining.partialorder.models.graph;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.SwingConstants;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;
import org.processmining.models.shapes.Decorated;

/**
 * An general abstract class of the nodes of {@link PartialOrderGraph}
 * 
 * @author xlu
 * 
 */
public abstract class PONode extends AbstractDirectedGraphNode implements Decorated {

	protected PartialOrderGraph graph;
	protected Color color;
	protected String label; 
	
	public PONode(PartialOrderGraph graph) {
		this.graph = graph;

		getAttributeMap().put(AttributeMap.LABELHORIZONTALALIGNMENT, SwingConstants.CENTER);
		getAttributeMap().put(AttributeMap.LABELVERTICALALIGNMENT, SwingConstants.CENTER);
		getAttributeMap().put(AttributeMap.RESIZABLE, false);
		getAttributeMap().put(AttributeMap.AUTOSIZE, false);
		
		
	}
	
	public void setLabel(String label) {
		this.label = label;
		getAttributeMap().put(AttributeMap.LABEL, label);		
	}
	
	protected abstract void initAttributeMap();
	
	public abstract String toStringType();
	
	public void decorate(Graphics2D g2d, double x, double y, double width, double height) {
		// ignore? XXQ: no documentation?		
	}

	public AbstractDirectedGraph<?, ?> getGraph() {
		return graph;
	}

	/**
	 * @return trace index
	 */
	public int getTrace() {
		return graph.getTrace();
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		if (color != null) {
			this.color = color;
			getAttributeMap().put(AttributeMap.FILLCOLOR, this.color);
		}
	}
	
	public void setNodeLineColor(Color color){
		getAttributeMap().put(AttributeMap.STROKECOLOR, color);
	}
	
	public void setNodeLabelColor(Color color){
		getAttributeMap().put(AttributeMap.LABELCOLOR, color);
	}
	
	public abstract Object[][] getInfo();

}
