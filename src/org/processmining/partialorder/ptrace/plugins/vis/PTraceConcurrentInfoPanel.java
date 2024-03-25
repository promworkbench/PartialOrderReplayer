package org.processmining.partialorder.ptrace.plugins.vis;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.processmining.framework.util.ui.widgets.ProMTable;
import org.processmining.partialorder.models.dependency.PDependency;
import org.processmining.partialorder.ptrace.model.PLog;
import org.processmining.partialorder.ptrace.model.PTrace;
import org.processmining.partialorder.ptrace.model.imp.PTraceImp;

import com.fluxicon.slickerbox.components.RoundedPanel;
import com.fluxicon.slickerbox.factory.SlickerDecorator;

import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;

public class PTraceConcurrentInfoPanel extends  RoundedPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6625838612548588952L;
	DefaultTableModel tableModel;
	String selectedName;
	private PLog poLog;

	/**
	 * For traces, create alignment information. 
	 * @param poLog 
	 * @param data Index of traces of alignment
	 * @param sortedSet 
	 */
	public PTraceConcurrentInfoPanel(final String[] names, PLog poLog){
		this.poLog = poLog;
		setBackground(Color.DARK_GRAY);
		setForeground(Color.WHITE);
		BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(boxLayout);	
		
		// add label 
		JLabel label = new JLabel("Select a label to show the number of concurrents: ");
		SlickerDecorator.instance().decorate(label);
		label.setForeground(getForeground());
		Border paddingBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		label.setBorder(paddingBorder);
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		final JComboBox<String> selectNameCombo = new JComboBox<String>(names);
		SlickerDecorator.instance().decorate(selectNameCombo);
		selectNameCombo.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				selectedName = (String) selectNameCombo.getSelectedItem();
				updateModel(getMap(names));
				
			}
		});
		this.add(selectNameCombo);
		selectNameCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
	
		tableModel = new DefaultTableModel();
				
//				getModel(names);
	
		ProMTable promTable = new ProMTable(tableModel);
//		promTable.setMinimumSize(new Dimension(200, 100));
		promTable.setMaximumSize(new Dimension(300, 500));
		promTable.setPreferredSize(new Dimension(300, 500));
		promTable.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.add(promTable);
		//this.setPreferredSize(new Dimension(300, this.getMaximumSize().height));
	}
	
	private void updateModel(Map<String, Integer> names) {
		Object[] columnNames = new Object[] { "Activity", "C-Num" };
		
		List<Object[]> data = new ArrayList<Object[]>();
		for(String n : names.keySet()){
			Object[] row = new Object[]{n , names.get(n)};
			data.add(row);
		}
		 data.toArray(new Object[data.size()][]);
		 tableModel.setDataVector(data.toArray(new Object[data.size()][]), columnNames);
		 tableModel.fireTableStructureChanged();
	}

	private Map<String, Integer> getMap(String[] names){
		Map<String, Integer> stats = new HashMap<String, Integer>();
		for(String n : names){
			stats.put(n, 0);
		}
		
		for(PTrace ptrace : poLog.getTraces()){
			Set<Integer> selectedIndices = new HashSet<Integer>();
			for(Integer i : ptrace.getEventIndices()){
				String name = XConceptExtension.instance().extractName(ptrace.getTrace().get(i));
				if(name.equals(selectedName)){
					selectedIndices.add(i);
				}
			}
			
			PTraceImp potraceGraph = (PTraceImp) ptrace;	

			UnweightedShortestPath<Integer, PDependency> distGraph = new UnweightedShortestPath<Integer, PDependency>(
					potraceGraph);
			for (Integer i : selectedIndices) {
				for (Integer s : ptrace.getEventIndices()) {
					Number n1 = distGraph.getDistance(i, s);
					Number n2 = distGraph.getDistance(s, i);
					if (n1 == null && n2 == null) {
						String name = XConceptExtension.instance().extractName(ptrace.getTrace().get(s));
						stats.put(name, stats.get(name) + 1);
					}
				}
			}
		}
		return stats;
	}

	private void initLayout() {
		setMaximumSize(new Dimension(600, 1020));		
	}
	
	

}
