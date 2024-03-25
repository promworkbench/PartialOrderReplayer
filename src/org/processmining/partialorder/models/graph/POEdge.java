package org.processmining.partialorder.models.graph;

import java.awt.Color;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.AttributeMap.ArrowType;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;
import org.processmining.partialorder.models.dependency.PDependency;
import org.processmining.partialorder.plugins.vis.PGraphColorStyle;

/**
 * Edges in the {@link PartialOrderGraph} class.
 * 
 * @author xlu
 * 
 */
public abstract class POEdge extends AbstractDirectedGraphEdge<PONode, PONode> {

	protected PONode source;
	protected PONode target;
	protected Color color;

	protected PDependency relation;
	private boolean isDirect;

	public POEdge(PONode source, PONode target, PDependency relation) {
		super(source, target);
		this.source = source;
		this.target = target;
		this.relation = relation;
		initAttributeMap();
	}

	public POEdge(PONode source, PONode target) {
		super(source, target);
		this.source = source;
		this.target = target;
		initAttributeMap();
	}

	public abstract void initAttributeMap();

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		if (color != null) {
			this.color = color;
			getAttributeMap().put(AttributeMap.EDGECOLOR, this.color);
		}
	}
	
	public PONode getTarget() {
		return target;
	}

	public PONode getSource() {
		return source;
	}

	public PDependency getRelation() {
		return relation;
	}

	public void setRelation(PDependency r) {
		this.relation = r;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof POEdge) {
			POEdge otherEdge = (POEdge) other;
			return otherEdge.getSource().equals(this.source) && otherEdge.getTarget().equals(this.target);

		}
		return false;
	}
	
	public Object[][] getInfo(){
		return new Object[0][2];		
	}

	public void setWidth(float width){
		getAttributeMap().put(AttributeMap.LINEWIDTH,  width);
	}

	public boolean isDirect() {
		return isDirect;
	}

	public void setDirect(boolean isDirect) {
		this.isDirect = isDirect;
		if(!isDirect){
			getAttributeMap().put(AttributeMap.EDGEEND, ArrowType.ARROWTYPE_TECHNICAL);
			getAttributeMap().put(AttributeMap.DASHPATTERN, new float[] {2, 2*2});
			getAttributeMap().put(AttributeMap.EDGEENDFILLED, true);
			getAttributeMap().put(AttributeMap.EDGECOLOR, PGraphColorStyle.COLOR_TRANSITIVE_RELATION);
			getAttributeMap().put(AttributeMap.LABELCOLOR, PGraphColorStyle.LABEL_COLOR);	
		}
	}

}
