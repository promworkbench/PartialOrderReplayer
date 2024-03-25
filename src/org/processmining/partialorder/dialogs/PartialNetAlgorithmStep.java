/**
 * 
 */
package org.processmining.partialorder.dialogs;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.deckfour.xes.model.XLog;
import org.processmining.annotation.PartialNetReplayAlgorithm;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.plugins.astar.petrinet.AbstractPetrinetReplayer;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerWithILP;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerWithoutILP;
import org.processmining.plugins.astar.petrinet.PrefixBasedPetrinetReplayer;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayAlgorithm;
import org.processmining.plugins.petrinet.replayer.algorithms.behavapp.BehavAppPruneAlg;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteMarkEquationPrune;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompletePruneAlg;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedprefix.CostBasedPrefixAlg;
import org.processmining.plugins.petrinet.replayer.algorithms.swapping.PetrinetSwapReplayer;

import com.fluxicon.slickerbox.components.SlickerTabbedPane;
import com.fluxicon.slickerbox.factory.SlickerFactory;
import com.fluxicon.slickerbox.ui.SlickerComboBoxUI;

/**
 * @author x.lu
 * 
 * Copied from Arya's PNAlgorithmStep
 * [Changes]
 * 
 * 17-01-2014 : instead of choosing all algorithms, we choose algorithms that are partial aware, 
 * thus annotated with PartialNetReplayAlgorithm:
 * Set<Class<?>> coverageEstimatorClasses = context.getPluginManager().getKnownClassesAnnotatedWith(
				PartialNetReplayAlgorithm.class);
 * 
 */
public class PartialNetAlgorithmStep extends JComponent {

	private static final long serialVersionUID = 7638460262987517737L;

	// overall layout
	@SuppressWarnings("unused")
	private boolean useBasic = true;
	private SlickerTabbedPane tabPane; // tab basic/advance
	private JPanel advancedPanel;
	private JPanel basicPanel;

	// for basic panel
	@SuppressWarnings("rawtypes")
	private JList algorithmList;

	// for expert panel
	@SuppressWarnings("rawtypes")
	private JComboBox combo;
	private JLabel label;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PartialNetAlgorithmStep(PluginContext context, final PetrinetGraph net, final XLog log,
			final TransEvClassMapping mapping) {
		double mainPanelSize[][] = { { TableLayoutConstants.FILL }, { TableLayoutConstants.FILL } };
		setLayout(new TableLayout(mainPanelSize));

		// init tab
		tabPane = new SlickerTabbedPane("Choose algorithm", new Color(200, 200, 200, 230), new Color(0, 0, 0, 230),
				new Color(220, 220, 220, 150));

		// init instance
		SlickerFactory slickerFactory = SlickerFactory.instance();

		/**
		 * WIZARD PANEL
		 */
		basicPanel = new JPanel();
		double sizeBasic[][] = { { 30, TableLayoutConstants.FILL, 50 },
				{ 80, 30, 30, 30, 30, 30, 30, 60, TableLayoutConstants.FILL } };
		basicPanel.setLayout(new TableLayout(sizeBasic));
		basicPanel.setBackground(new Color(200, 200, 200));

		// add question
		basicPanel
				.add(slickerFactory
						.createLabel("<html><h1>Select Algorithm</h1><p>Select your replay algorithm</p></html>"),
						"0, 0, 1, 0, l, t");

		final JRadioButton fitnessRB = slickerFactory.createRadioButton("Measuring fitness");
		final JRadioButton fitnessCompleteRB = slickerFactory.createRadioButton("Yes");
		fitnessRB.setSelected(true);
		fitnessCompleteRB.setSelected(true);
		final JRadioButton fitnessNotCompleteRB = slickerFactory.createRadioButton("No");
		final JRadioButton behavAppRB = slickerFactory.createRadioButton("Measuring behavioral appropriateness");
		DefActListener defaultAction = new DefActListener(context, fitnessRB, fitnessCompleteRB, net, log, mapping);

		ButtonGroup algTypeSelection = new ButtonGroup();
		fitnessRB.addActionListener(defaultAction);
		behavAppRB.addActionListener(defaultAction);
		algTypeSelection.add(fitnessRB);
		algTypeSelection.add(behavAppRB);

		JLabel questCompleteFitness = slickerFactory.createLabel("Would you penalize improper completion?");
		ButtonGroup fitnessTypeSelection = new ButtonGroup();
		fitnessCompleteRB.addActionListener(defaultAction);
		fitnessNotCompleteRB.addActionListener(defaultAction);
		fitnessTypeSelection.add(fitnessCompleteRB);
		fitnessTypeSelection.add(fitnessNotCompleteRB);

		int basicRowCounter = 1;
		basicPanel.add(slickerFactory.createLabel("<html><h3>What is the purpose of your replay?</h3></html>"), "0,"
				+ basicRowCounter + ",2," + basicRowCounter + ",l,b");
		basicRowCounter++;
		basicPanel.add(fitnessRB, "0," + basicRowCounter + ",2," + basicRowCounter + ",l,b");
		basicRowCounter++;
		basicPanel.add(questCompleteFitness, "0," + basicRowCounter + ",2," + basicRowCounter + ",l,b");
		basicRowCounter++;
		basicPanel.add(fitnessCompleteRB, "1," + basicRowCounter + ",2," + basicRowCounter + ",l,b");
		basicRowCounter++;
		basicPanel.add(fitnessNotCompleteRB, "1," + basicRowCounter + ",2," + basicRowCounter + ",l,b");
		basicRowCounter++;

		basicPanel.add(behavAppRB, "0," + basicRowCounter + ",2," + basicRowCounter + ",l,b");
		basicRowCounter++;

		basicPanel.add(slickerFactory.createLabel("<html><h3>Suggested algorithm(s)</h3></html>"), "0,"
				+ basicRowCounter + ",2," + basicRowCounter + ",l,b");
		basicRowCounter++;
		algorithmList = new JList();
		algorithmList.setSize(basicPanel.getWidth(), algorithmList.getHeight());
		basicPanel.add(algorithmList, "0," + basicRowCounter + ",2," + basicRowCounter + ",l,t");

		populateBasicAlgorithms(context, fitnessRB.isSelected(), fitnessCompleteRB.isSelected(), net, log, mapping);

		/**
		 * EXPERT PANEL
		 */
		advancedPanel = new JPanel();
		double size[][] = { { TableLayoutConstants.FILL }, { 80, 30, TableLayoutConstants.FILL } };
		advancedPanel.setLayout(new TableLayout(size));
		advancedPanel.setBackground(new Color(200, 200, 200));

		advancedPanel.add(slickerFactory
				.createLabel("<html><h1>Select Algorithm</h1><p>Select your replay algorithm.</p></html>"),
				"0, 0, l, t");

		// add combobox
		// get all algorithms from the framework
		Set<Class<?>> coverageEstimatorClasses = context.getPluginManager().getKnownClassesAnnotatedWith(
				PartialNetReplayAlgorithm.class);
		IPNReplayAlgorithm[] availAlgorithms = null;
		if (coverageEstimatorClasses != null) {
			List<IPNReplayAlgorithm> algList = new LinkedList<IPNReplayAlgorithm>();
			for (Class<?> coverClass : coverageEstimatorClasses) {
				try {
					IPNReplayAlgorithm alg = (IPNReplayAlgorithm) coverClass.newInstance();
					if (alg.isReqWOParameterSatisfied(context, net, log, mapping)) {
						algList.add(alg);
					}
				} catch (InstantiationException e1) {
					// do nothing
				} catch (IllegalAccessException e1) {
					// do nothing
				} catch (Exception exc) {
					// do nothing
				}
			}
			Collections.sort(algList, new Comparator<IPNReplayAlgorithm>() {

				public int compare(IPNReplayAlgorithm o1, IPNReplayAlgorithm o2) {
					return o1.toString().compareTo(o2.toString());
				}
			});
			availAlgorithms = algList.toArray(new IPNReplayAlgorithm[algList.size()]);
			
		}

		if (availAlgorithms == null) {
			JOptionPane.showMessageDialog(null, "No replay algorithm is found.", "Error", JOptionPane.ERROR_MESSAGE);
			context.getFutureResult(0).cancel(true);
			return;
		}

		combo = new JComboBox(availAlgorithms);
		combo.setPreferredSize(new Dimension(150, 25));
		combo.setSize(new Dimension(150, 25));
		combo.setMinimumSize(new Dimension(150, 25));
		combo.setSelectedItem(0);
		combo.setUI(new SlickerComboBoxUI());
		advancedPanel.add(combo, "0, 1");

		combo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				label.setText(((IPNReplayAlgorithm) combo.getSelectedItem()).getHTMLInfo());
			}
		});

		label = slickerFactory.createLabel(availAlgorithms[0].getHTMLInfo());
		label.setPreferredSize(new Dimension(150, 300));

		advancedPanel.add(label, "0, 2");

		// XXC: removed basic wizard since only one partial alignment algorithm
//		tabPane.addTab("Basic wizard", basicPanel, new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				useBasic = true;
//			}
//		});
		tabPane.addTab("Advanced", advancedPanel, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				useBasic = false;
				
				// set algorithm to be the same as basic
				Class<? extends Object> classAlg = algorithmList.getSelectedValue().getClass();
				for (int i=0; i < combo.getItemCount(); i++){
					if (combo.getItemAt(i).getClass().equals(classAlg)){
						combo.setSelectedIndex(i);
					}
				}
			}
		});
		tabPane.setSize(advancedPanel.getPreferredSize());
		add(tabPane, "0,0");
		setSize(tabPane.getPreferredSize());
	}

	@SuppressWarnings("unchecked")
	private void populateBasicAlgorithms(PluginContext context, boolean isFitness, boolean isComplete,
			PetrinetGraph net, XLog log, TransEvClassMapping mapping) {
		this.algorithmList.removeAll();
		List<IPNReplayAlgorithm> listAlgorithms = new LinkedList<IPNReplayAlgorithm>();
		if (isFitness) {
			if (isComplete) {
				// check final markings
				Collection<FinalMarkingConnection> conns;
				try {
					conns = context.getConnectionManager().getConnections(FinalMarkingConnection.class, context, net);
					if (conns != null){
						for (FinalMarkingConnection conn : conns){
							if (conn.getObjectWithRole(FinalMarkingConnection.MARKING) != null){
								AbstractPetrinetReplayer<?, ?> expressSwap = new PetrinetSwapReplayer();
								if (expressSwap.isReqWOParameterSatisfied(context, net, log, mapping)) {
									listAlgorithms.add(expressSwap);
								};

								AbstractPetrinetReplayer<?, ?> express = new PetrinetReplayerWithILP();
								if (express.isReqWOParameterSatisfied(context, net, log, mapping)) {
									listAlgorithms.add(express);
								};
								
								break;
							};
						}
					}
				} catch (ConnectionCannotBeObtained e) {
					// do nothing
				}
				
				// check efficient replayer
				AbstractPetrinetReplayer<?, ?> pnReplayerWOILP = new PetrinetReplayerWithoutILP();
				if (pnReplayerWOILP.isReqWOParameterSatisfied(context, net, log, mapping)) {
					listAlgorithms.add(pnReplayerWOILP);
				} else {
					// check prune replayer
					CostBasedCompletePruneAlg prune = new CostBasedCompletePruneAlg();
					if (prune.isReqWOParameterSatisfied(context, net, log, mapping)) {
						listAlgorithms.add(prune);
					}
					
					// check the one with marking equation
					CostBasedCompleteMarkEquationPrune markEqPrune = new CostBasedCompleteMarkEquationPrune();
					if (markEqPrune.isReqWOParameterSatisfied(context, net, log, mapping)) {
						listAlgorithms.add(markEqPrune);
					}
					
				}
			} else {
				// check prefix replayer
				PrefixBasedPetrinetReplayer prefixReplayer = new PrefixBasedPetrinetReplayer();
				if (prefixReplayer.isReqWOParameterSatisfied(context, net, log, mapping)){
					listAlgorithms.add(prefixReplayer);
				} else {
					// check cost based
					CostBasedPrefixAlg costBased = new CostBasedPrefixAlg();
					if (costBased.isReqWOParameterSatisfied(context, net, log, mapping)) {
						listAlgorithms.add(costBased);
					}					
				}				
			}
			algorithmList.setListData(listAlgorithms.toArray(new Object[] { listAlgorithms.size() }));
		} else {
			algorithmList.setListData(new Object[] { new BehavAppPruneAlg() });
		}
		this.algorithmList.setSelectedIndex(0);
	}

	/**
	 * Obtain the selected algorithm
	 * 
	 * @return
	 */
	public IPNReplayAlgorithm getAlgorithm() {
//		if (useBasic) {
//			return (IPNReplayAlgorithm) this.algorithmList.getSelectedValue();
//		} else {
			return (IPNReplayAlgorithm) combo.getSelectedItem();
//		}
	}

	class DefActListener implements ActionListener {

		private AbstractButton fitnessRB;
		private AbstractButton fitnessCompleteRB;
		private PetrinetGraph net;
		private XLog log;
		private TransEvClassMapping mapping;
		private PluginContext context;

		public DefActListener(PluginContext context, AbstractButton fitnessRB, AbstractButton fitnessCompleteRB,
				PetrinetGraph net, XLog log, TransEvClassMapping mapping) {
			this.context = context;
			this.fitnessRB = fitnessRB;
			this.fitnessCompleteRB = fitnessCompleteRB;
			this.net = net;
			this.log = log;
			this.mapping = mapping;
		}

		public void actionPerformed(ActionEvent e) {
			populateBasicAlgorithms(context, fitnessRB.isSelected(), fitnessCompleteRB.isSelected(), net, log, mapping);
		}
	}
}
