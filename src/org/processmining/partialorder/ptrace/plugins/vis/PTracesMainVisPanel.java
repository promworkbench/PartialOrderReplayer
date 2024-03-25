package org.processmining.partialorder.ptrace.plugins.vis;

import info.clearthought.layout.TableLayout;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XLogImpl;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.util.ui.widgets.ProMTextField;
import org.processmining.partialorder.models.graph.PartialOrderGraph;
import org.processmining.partialorder.plugins.vis.PGraphColorStyle;
import org.processmining.partialorder.plugins.vis.PartialVisualType;
import org.processmining.partialorder.plugins.vis.UIController;
import org.processmining.partialorder.plugins.vis.palignment.PGraphInfoPanel;
import org.processmining.partialorder.plugins.vis.palignment.PGraphInfoPanelImp;
import org.processmining.partialorder.plugins.vis.projection.PProjectionConfigPanel;
import org.processmining.partialorder.ptrace.model.PLog;
import org.processmining.partialorder.ptrace.model.PTrace;
import org.processmining.partialorder.ptrace.plugins.builder.alg.PTraceUtil;
import org.processmining.partialorder.ptrace.plugins.conversion.PTraceToGraphConversion;
import org.processmining.partialorder.ptrace.plugins.vis.model.FilterDistance;
import org.processmining.partialorder.ptrace.plugins.vis.model.FilterDistance.ComparisonType;
import org.processmining.partialorder.util.VisUtil;

import com.fluxicon.slickerbox.factory.SlickerDecorator;
import com.fluxicon.slickerbox.factory.SlickerFactory;

public class PTracesMainVisPanel extends JComponent implements ListSelectionListener {
	private static final String NO_LABEL_FOUND = "No label found";

	private static final long serialVersionUID = 8702589427013827575L;

	// Data
	private PluginContext context;
	private PLog poLog;

	private List<PTrace> selectedPTraces = new ArrayList<PTrace>();

	private List<PTraceGraphPanel> graphPanels = new ArrayList<PTraceGraphPanel>();

	private JList<String> eventColorList;
	private Map<String, Color> mapEventToColor;

	private JPanel pTracesPanel;
	private JScrollPane pTracesScrollPane;

	private boolean applyFilter = false;
	private FilterDistance filter = new FilterDistance();
	private PartialVisualType visType = PartialVisualType.MINIMAL_REDUTION;
	private XEventClassifier classifier = new XEventNameClassifier();
	
	//	private Object selectedName;
	private int maxNum = 10;



	public PTracesMainVisPanel(PluginContext context, PLog poLog) {
		this.poLog = poLog;
		this.context = context;
		initLayout();
		addComponentPTraceListPanel();
		addComponentControllerPanel();
	}

	private void initLayout() {
		TableLayout mainLayout = new TableLayout(new double[][] { { TableLayout.FILL, 300 }, { TableLayout.FILL } });
		setLayout(mainLayout);
		setBorder(BorderFactory.createEmptyBorder());
		setBackground(Color.DARK_GRAY);
		setForeground(Color.WHITE);
	}

	private void addComponentPTraceListPanel() {
		pTracesPanel = new JPanel();
		pTracesPanel.setBackground(Color.DARK_GRAY);
		pTracesPanel.setForeground(Color.WHITE);
		pTracesPanel.setLayout(new BoxLayout(pTracesPanel, BoxLayout.Y_AXIS));
		
		pTracesScrollPane = VisUtil.getProMStyleScrollPane(addComponentsPTracePanels());
		add(pTracesScrollPane, "0,0");
	}

	private void addComponentControllerPanel() {
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setBackground(Color.DARK_GRAY);
		tabbedPane.setForeground(Color.WHITE);
		//		tabbedPane.getRootPane().setBackground(Color.DARK_GRAY);
		//		tabbedPane.getRootPane().setForeground(Color.WHITE);
		UIManager.put("TabbedPane.selected", Color.DARK_GRAY);
		UIManager.put("TabbedPane.borderHightlightColor", Color.DARK_GRAY);
		UIManager.put("TabbedPane.contentAreaColor", Color.DARK_GRAY);
		UIManager.put("TabbedPane.highlight", Color.DARK_GRAY);
		//		UIManager.put("TabbedPane.light", Color.DARK_GRAY);
		UIManager.put("TabbedPane.selectHighlight", Color.DARK_GRAY);
		UIManager.put("TabbedPane.shadow", Color.DARK_GRAY);
		UIManager.put("TabbedPane.unselectedTabShadow", Color.DARK_GRAY);

		initAllEventNamesAndColor(poLog);

		tabbedPane.addTab("Settings", getComponentVisSettings());
		tabbedPane.addTab("Highlight", getComponentSelectEventColor());
		tabbedPane.addTab("Filter", getComponentFilterPanel());
//		tabbedPane.addTab("Stats", getComponentStatisticPanel());

		for (int i = 0; i < tabbedPane.getTabCount(); i++) {
			tabbedPane.setBackgroundAt(i, Color.LIGHT_GRAY);
			tabbedPane.setForegroundAt(i, Color.WHITE);
		}

		UIController.makeup(tabbedPane);
		add(tabbedPane, "1,0");
	}

	private void initAllEventNamesAndColor(PLog poLog) {
		Map<String, Color> names = new HashMap<String, Color>();
		for (PTrace poTrace : poLog.getTraces()) {
			for (XEvent e : poTrace.getTrace()) {
				names.put(XConceptExtension.instance().extractName(e), PGraphColorStyle.EVENT_NODE_COLOR);
			}
		}
		this.mapEventToColor = names;
	}

	private String[] getAllEventNames() {
		return mapEventToColor.keySet().toArray(new String[mapEventToColor.keySet().size()]);
	}

	private Component getComponentVisSettings() {
		JComponent panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(Color.DARK_GRAY);
		panel.setForeground(Color.WHITE);

		Border paddingBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);

		
		JLabel labelclassifier = new JLabel("Use this classifier");
		SlickerDecorator.instance().decorate(labelclassifier);
		labelclassifier.setForeground(panel.getForeground());
		labelclassifier.setBorder(paddingBorder);
		labelclassifier.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(labelclassifier);

		final JComboBox<XEventClassifier> selectClassifier = new JComboBox<XEventClassifier>();
		selectClassifier.addItem(new XEventAttributeClassifier("default", XConceptExtension.KEY_NAME));
		for(XEventClassifier c : poLog.getXLog().getClassifiers()){
			selectClassifier.addItem(c);
		}
		selectClassifier.setSelectedIndex(0);
		SlickerDecorator.instance().decorate(selectClassifier);
		
		selectClassifier.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				XEventClassifier value = (XEventClassifier) selectClassifier.getSelectedItem();
				classifier = value;
				updateComponentsPTracePanels();
			}
		});
		selectClassifier.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(selectClassifier);
		
		
		JLabel labelnum = new JLabel("Show this number of PTraces: ");
		SlickerDecorator.instance().decorate(labelnum);
		labelnum.setForeground(panel.getForeground());
		labelnum.setBorder(paddingBorder);
		labelnum.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(labelnum);

		final JComboBox<Integer> select = new JComboBox<Integer>(new Integer[] { 10, 50, 100, Integer.MAX_VALUE });
		select.setSelectedIndex(0);
		SlickerDecorator.instance().decorate(select);
		;
		select.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Integer value = (Integer) select.getSelectedItem();
				maxNum = value;
				updateComponentsPTracePanels();
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
				PartialVisualType.toPTraceVisualTypes());
		visCombo.setSelectedItem(visType);
		SlickerDecorator.instance().decorate(visCombo);
		;
		visCombo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				visType = (PartialVisualType) visCombo.getSelectedItem();
				updateComponentsPTracePanels();
			}
		});
		visCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(visCombo);

		return panel;
	}

	private JComponent getComponentSelectEventColor() {
		eventColorList = new JList<String>(getAllEventNames());
		eventColorList.getSelectionModel().addListSelectionListener(this);
		PProjectionConfigPanel.configureSingleSelectionList(eventColorList, "Select Log Moves",
				"Visualize only the log moves selected");
		eventColorList.setCellRenderer(new SelectedListCellRenderer());
		return eventColorList;
	}

	private JComponent getComponentFilterPanel() {
		SlickerDecorator decor = SlickerDecorator.instance();

		JComponent panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBackground(Color.DARK_GRAY);
		panel.setForeground(Color.WHITE);

		//		JLabel title = SlickerFactory.instance().createLabel("Select the p-traces that: ");
		JLabel label = createLabel("Select the p-traces that: ");
		panel.add(label);

		final JComboBox<String> predCombo = new JComboBox<String>();
		for (String n:getAllEventNames()){
			predCombo.addItem(n == null? NO_LABEL_FOUND : n);
		}
		final JComboBox<String> succCombo = new JComboBox<String>();
		for(String n : getAllEventNames()){
			succCombo.addItem(n == null? NO_LABEL_FOUND : n);
		}
		if(getAllEventNames().length == 0){
			succCombo.addItem(NO_LABEL_FOUND);
			predCombo.addItem(NO_LABEL_FOUND);
		}
		
		final JComboBox<ComparisonType> compareCombo = new JComboBox<ComparisonType>(ComparisonType.values());
		final ProMTextField numberField = new ProMTextField();
		numberField.setBackground(Color.LIGHT_GRAY);
		numberField.setForeground(Color.LIGHT_GRAY);
		decor.decorate(predCombo);
		decor.decorate(succCombo);
		decor.decorate(compareCombo);
		predCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
		succCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
		compareCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
		numberField.setAlignmentX(Component.LEFT_ALIGNMENT);

		panel.add(createLabel("From event: "));
		panel.add(predCombo);
		panel.add(createLabel("To event: "));
		panel.add(succCombo);
		panel.add(createLabel("Using this criterion: "));
		panel.add(compareCombo);
		
		panel.add(createLabel("With this number: "));
		
		panel.add(numberField);

		Box buttonbox = Box.createHorizontalBox();

		buttonbox.add(Box.createHorizontalGlue());
		buttonbox.setAlignmentX(Component.LEFT_ALIGNMENT);
		buttonbox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		JButton filterButton = SlickerFactory.instance().createButton("Apply Filter");
		filterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				applyFilter = true;

				String pred = (String) predCombo.getSelectedItem();
				String succ = (String) succCombo.getSelectedItem();
				
				if(pred.equals(NO_LABEL_FOUND) || succ.equals(NO_LABEL_FOUND)){
					return;
				}
				
				
				ComparisonType compareType = compareCombo.getItemAt(compareCombo.getSelectedIndex());
				int dist = 0;

				try {
					dist = Integer.parseInt(numberField.getText());
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(null, "Can't parse the number, please type a number.");
					return;
				}

				filter.update(pred, succ, compareType, dist);
				updateComponentsPTracePanels();

			}
		});

		JButton clearFilterButton = SlickerFactory.instance().createButton("Clear Filter");
		clearFilterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				applyFilter = false;
				updateComponentsPTracePanels();
			}
		});

		JButton exportButton = SlickerFactory.instance().createButton("Export");
		exportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				XLog log = new XLogImpl(new XAttributeMapImpl());
				for (PTrace p : selectedPTraces) {
					log.add(p.getTrace());
				}
				context.getProvidedObjectManager().createProvidedObject("FilteredLog", log, XLog.class, context);
			}
		});

		buttonbox.add(filterButton);
		buttonbox.add(clearFilterButton);
		buttonbox.add(exportButton);
		panel.add(buttonbox);
		return panel;
	}

	private JLabel createLabel(String l) {
		JLabel label = new JLabel(l);
		SlickerDecorator.instance().decorate(label);
		label.setForeground(Color.white);
		Border paddingBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		label.setBorder(paddingBorder);
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		return label;
	}

	private Component getComponentStatisticPanel() {
		return new PTraceConcurrentInfoPanel(getAllEventNames(), poLog);
	}

	private JComponent addComponentsPTracePanels() {
		PTraceToGraphConversion conv = new PTraceToGraphConversion();
		selectedPTraces.clear();
		for (int i = 0; i < maxNum && i < poLog.size(); i++) {
			PTrace potrace = poLog.get(i);
			if (!applyFilter || (applyFilter && filter != null && PTraceUtil.areEventsWithInDistance(potrace, filter))) {

				selectedPTraces.add(potrace);

				JPanel tracePanel = new JPanel();
				tracePanel.setBackground(Color.DARK_GRAY);
				tracePanel.setForeground(Color.WHITE);

				tracePanel.setLayout(new BoxLayout(tracePanel, BoxLayout.X_AXIS));
				
				
				PGraphInfoPanel poTraceInfoPanel = new PGraphInfoPanelImp(i, poLog.get(i));
				poTraceInfoPanel.setBackground(Color.DARK_GRAY);	
				tracePanel.add(poTraceInfoPanel);

				PartialOrderGraph graph = conv.convert(potrace, this.visType, this.classifier);
				PTraceGraphPanel poTraceGraphPanel = new PTraceGraphPanel(context, graph, poTraceInfoPanel);
				graphPanels.add(poTraceGraphPanel);
				tracePanel.add(poTraceGraphPanel);

				pTracesPanel.add(tracePanel);
			}
		}
		return pTracesPanel;
	}

	/*
	 * Create for each partially ordered TRACE in the log one panel @tracePanel
	 * The panel @tracePanel has two component: @poTraceInfoPanel and
	 * 
	 * @poTraceGraphPanel
	 */
	private void updateComponentsPTracePanels() {
		pTracesPanel.removeAll();
		// TODOP update graph instead of build the panels
		addComponentsPTracePanels();

		pTracesPanel.revalidate();
		pTracesPanel.repaint();
		pTracesScrollPane.setViewportView(pTracesPanel);

	}

	public void valueChanged(ListSelectionEvent e) {

		if (e.getValueIsAdjusting() == false) {
			if (eventColorList.getSelectedIndex() == -1) {
				//No selection, disable fire button.
				//fireButton.setEnabled(false);
			} else {
				Color newColor = JColorChooser.showDialog(this, "Choose Background Color", null);
				mapEventToColor.put(eventColorList.getSelectedValue(), newColor);
				for (PTraceGraphPanel panel : graphPanels) {
					panel.updateEventColor(mapEventToColor);
				}
			}
		}
	}

	public class SelectedListCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = -2089347684127611653L;

		@SuppressWarnings("rawtypes")
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

			c.setForeground(mapEventToColor.get(value));
			return c;
		}
	}

}
