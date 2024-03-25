package org.processmining.partialorder.plugins.vis.projection;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import org.processmining.framework.util.ui.widgets.ProMTable;
import org.processmining.partialorder.models.projection.DataPattern;
import org.processmining.partialorder.models.projection.LogMovePattern;


public class PAlignModelProjectionInfoPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3833997445202875661L;
	
	DefaultTableModel tableModel;

	public PAlignModelProjectionInfoPanel() {
		
		Object[] columnNames = new Object[] { "Property", "Value" };
		tableModel = new DefaultTableModel(new Object[][] { { "Selected", "NULL" } }, columnNames);

		ProMTable promTable = new ProMTable(tableModel);
		//promTable.setMinimumSize(new Dimension());
//		promTable.getTable().setMaximumSize(new Dimension(100,100));
//		promTable.getTable().setSize(new Dimension(100, 100));
//		promTable.getTable().setPreferredSize(new Dimension(100, 100));
		promTable.setPreferredSize(new Dimension(300, 300));
//		promTable.getTable().setPreferredScrollableViewportSize(
//				new Dimension(PartialOrderAlignmentsOnModelVisPanel.CONFIG_PANEL_WIDTH, this.getHeight()));
//		promTable.getTable().setFillsViewportHeight(true);

		this.add(promTable);
		this.setPreferredSize(new Dimension(300, 200));
	}

	public void updateInfo(LogMovePattern pattern) {

		List<Object[]> infoPattern = new ArrayList<Object[]>();

		Object[] entry = new Object[] { "Log Move", pattern.getLogMoveClassString() };
		infoPattern.add(entry);

		entry = new Object[] { "num. cases", String.valueOf(pattern.getNumberOfTraceIds()) };
		infoPattern.add(entry);

		entry = new Object[] { "num. events", String.valueOf(pattern.getNumberOfEvents()) };
		infoPattern.add(entry);

		Object[] columnNames = new Object[] { "Property", "Value" };
		tableModel.setDataVector(infoPattern.toArray(new Object[0][]), columnNames);
		tableModel.fireTableStructureChanged();

	}

	public void updateInfo(String datakey, List<DataPattern> dataPatterns) {
		List<Object[]> infoPattern = new ArrayList<Object[]>();
		
		
		Object[] entry = new Object[] { "Data", datakey };
		infoPattern.add(entry);
		
		for(DataPattern dp : dataPatterns){
			String prop = (dp.getTransition() != null) ? 
					dp.getTransition().getLabel() : dp.getPattern().getLogMoveClassString() + "(LM)";
			prop += " _ " + dp.getType();
			
			String value = dp.getNumberEvents() + " events (" + dp.getNumberTraces() + " traces)";
			entry = new Object[] {prop , value };
			infoPattern.add(entry);
		}
		
		Object[] columnNames = new Object[] { "Property", "Value" };
		tableModel.setDataVector(infoPattern.toArray(new Object[0][]), columnNames);
		tableModel.fireTableStructureChanged();
		
		
	}
}
