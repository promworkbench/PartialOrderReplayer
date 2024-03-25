/*
 * XES Mapper Application (XESame)
 * Generic data conversion tool for extracting event logs
 * 
 * LICENSE: 
 * This software is licensed under the EPL v1.0 license.
 * The license should be provided with this application. 
 * If the license was not provided with the application 
 *   it can be retrieved from 
 *   http://www.eclipse.org/legal/epl-v10.html 
 */

package org.processmining.partialorder.plugins.vis;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.fluxicon.slickerbox.ui.SlickerComboBoxUI;
import com.fluxicon.slickerbox.util.SlickerSwingUtils;

/**
 * This class calls upon the browsers and provides access 
 * to the Mapping objects and controller. 
 * Also helps in creating a nice and constant layout
 * 
 * @version 1.1BETA
 * @author Joos Buijs (j.c.a.m.buijs@tue.nl)
 * 
 */
public class UIController {

	// Configuration standard key names
	public static final String SAVE_LOCATION_MAPPINGFILE = "locationMappingFile";
	public static final String SAVE_LOCATION_CACHEDB = "locationCacheDB";
	public static final String SAVE_LOCATION_EVENTLOG = "locationEventlog";
	public static final String SAVE_LOCATION_VISUALIZATION = "locationVisualization";
	public static final String SAVE_LOCATION_DRIVER = "locationDriver";

//	private static final int VIEWPORT_WIDTH = 500;


//	private ConfigurationSet conf;

	// Standard layout colors and sizes
	public static Color colorLightGrey = new Color(180, 180, 180);
	public static Color colorMidGrey = new Color(160, 160, 160);
	public static Color colorDarkGrey = new Color(120, 120, 120);
	public static Color colorVeryDarkGrey = new Color(80, 80, 80);
	public static Color colorPanelBg = colorLightGrey;

	// And then assign those colors to the items
	public static Color colorTabTitle = new Color(20, 20, 20, 230);
	public static Color colorTabFg = Color.black; // black
	public static Color colorEditBg = new Color(210, 210, 210);
	public static Color colorButtonGroupBg = colorDarkGrey;
	public static Color colorSubPanelBg = colorMidGrey;

	public static int sizeTextfieldHight = 28;



	/**
	 * Applies some paint and border settings to the component
	 */
	@SuppressWarnings("rawtypes")
	public static void makeup(JComponent component) {
		// Filechooser look ugly when half processed so skip them for now
		if (component instanceof JFileChooser) {
			return;
		}

		SlickerSwingUtils.injectSlickerStyle(component, colorLightGrey);
		SlickerSwingUtils.injectBackgroundColor(component, colorLightGrey);
	
		
		/*
		 * Slicker style is only applied for: splitpane, tabbedpane, checkbox,
		 * radiobutton, scrollbar, scrollpane, slider and progressbar
		 * 
		 * Textfields and textarea's are ignored on purpose
		 * 
		 * So, we need to apply some additional styling ourselves
		 */

		// Textfields, area's and panes
		if (component instanceof JTextField || component instanceof JTextArea) {
			JTextComponent textcomponent = (JTextComponent) component;
			textcomponent.setBackground(colorEditBg);
			textcomponent.setSelectedTextColor(colorLightGrey);
			textcomponent.setSelectionColor(colorVeryDarkGrey);
			textcomponent.setDisabledTextColor(Color.white);
			textcomponent.setBorder(BorderFactory
					.createLineBorder(colorDarkGrey));

			// Set a minimal size
			textcomponent.setMinimumSize(new Dimension(100, 28));
		} else if (component instanceof JTextPane) {
			JTextPane textcomponent = (JTextPane) component;
			textcomponent.setBackground(colorEditBg);
			textcomponent.setSelectedTextColor(colorLightGrey);
			textcomponent.setSelectionColor(colorVeryDarkGrey);
			textcomponent.setDisabledTextColor(Color.white);
			textcomponent.setBorder(BorderFactory
					.createLineBorder(colorDarkGrey));
		}
		// Bug in the slicker style injector: it doesn't inject into comboboxes
		else if (component instanceof JComboBox) {
			((JComboBox) component).setUI(new SlickerComboBoxUI());
		}
		// Tables
		else if (component instanceof JTable) {
			JTable table = (JTable) component;

			// Set the header
			table.getTableHeader().setBackground(colorMidGrey);
			table.setBorder(BorderFactory.createLineBorder(colorDarkGrey));

			// And the cells
			table.setSelectionBackground(colorLightGrey);
			table.setGridColor(colorVeryDarkGrey);
		}
		// Tree's are also very important!
		else if (component instanceof JTree) {
			JTree tree = (JTree) component;

			// Now change how the tree cells are displayed!
			DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
			renderer.setTextNonSelectionColor(colorVeryDarkGrey);
			renderer.setBackgroundNonSelectionColor(Color.white);
			renderer.setTextSelectionColor(colorLightGrey);
			renderer.setBackgroundSelectionColor(colorVeryDarkGrey);
			renderer.setBorderSelectionColor(colorMidGrey);
			tree.setCellRenderer(renderer);

		}
	}
}