package org.processmining.partialorder.ptrace.plugins.vis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JScrollPane;

import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.util.ui.widgets.WidgetColors;
import org.processmining.models.jgraph.elements.ProMGraphCell;
import org.processmining.models.jgraph.elements.ProMGraphEdge;
import org.processmining.partialorder.models.graph.POEdge;
import org.processmining.partialorder.models.graph.PONode;
import org.processmining.partialorder.models.graph.PartialOrderGraph;
import org.processmining.partialorder.models.graph.node.POEventNode;
import org.processmining.partialorder.plugins.vis.GraphExportablePanel;
import org.processmining.partialorder.plugins.vis.palignment.PGraphInfoPanel;
import org.processmining.partialorder.util.VisUtil;

public class PTraceGraphPanel extends GraphExportablePanel implements GraphSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5343460601251508977L;
	private PartialOrderGraph graph;
	//private PartiallyOrderedXTrace potrace;
	//ProMJGraph comp;
	private PGraphInfoPanel infopanel;

	public PTraceGraphPanel(PluginContext context, PartialOrderGraph graph, PGraphInfoPanel poTraceInfoPanel) {
		super(10, 5, 0);
		initLayout();

//		SlickerFactory factory = SlickerFactory.instance();
//		SlickerDecorator decorator = SlickerDecorator.instance();

		this.infopanel = poTraceInfoPanel;

		this.graph = graph;
		comp = VisUtil.getProMSimpleJGraph(graph, VisUtil.LightBackgroundColor);
		if(comp != null){
			comp.addGraphSelectionListener(this);
		
			JScrollPane scalable = VisUtil.getProMStyleScrollPane(comp);
			comp.addMouseListener(this);
			this.add(scalable, BorderLayout.CENTER);
		
		}

		this.add(VisUtil.getProMStyleScrollPane(comp), BorderLayout.CENTER);
	}

	private void initLayout() {
		setBackground(WidgetColors.COLOR_ENCLOSURE_BG);
		setLayout(new BorderLayout());
//		setMinimumSize(new Dimension(200, 100));
//		setMaximumSize(new Dimension(1000, 1000));
//		setPreferredSize(new Dimension(1000, 600));
		add(Box.createHorizontalStrut(5), BorderLayout.WEST);
		add(Box.createHorizontalStrut(5), BorderLayout.EAST);
	}

	public void valueChanged(GraphSelectionEvent evt) {
		for (Object cell : evt.getCells()) {
			if (!evt.isAddedCell(cell)) {
				continue;
			}
			if (cell instanceof ProMGraphCell && ((ProMGraphCell) cell).getNode() instanceof PONode) {

				infopanel.updateInfo((POEventNode) ((ProMGraphCell) cell).getNode());

			} else if (cell instanceof ProMGraphEdge && ((ProMGraphEdge) cell).getEdge() instanceof POEdge) {

				infopanel.updateInfo((POEdge) ((ProMGraphEdge) cell).getEdge());
			}
		}
	}

	public void updateEventColor(Map<String, Color> mapEventToColor) {
		comp.getModel().beginUpdate();
		// 
		for (PONode n : graph.getNodes()) {

			Color c = mapEventToColor.get(n.getLabel());
			if (c != null) {
				n.setColor(c);
			}
		}

		comp.getModel().endUpdate();
		comp.refresh();
		comp.revalidate();
		comp.repaint();

	}

}
