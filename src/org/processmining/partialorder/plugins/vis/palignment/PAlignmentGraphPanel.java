package org.processmining.partialorder.plugins.vis.palignment;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JScrollPane;

import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.util.ui.widgets.WidgetColors;
import org.processmining.models.jgraph.elements.ProMGraphCell;
import org.processmining.models.jgraph.elements.ProMGraphEdge;
import org.processmining.partialorder.models.graph.POEdge;
import org.processmining.partialorder.models.graph.PartialOrderGraph;
import org.processmining.partialorder.models.graph.node.POEventNode;
import org.processmining.partialorder.models.graph.node.PONodeMove;
import org.processmining.partialorder.plugins.vis.GraphExportablePanel;
import org.processmining.partialorder.util.VisUtil;

public class PAlignmentGraphPanel extends GraphExportablePanel implements GraphSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5343460601251508977L;

	private PGraphInfoPanel infopanel;

	public PAlignmentGraphPanel(PluginContext context, PartialOrderGraph graph, PGraphInfoPanel poTraceInfoPanel) {
		super(10, 5, 0);
		initLayout();
		this.infopanel = poTraceInfoPanel;
		comp = VisUtil.getProMSimpleJGraph(graph, VisUtil.LightBackgroundColor);
		if (comp != null) {
			comp.addGraphSelectionListener(this);

			JScrollPane scalable = VisUtil.getProMStyleScrollPane(comp);
			comp.addMouseListener(this);
			this.add(scalable, BorderLayout.CENTER);

		}
	}

	public PAlignmentGraphPanel(PluginContext context, PartialOrderGraph graph) {
		super(10, 5, 0);
		initLayout();
//		this.infopanel = poTraceInfoPanel;
		comp = VisUtil.getProMSimpleJGraph(graph, VisUtil.LightBackgroundColor);
		if (comp != null) {
			comp.addGraphSelectionListener(this);

			JScrollPane scalable = VisUtil.getProMStyleScrollPane(comp);
			comp.addMouseListener(this);
			this.add(scalable, BorderLayout.CENTER);

		}
	}

	private void initLayout() {
		setBackground(WidgetColors.COLOR_ENCLOSURE_BG);
		setLayout(new BorderLayout());
		setMinimumSize(new Dimension(500, 500));
		setMaximumSize(new Dimension(1000, 1000));
		setPreferredSize(new Dimension(1000, 500));
		add(Box.createHorizontalStrut(5), BorderLayout.WEST);
		add(Box.createHorizontalStrut(5), BorderLayout.EAST);
	}

	public void valueChanged(GraphSelectionEvent evt) {
		if (infopanel != null) {
			for (Object cell : evt.getCells()) {
				if (!evt.isAddedCell(cell)) {
					continue;
				}
				if (cell instanceof ProMGraphCell) {
					if (((ProMGraphCell) cell).getNode() instanceof POEventNode) {
						// Partially Ordered Trace Information visualization
						infopanel.updateInfo((POEventNode) ((ProMGraphCell) cell).getNode());
					} else if (((ProMGraphCell) cell).getNode() instanceof PONodeMove) {
						// Partially Ordered Alignment Information visualization
						infopanel.updateInfo((PONodeMove) ((ProMGraphCell) cell).getNode());
					}

				} else if (cell instanceof ProMGraphEdge && ((ProMGraphEdge) cell).getEdge() instanceof POEdge) {

					infopanel.updateInfo((POEdge) ((ProMGraphEdge) cell).getEdge());
				}
			}
		}
	}

}
