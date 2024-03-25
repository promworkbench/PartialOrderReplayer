package org.processmining.partialorder.plugins.vis.projection;

import java.awt.Color;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.processmining.partialorder.models.projection.DataPattern;
import org.processmining.partialorder.models.projection.LogMovePattern;
import org.processmining.partialorder.models.projection.POAlignmentOnModelDataProvider;

import com.fluxicon.slickerbox.components.RoundedPanel;
import com.fluxicon.slickerbox.ui.SlickerScrollBarUI;

public class PProjectionConfigPanel extends RoundedPanel implements ListSelectionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3371900942877971155L;
	protected static Color colorBg = new Color(140, 140, 140);
	protected static Color colorOuterBg = new Color(100, 100, 100);
	protected static Color colorListBg = new Color(60, 60, 60);
	protected static Color colorListBgSelected = new Color(10, 90, 10);
	protected static Color colorListFg = new Color(200, 200, 200, 160);
	protected static Color colorListFgSelected = new Color(230, 230, 230, 200);
	protected static Color colorListEnclosureBg = new Color(150, 150, 150);
	protected static Color colorListHeader = new Color(10, 10, 10);
	protected static Color colorListDescription = new Color(60, 60, 60);
	
	
	PAlignmentsOnModelVisPanel parent;
	private POAlignmentOnModelDataProvider data;
	private JList<String> logMovelist;
	private JList<String> dataElemList;
	private PAlignModelProjectionInfoPanel infoComp;


	public PProjectionConfigPanel(final PAlignmentsOnModelVisPanel p, final POAlignmentOnModelDataProvider d) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBackground(colorListHeader);
		this.parent = p;
		this.data = d;
		
		this.infoComp = new PAlignModelProjectionInfoPanel();
		this.infoComp.setBackground(colorListHeader);
		
		
		/* Log moves selector */
		this.logMovelist = new JList<String>(data.getLogMoveEClasses().toArray(new String[data.getLogMoveEClasses().size()]));
		logMovelist.getSelectionModel().addListSelectionListener(this);
		JComponent logMoveListComp = configureMultiSelectionList(logMovelist, "Select Log Moves", 
				"Visualize only the log moves selected");
	
		/* Data element selector */
		this.dataElemList = new JList<String>(data.getDataElements().toArray(new String[data.getDataElements().size()]));
		dataElemList.getSelectionModel().addListSelectionListener(this);
		JComponent dataElemListComp = configureMultiSelectionList(dataElemList, "Select Data Elements", 
				"Visualize only the data attributes selected");
		
		add(infoComp);
		add(logMoveListComp);
		add(dataElemListComp);
		
	}
	
	public void updateInfo(LogMovePattern pattern){
		this.infoComp.updateInfo(pattern);
	}
	
	public void updateInfo(String datakey, List<DataPattern> dataPatternsOfKey) {
		this.infoComp.updateInfo(datakey, dataPatternsOfKey);
		
	}


	/*
	 * Copied from LogFilterUI developed by hverbeek
	 */
	@SuppressWarnings("rawtypes")
	public static JComponent configureMultiSelectionList(JList list, String title, String description) {
		cofigureListBasic(list, title, description);
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.setSelectionInterval(0, list.getModel().getSize() - 1);
		return configureAnyScrollable(list, title, description);
	}
	
	@SuppressWarnings("rawtypes")
	public static JComponent configureSingleSelectionList(JList list, String title, String description) {
		cofigureListBasic(list, title, description);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//		list.setSelectionInterval(0, list.getModel().getSize() - 1);
		return configureAnyScrollable(list, title, description);
	}
	
	
	@SuppressWarnings("rawtypes")
	public static void cofigureListBasic(JList list, String title, String description){
		list.setFont(list.getFont().deriveFont(13f));
		list.setBackground(colorListBg);
		list.setForeground(colorListFg);
		list.setSelectionBackground(colorListBgSelected);
		list.setSelectionForeground(colorListFgSelected);
		list.setFont(list.getFont().deriveFont(12f));
	}
	
	protected static JComponent configureAnyScrollable(JComponent scrollable, String title, String description) {
		RoundedPanel enclosure = new RoundedPanel(10, 5, 5);
		enclosure.setBackground(colorListEnclosureBg);
		enclosure.setLayout(new BoxLayout(enclosure, BoxLayout.Y_AXIS));
		JLabel headerLabel = new JLabel(title);
		headerLabel.setOpaque(false);
		headerLabel.setForeground(colorListHeader);
		headerLabel.setFont(headerLabel.getFont().deriveFont(14f));
		JLabel descriptionLabel = new JLabel("<html>" + description + "</html>");
		descriptionLabel.setOpaque(false);
		descriptionLabel.setForeground(colorListDescription);
		descriptionLabel.setFont(descriptionLabel.getFont().deriveFont(11f));
		JScrollPane listScrollPane = new JScrollPane(scrollable);
		listScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		listScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		listScrollPane.setViewportBorder(BorderFactory.createLineBorder(new Color(40, 40, 40)));
		listScrollPane.setBorder(BorderFactory.createEmptyBorder());
		JScrollBar vBar = listScrollPane.getVerticalScrollBar();
		vBar.setUI(new SlickerScrollBarUI(vBar, colorListEnclosureBg, new Color(30, 30, 30), new Color(80, 80, 80), 4,
				12));
		enclosure.add(packLeftAligned(headerLabel));
		enclosure.add(Box.createVerticalStrut(3));
		enclosure.add(packLeftAligned(descriptionLabel));
		enclosure.add(Box.createVerticalStrut(5));
		enclosure.add(listScrollPane);
		return enclosure;
	}
	
	protected static JComponent packLeftAligned(JComponent component) {
		JPanel packed = new JPanel();
		packed.setOpaque(false);
		packed.setBorder(BorderFactory.createEmptyBorder());
		packed.setLayout(new BoxLayout(packed, BoxLayout.X_AXIS));
		packed.add(component);
		packed.add(Box.createHorizontalGlue());
		return packed;
	}


	/*
	 *  ListSelectionListener: change the selected data elements and log move 
	 *  in the data {@link POAlignmentOnModelDataProvider}, calls the update function 
	 *  in POAlignmentsOnModelVisPanel
	 */
	public void valueChanged(ListSelectionEvent e) {
		 ListSelectionModel lsm = (ListSelectionModel)e.getSource();
		 boolean changeLogMoves = this.logMovelist.getSelectionModel().equals(lsm);
		 
	        int firstIndex = e.getFirstIndex();
	        int lastIndex = e.getLastIndex();
	        //boolean isAdjusting = e.getValueIsAdjusting();
	        for(int i = firstIndex; i<= lastIndex; i++){
	        	if(changeLogMoves){
		        	String lm = logMovelist.getModel().getElementAt(i);
		        	if(lsm.isSelectedIndex(i)){
		        		
		        		data.setLogMoveEClassSelected(lm, true);
		        	} else {
		        		data.setLogMoveEClassSelected(lm, false);
		        	}
	        	} else {
	        		String dataElem = dataElemList.getModel().getElementAt(i);
	        		if(lsm.isSelectedIndex(i)){
		        		
		        		data.setDataElementSelected(dataElem, true);
		        	} else {
		        		data.setDataElementSelected(dataElem, false);
		        	}
	        	}
	        }
	        parent.updateProjection();
		
	}

	


}
