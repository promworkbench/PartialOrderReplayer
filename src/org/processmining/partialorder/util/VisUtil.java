package org.processmining.partialorder.util;

import java.awt.Color;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeBoolean;
import org.deckfour.xes.model.XAttributeContainer;
import org.deckfour.xes.model.XAttributeContinuous;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XAttributeID;
import org.deckfour.xes.model.XAttributeList;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.processmining.framework.util.ui.scalableview.ScalableViewPanel;
import org.processmining.framework.util.ui.scalableview.interaction.ZoomInteractionPanel;
import org.processmining.framework.util.ui.widgets.WidgetColors;
import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.ViewSpecificAttributeMap;
import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.graphbased.directed.DirectedGraphEdge;
import org.processmining.models.graphbased.directed.DirectedGraphNode;
import org.processmining.models.jgraph.ProMGraphModel;
import org.processmining.models.jgraph.ProMJGraph;
import org.processmining.models.jgraph.visualization.ProMJGraphPanel;
import org.processmining.partialorder.models.dependency.PDependencyDataAware.EnumDataDependency;
import org.processmining.plugins.petrinet.replayresult.StepTypes;

import com.fluxicon.slickerbox.ui.SlickerScrollBarUI;
import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayout;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;

public class VisUtil {

	public final static Color DarkBackgroundColor = new Color(30, 30, 30);
	public final static Color LightBackgroundColor = Color.WHITE;

	public static JScrollPane getProMStyleScrollPane(JComponent comp) {
		JScrollPane vscrollPane = null;
		if (comp instanceof ProMJGraph) {

			//TODO this ProMJGraphPanel is really urgely, find another way to fit and scale
			ProMJGraphPanel panel = new ProMJGraphPanel((ProMJGraph) comp);
			//		panel.setSize(300, 300);
			panel.addViewInteractionPanel(new ZoomInteractionPanel(panel, ScalableViewPanel.MAX_ZOOM),
					SwingConstants.WEST);
			panel.scaleToFit();
			vscrollPane = new JScrollPane(panel);
		} else {
			vscrollPane = new JScrollPane(comp);
		}
		vscrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		vscrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		vscrollPane.setOpaque(true);
		vscrollPane.setBackground(WidgetColors.COLOR_ENCLOSURE_BG);
		vscrollPane.getViewport().setOpaque(true);
		vscrollPane.getViewport().setBackground(WidgetColors.COLOR_ENCLOSURE_BG);
		vscrollPane.setBorder(BorderFactory.createEmptyBorder());

		JScrollBar vBar = vscrollPane.getVerticalScrollBar();
		vBar.setUI(new SlickerScrollBarUI(vBar, new Color(0, 0, 0, 0), new Color(160, 160, 160),
				WidgetColors.COLOR_NON_FOCUS, 4, 12));
		vBar.setOpaque(true);
		vBar.setBackground(WidgetColors.COLOR_ENCLOSURE_BG);

		JScrollBar hBar = vscrollPane.getHorizontalScrollBar();
		hBar.setUI(new SlickerScrollBarUI(hBar, new Color(0, 0, 0, 0), new Color(160, 160, 160),
				WidgetColors.COLOR_NON_FOCUS, 4, 12));
		hBar.setOpaque(true);
		hBar.setBackground(WidgetColors.COLOR_ENCLOSURE_BG);
		return vscrollPane;
	}

	public static JScrollPane getProMStyleVerticalScrollPane(JComponent comp) {
		JScrollPane vscrollPane = null;
		if (comp instanceof ProMJGraph) {

			//TODO this ProMJGraphPanel is really urgely, find another way to fit and scale
			ProMJGraphPanel panel = new ProMJGraphPanel((ProMJGraph) comp);
			//		panel.setSize(300, 300);
			panel.addViewInteractionPanel(new ZoomInteractionPanel(panel, ScalableViewPanel.MAX_ZOOM),
					SwingConstants.WEST);
			panel.scaleToFit();
			vscrollPane = new JScrollPane(panel);
		} else {
			vscrollPane = new JScrollPane(comp);
		}
		vscrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		vscrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		vscrollPane.setOpaque(true);
		vscrollPane.setBackground(WidgetColors.COLOR_ENCLOSURE_BG);
		vscrollPane.getViewport().setOpaque(true);
		vscrollPane.getViewport().setBackground(WidgetColors.COLOR_ENCLOSURE_BG);
		vscrollPane.setBorder(BorderFactory.createEmptyBorder());

		JScrollBar vBar = vscrollPane.getVerticalScrollBar();
		vBar.setUI(new SlickerScrollBarUI(vBar, new Color(0, 0, 0, 0), new Color(160, 160, 160),
				WidgetColors.COLOR_NON_FOCUS, 4, 12));
		vBar.setOpaque(true);
		vBar.setBackground(WidgetColors.COLOR_ENCLOSURE_BG);
		return vscrollPane;
	}

	public static ProMJGraph getProMSimpleJGraph(
			DirectedGraph<? extends DirectedGraphNode, ? extends DirectedGraphEdge<? extends DirectedGraphNode, ? extends DirectedGraphNode>> net,
			Color backgroundcolor, GraphLayoutConnection layoutConnection) {

		ViewSpecificAttributeMap map = new ViewSpecificAttributeMap();

		ProMGraphModel model = new ProMGraphModel(net);
		ProMJGraph jGraph = null;

		if (layoutConnection == null) {
			layoutConnection = new GraphLayoutConnection(net);
			jGraph = new ProMJGraph(model, map, layoutConnection);

			JGraphHierarchicalLayout layout = new JGraphHierarchicalLayout();
			layout.setDeterministic(false);
			layout.setCompactLayout(false);
			layout.setFineTuning(true);
			layout.setParallelEdgeSpacing(15);
			layout.setFixRoots(false);

			layout.setOrientation(map.get(net, AttributeMap.PREF_ORIENTATION, SwingConstants.SOUTH));

			JGraphFacade facade = new JGraphFacade(jGraph);

			facade.setOrdered(false);
			facade.setEdgePromotion(true);
			facade.setIgnoresCellsInGroups(false);
			facade.setIgnoresHiddenCells(false);
			facade.setIgnoresUnconnectedCells(false);
			facade.setDirected(true);
			facade.resetControlPoints();

			facade.run(layout, true);

			java.util.Map<?, ?> nested = facade.createNestedMap(true, true);
			jGraph.getGraphLayoutCache().edit(nested);

			jGraph.setUpdateLayout(layout);

		} else if (!layoutConnection.isLayedOut()) {
			jGraph = new ProMJGraph(model, map, layoutConnection);

			layoutConnection.setLayedOut(true);
			layoutConnection.updated();
		}
		jGraph.setBackground(backgroundcolor);

		return jGraph;
	}

	public static ProMJGraph getProMSimpleJGraph(
			DirectedGraph<? extends DirectedGraphNode, ? extends DirectedGraphEdge<? extends DirectedGraphNode, ? extends DirectedGraphNode>> net,
			Color backgroundcolor) {
		return getProMSimpleJGraph(net, backgroundcolor, null);
	}

	public static JGraphLayout getLayout(int orientation) {
		JGraphHierarchicalLayout layout = new JGraphHierarchicalLayout();
		layout.setDeterministic(false);
		layout.setCompactLayout(false);
		layout.setFineTuning(true);
		layout.setParallelEdgeSpacing(15);
		layout.setFixRoots(false);

		layout.setOrientation(orientation);

		return layout;
	}

	public static Color getStepTypeColor(StepTypes type) {
		if (type.equals(StepTypes.L))
			return new Color(255, 215, 0);
		if (type.equals(StepTypes.LMGOOD))
			return new Color(144, 238, 144);
		if (type.equals(StepTypes.MINVI))
			return new Color(105, 105, 105);
		if (type.equals(StepTypes.MREAL))
			return new Color(255, 20, 147);

		return null;
	}

	public static Object getStringOfSet(Set<String> value) {
		String res = "";
		for (String v : value) {
			res += v + "; ";
		}
		return res;
	}

	public static String getStringOfAttribute(XAttribute attribute) {
		if (attribute instanceof XAttributeList) {
			return "List";
		} else if (attribute instanceof XAttributeContainer) {
			return "Container";
		} else if (attribute instanceof XAttributeLiteral) {
			return ((XAttributeLiteral) attribute).getValue();
		} else if (attribute instanceof XAttributeBoolean) {
			return String.valueOf(((XAttributeBoolean) attribute).getValue());
		} else if (attribute instanceof XAttributeContinuous) {
			return String.valueOf(((XAttributeContinuous) attribute).getValue());
		} else if (attribute instanceof XAttributeDiscrete) {
			return String.valueOf(((XAttributeDiscrete) attribute).getValue());
		} else if (attribute instanceof XAttributeTimestamp) {
			return String.valueOf(((XAttributeTimestamp) attribute).getValue());
		} else if (attribute instanceof XAttributeID) {
			return String.valueOf(((XAttributeID) attribute).getValue());
		} else {
			//throw new AssertionError("Unexpected attribute type!");
		}
		return "Unknown attribute type";
	}

	public static String getStringOfSourceAttribute(EnumDataDependency d, XAttribute source) {
		String pre = null;
		if (d.equals(EnumDataDependency.II_DiffValue) || d.equals(EnumDataDependency.II_SameValue)
				|| d.equals(EnumDataDependency.IO_DiffValue) || d.equals(EnumDataDependency.IO_SameValue)) {
			pre = "(input) ";

		} else {
			pre = "(output) ";
		}
		return pre + getStringOfAttribute(source);
	}

	public static String getStringOfTargetAttribute(EnumDataDependency d, XAttribute target) {
		String pre = null;
		if (d.equals(EnumDataDependency.II_DiffValue) || d.equals(EnumDataDependency.II_SameValue)
				|| d.equals(EnumDataDependency.OI_DiffValue) || d.equals(EnumDataDependency.OI_SameValue)) {
			pre = "(input) ";

		} else {
			pre = "(output) ";
		}
		return pre + getStringOfAttribute(target);
	}

}
