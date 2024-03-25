package org.processmining.partialorder.plugins.vis.palignment;

import java.awt.event.ItemListener;

import javax.swing.JPanel;

import org.processmining.partialorder.models.graph.POEdge;
import org.processmining.partialorder.models.graph.node.POEventNode;
import org.processmining.partialorder.models.graph.node.PONodeMove;

public abstract class PGraphInfoPanel extends JPanel implements ItemListener {
	
	

	public abstract void updateInfo(POEdge edge);

	public abstract void updateInfo(POEventNode node);
	
	public abstract void updateInfo(PONodeMove node);

	
	
	

}
