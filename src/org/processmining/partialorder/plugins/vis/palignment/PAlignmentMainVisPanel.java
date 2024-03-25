package org.processmining.partialorder.plugins.vis.palignment;

import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.util.ui.widgets.ProMSplitPane;
import org.processmining.models.jgraph.ProMJGraphVisualizer;
import org.processmining.partialorder.models.graph.PartialOrderGraph;
import org.processmining.partialorder.models.palignment.PAlignment;
import org.processmining.partialorder.models.replay.POAlignmentDataProvider;
import org.processmining.partialorder.models.replay.POSyncReplayResult;
import org.processmining.partialorder.plugins.replay.POAlignmentBuilder;
import org.processmining.partialorder.plugins.vis.PartialOrderGraphFactory;
import org.processmining.partialorder.plugins.vis.PartialVisualType;
import org.processmining.partialorder.ptrace.model.PTrace;
import org.processmining.partialorder.util.VisUtil;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

import com.fluxicon.slickerbox.components.RoundedPanel;
import com.fluxicon.slickerbox.factory.SlickerDecorator;

public class PAlignmentMainVisPanel extends JComponent {
	private static final long serialVersionUID = 8702589427013827575L;
	
	public enum POAlignmentVisType {
		S_Alignment, Unfolded_Petrinet, P_Alignment_Standard, P_Alignment_Data
	}

	 
	// Data
	private PluginContext context;
	private POAlignmentDataProvider data;
	
	// Settings
	private POAlignmentVisType currentType = POAlignmentVisType.P_Alignment_Standard;
	private PartialVisualType visType = PartialVisualType.MINIMAL_REDUTION;
	private int maxNum = 10;

	// Store to update
	private JPanel logAlignmentPanel;
	private JScrollPane pAlignmentScrollPane;

	//	private final PAlignmentVisInfoProvider poaInfoProvider;
	//	private int selectedTraceIndex;
	//	private Map<String, PAlignmentGraphPanel> mapTraceToGraph = new HashMap<String, PAlignmentGraphPanel>();
	//	private TableLayoutCoord coord = new TableLayoutCoord();


	private static int MARGIN_LABEL = 10;
	
	
	public PAlignmentMainVisPanel(PluginContext context, final POAlignmentDataProvider data) {
		this.context = context;
		this.data = data;
		//		this.poaInfoProvider = new PAlignmentVisInfoProvider(data);
//		initLayout();
		
		setLayout(new BorderLayout());
		ProMSplitPane splitPane = new ProMSplitPane(ProMSplitPane.HORIZONTAL_SPLIT);
		splitPane.setBackground(Color.DARK_GRAY);
		splitPane.setForeground(Color.DARK_GRAY);
//		splitPane.s
		splitPane.setResizeWeight(1.0d);
		add(splitPane, BorderLayout.CENTER);
		splitPane.setBorder(BorderFactory.createEmptyBorder());
		splitPane.setLeftComponent(getComponentPAlignmentListPanel());
		splitPane.setRightComponent(getComponentVisSettings());

//		getComponentPAlignmentListPanel();
//		getComponentControllerPanel();
	}

	private void initLayout() {
		TableLayout mainLayout = new TableLayout(new double[][] { { TableLayout.FILL, 300 }, { TableLayout.FILL } });
		setLayout(mainLayout);
		setBorder(BorderFactory.createEmptyBorder());
	}

	private JComponent getComponentPAlignmentListPanel() {

		pAlignmentScrollPane = VisUtil.getProMStyleScrollPane(getLogAlignmentPanel());
		pAlignmentScrollPane.setBackground(Color.DARK_GRAY);
		pAlignmentScrollPane.setForeground(Color.DARK_GRAY);
		//add(pAlignmentScrollPane, "0,0");
		return pAlignmentScrollPane;
	}

//	private JTabbedPane getComponentControllerPanel() {
//		JTabbedPane tabbedPane = new JTabbedPane();
//		tabbedPane.addTab("Settings", getComponentVisSettings());
////		add(tabbedPane, "1, 0");
//		return tabbedPane;
//	}

	private Component getComponentVisSettings() {
		JComponent panel = new JPanel();
		panel.setBackground(Color.DARK_GRAY);
		panel.setForeground(Color.WHITE);
		BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(boxLayout);	

		// Add graph form selector
		JLabel label = new JLabel("Select a way to visualize PAlignments: ");
		SlickerDecorator.instance().decorate(label);
		label.setForeground(panel.getForeground());
		Border paddingBorder = BorderFactory.createEmptyBorder(MARGIN_LABEL, MARGIN_LABEL, MARGIN_LABEL, MARGIN_LABEL);
		label.setBorder(paddingBorder);
		label.setAlignmentX(Component.LEFT_ALIGNMENT);

		panel.add(label);
		final JComboBox<POAlignmentVisType> combo = new JComboBox<POAlignmentVisType>(POAlignmentVisType.values());
		SlickerDecorator.instance().decorate(combo);
		combo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentType = (POAlignmentVisType) combo.getSelectedItem();
				updateAlignmentGraphs();

			}
		});
		combo.setSelectedItem(currentType);
		combo.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(combo);

		// Add p-alignment selector
//		final JComboBox<String> comboTrace = new JComboBox<String>(LogUtil.getTraceNames(data.getLog()).toArray(
//				new String[data.getLog().size()]));
//		SlickerDecorator.instance().decorate(comboTrace);
//		comboTrace.addItemListener(new ItemListener() {
//			public void itemStateChanged(ItemEvent e) {
//				if (e.getStateChange() == ItemEvent.SELECTED) {
//					String name = (String) comboTrace.getSelectedItem();
//					setSelectedTraceIndex(LogUtil.getTraceIndexByName(data.getLog(), name));
//					updateAlignmentGraphs();
//				}
//			}
//		});
//		panel.add(comboTrace);

		// Add number of p-alignment selector
		JLabel labelnum = new JLabel("Show this number of PAlignments: ");
		SlickerDecorator.instance().decorate(labelnum);
		labelnum.setForeground(panel.getForeground());
		labelnum.setBorder(paddingBorder);
		labelnum.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(labelnum);
		final JComboBox<Integer> select = new JComboBox<Integer>(new Integer[] { 10, 50, 100, Integer.MAX_VALUE });
		select.setSelectedIndex(0);
		SlickerDecorator.instance().decorate(select);
		
		select.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Integer value = (Integer) select.getSelectedItem();
				maxNum = value;
				updateAlignmentGraphs();
			}
		});
		select.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(select);

		// Add graph type selector
		JLabel labeltype = new JLabel("Visualize Alignments using: ");
		SlickerDecorator.instance().decorate(labeltype);
		labeltype.setForeground(panel.getForeground());
		labeltype.setBorder(paddingBorder);
		labeltype.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(labeltype);
		
		final JComboBox<PartialVisualType> visCombo = new JComboBox<PartialVisualType>(
				PartialVisualType.toPAlignmentVisualTypes());
		visCombo.setSelectedItem(visType);
		SlickerDecorator.instance().decorate(visCombo);
	
		visCombo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				visType = (PartialVisualType) visCombo.getSelectedItem();
				updateAlignmentGraphs();
			}
		});
		visCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(visCombo);
		
		

		return panel;
	}

	protected void updateAlignmentGraphs() {
		logAlignmentPanel.removeAll();
		getLogAlignmentPanel();
		logAlignmentPanel.revalidate();
		logAlignmentPanel.repaint();
		pAlignmentScrollPane.setViewportView(logAlignmentPanel);
	}

	private JComponent getLogAlignmentPanel() {
		if(logAlignmentPanel == null){
			logAlignmentPanel = new JPanel();
			logAlignmentPanel.setBackground(Color.DARK_GRAY);
			logAlignmentPanel.setForeground(Color.DARK_GRAY);
			logAlignmentPanel.setLayout(new BoxLayout(logAlignmentPanel, BoxLayout.Y_AXIS));
		}
		/* init data variables */
		PNRepResult logReplayResult = data.getLogReplayResult();
		int count = 0;

		/* For each alignment result, construct alignment graph */
		MainLoop: for (SyncReplayResult res : logReplayResult) {
			/*
			 * For each trace that is associate with the alignment res,
			 * construct different graphs because the data dependencies are
			 * different
			 */
			for (int traceIndex : res.getTraceIndex()) {

				if (count > maxNum) {
					break MainLoop;
				}
				// tracePanel for each trace.
				RoundedPanel tracePanel = new RoundedPanel();
				tracePanel.setBackground(logAlignmentPanel.getBackground());
				tracePanel.setLayout(new BoxLayout(tracePanel, BoxLayout.X_AXIS));

				// create alignment info panel (on the left)
				PGraphInfoPanel infoPanel = new PGraphInfoPanelImp(data, traceIndex);
				tracePanel.add(infoPanel);
				// create alignment graph panel (on the right)
				JComponent comp = getNewPOAlignmentGraphPanel(context, traceIndex, res, infoPanel);

				tracePanel.add(comp);
				logAlignmentPanel.add(tracePanel);
				count++;

			}

		}
		return logAlignmentPanel;
	}

	private JComponent getNewPOAlignmentGraphPanel(PluginContext context, int traceIndex, SyncReplayResult res,
			PGraphInfoPanel infoPanel) {

		POSyncReplayResult pores = (POSyncReplayResult) res;

		PAlignment pAlignment = getOrComputePAlignment(pores, traceIndex);

		JComponent comp = null;
		
		if (currentType.equals(POAlignmentVisType.Unfolded_Petrinet)) {
			comp = ProMJGraphVisualizer.instance().visualizeGraph(context, pores.getUnfoldednet());
			
			
		} else {
			PartialOrderGraph graph = PartialOrderGraphFactory.convert(pAlignment, currentType, visType, data.getEventClasses().getClassifier());

			
			comp = new PAlignmentGraphPanel(context, graph, infoPanel);

			// Exporting alignment as graph for case study
			// poAlignmentGraphPanel.exportGraph(XConceptExtension.instance().extractName(data.getLog().get(traceIndex)).replace("/", ""));
			// mapTraceToGraph.put(
			// XConceptExtension.instance().extractName(data.getLog().get(traceIndex)).replace("/", ""),
			// poAlignmentGraphPanel);
		}
		
		return comp;
	}

	private PAlignment getOrComputePAlignment(POSyncReplayResult pores, int traceIndex) {
//		PAlignment pAlignment = null;
//		if (currentType.equals(POAlignmentVisType.S_Alignment)) {
//			pAlignment = POAlignmentBuilder.computeLinearPAlignment(data, traceIndex, pores);
//		} else {
		 PAlignment	pAlignment = pores.getPOAlignmentGraph();
			if (pAlignment == null) {
				PTrace potrace = data.getPOTrace(traceIndex);
				pAlignment = POAlignmentBuilder.computePAlignmentAndUpdateResult(data.getGraph(),
						data.getInitialMarking(), data.getFinalMarkings(), data.getEventClasses(), data.getMapping(),
						data.getXTrace(traceIndex), potrace, traceIndex, pores);
			}
//		}
		return pAlignment;
	}

//	public int getSelectedTraceIndex() {
//		return selectedTraceIndex;
//	}
//
//	public void setSelectedTraceIndex(int selectedTraceIndex) {
//		this.selectedTraceIndex = selectedTraceIndex;
//	}

	/*
	 * The following functions are added to export palignments as graphs.
	 */
	//	public void mouseClicked(MouseEvent e) {
	//		// do nothing
	//	}
	//
	//	public void mousePressed(MouseEvent e) {
	//		if (e.isPopupTrigger()) {
	//			doPop(e);
	//		}
	//	}
	//
	//	public void mouseReleased(MouseEvent e) {
	//		if (e.isPopupTrigger()) {
	//			doPop(e);
	//		}
	//	}
	//
	//	public void mouseEntered(MouseEvent e) {
	//		// do nothing
	//	}
	//
	//	public void mouseExited(MouseEvent e) {
	//		// do nothing
	//	}
	//
	//	private void doPop(MouseEvent e) {
	//		PopUpDemo menu = new PopUpDemo();
	//		menu.show(e.getComponent(), e.getX(), e.getY());
	//	}
	//
	//	class PopUpDemo extends JPopupMenu {
	//		/**
	//		 * 
	//		 */
	//		private static final long serialVersionUID = 3572868114862156397L;
	//		JMenuItem anItem;
	//
	//		public PopUpDemo() {
	//			anItem = new JMenuItem("Export graph");
	//			anItem.addActionListener(new ActionListener() {
	//
	//				public void actionPerformed(ActionEvent e) {
	//					for (String name : mapTraceToGraph.keySet()) {
	//						mapTraceToGraph.get(name).exportGraph(name);
	//					}
	//
	//				}
	//			});
	//			add(anItem);
	//		}
	//	}

}
