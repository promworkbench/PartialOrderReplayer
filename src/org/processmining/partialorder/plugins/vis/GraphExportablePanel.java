package org.processmining.partialorder.plugins.vis;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import org.freehep.graphicsbase.util.export.ExportDialog;
import org.processmining.models.jgraph.ProMJGraph;
import org.processmining.partialorder.util.GraphExportPopUp;

import com.fluxicon.slickerbox.components.RoundedPanel;

public abstract class GraphExportablePanel extends RoundedPanel implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5569333282072856203L;
	protected ProMJGraph comp;

	public GraphExportablePanel(int i, int j, int k) {
		super(i, j, k);
	}

	public void mouseClicked(MouseEvent e) {
		// do nothing
	}

	public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger()) {
			doPop(e);
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger()) {
			doPop(e);
		}
	}

	public void mouseEntered(MouseEvent e) {
		// do nothing

	}

	public void mouseExited(MouseEvent e) {
		// do nothing
	}

	private void doPop(MouseEvent e) {
		GraphExportPopUp menu = new GraphExportPopUp(comp);
		menu.show(e.getComponent(), e.getX(), e.getY());
	}

	public void exportGraph(String name) {
		ExportDialog export = new ExportDialog();
		export.showExportDialog(null, "Export graph as ...", comp.getComponent(), name);
	}

}
