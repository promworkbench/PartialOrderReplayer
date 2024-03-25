package org.processmining.partialorder.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.freehep.graphicsbase.util.export.ExportDialog;
import org.processmining.models.jgraph.ProMJGraph;

public class GraphExportPopUp extends JPopupMenu {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3144738820027420510L;
	JMenuItem anItem;
	

	public GraphExportPopUp(final ProMJGraph comp) {
		anItem = new JMenuItem("Export graph");
		anItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ExportDialog export = new ExportDialog();
				export.showExportDialog(null, "Export graph as ...", comp.getComponent(), "View");

			}
		});
		add(anItem);
	}

}
