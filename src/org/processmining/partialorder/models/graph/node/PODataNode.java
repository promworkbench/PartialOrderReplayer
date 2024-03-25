package org.processmining.partialorder.models.graph.node;

import java.awt.Dimension;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.shapes.Ellipse;
import org.processmining.partialorder.models.dependency.PDependency;
import org.processmining.partialorder.models.dependency.PDependencyDataAware;
import org.processmining.partialorder.models.graph.PONode;
import org.processmining.partialorder.models.graph.PartialOrderGraph;
import org.processmining.partialorder.plugins.vis.PGraphColorStyle;
import org.processmining.partialorder.plugins.vis.palignment.PAlignmentMainVisPanel;

/**
 * The class extends the {@link PONode} and represents a data dependency between
 * two events in the graph. Only added to the graph if the option
 * "DataDepAndVisible_ModelDep" is selected in {@link PAlignmentMainVisPanel}
 * 
 * @author xlu
 */
public class PODataNode extends PONode {

	private PDependency relation;

	public PODataNode(int trace, PartialOrderGraph graph, PONode source, PONode target, PDependency relation) {
		super(graph);
		this.relation = relation;
		initAttributeMap();
	}

	protected void initAttributeMap() {
		getAttributeMap().put(AttributeMap.SHAPE, new Ellipse());
		getAttributeMap().put(AttributeMap.SIZE, new Dimension(50, 50));
		getAttributeMap().put(AttributeMap.BORDERWIDTH, 2);


		if (relation instanceof PDependencyDataAware) {
			String label = "";
			for (Object[] v : ((PDependencyDataAware) relation).getDataDependencyValues()) {
				label += (String) v[0] + ",";
			}

			getAttributeMap().put(AttributeMap.LABEL, label.substring(0, label.length() - 1));
		}
		getAttributeMap().put(AttributeMap.SHOWLABEL, true);
		setColor(PGraphColorStyle.DATA_NODE_COLOR);
	}

	public PDependency getRelation() {
		return relation;
	}

	public void setRelation(PDependency relation) {
		this.relation = relation;
	}

	public String toStringType() {
		return "Data node";
	}

	public Object[][] getInfo() {
		// TODO Auto-generated method stub
		return new Object[0][];
	}

}
