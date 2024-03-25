package org.processmining.partialorder.plugins.vis.palignment;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.processmining.framework.util.ui.widgets.ProMTable;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.partialorder.models.dependency.PDependency;
import org.processmining.partialorder.models.dependency.PDependencyDataAware;
import org.processmining.partialorder.models.dependency.PDependencyDataAware.EnumDataDependency;
import org.processmining.partialorder.models.graph.POEdge;
import org.processmining.partialorder.models.graph.node.POEventNode;
import org.processmining.partialorder.models.graph.node.POLogMoveNode;
import org.processmining.partialorder.models.graph.node.POModelMoveVisibleNode;
import org.processmining.partialorder.models.graph.node.PONodeMove;
import org.processmining.partialorder.models.graph.node.POSyncMoveNode;
import org.processmining.partialorder.models.replay.POAlignmentDataProvider;
import org.processmining.partialorder.ptrace.model.PTrace;
import org.processmining.partialorder.util.GraphUtil;
import org.processmining.partialorder.util.VisUtil;

public class PGraphInfoPanelImp extends PGraphInfoPanel implements ItemListener {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 6625838612548588952L;
		DefaultTableModel tableModel;
		private POAlignmentDataProvider data;
		private int currentTrace;
		
		public PGraphInfoPanelImp(POAlignmentDataProvider data, int traceIndex){
			this.data = data;
			setBackground(Color.DARK_GRAY);
			currentTrace = traceIndex;
			initLayout();
			
			// Add selection listener
//			JComboBox<Integer> combo = SlickerFactory.instance().createComboBox(traces.toArray(new Integer[traces.size()]));
////			combo.setPreferredSize(new Dimension(200, 10));
////			combo.setMinimumSize(new Dimension(50, 10));
////			combo.setMaximumSize(new Dimension(200, 10));
//			combo.addItemListener(this);
//			currentTrace = traces.first();
//			combo.setSelectedItem(currentTrace);
//			this.add(combo);
			
			Object[] columnNames = new Object[] { "Property", "Value" };
			tableModel = new DefaultTableModel(new Object[][]{{"Trace : ", currentTrace}, 
					{"Trace name", XConceptExtension.instance().extractName(data.getLog().get(currentTrace))}}, columnNames);
		
			ProMTable promTable = new ProMTable(tableModel);
			promTable.setMinimumSize(new Dimension(200, 100));
			promTable.setMaximumSize(new Dimension(600, 1000));
			promTable.setPreferredSize(new Dimension(600, 280));
			
			
			this.add(promTable);
			//this.setPreferredSize(new Dimension(300, this.getMaximumSize().height));
		}
		
		/**
		 * For trace i, create trace information. 
		 * @param sortedSet Index of trace
		 */
		public PGraphInfoPanelImp(Integer sortedSet, PTrace ptrace) {
			initLayout();
			
			// Add selection listener
//			JComboBox combo = SlickerFactory.instance().createComboBox(sortedSet.toArray());
//			combo.setPreferredSize(new Dimension(200, 10));
//			combo.setMinimumSize(new Dimension(50, 10));
//			combo.setMaximumSize(new Dimension(200, 10));
//			combo.addItemListener(this);
			
			Object[] columnNames = new Object[] { "Property", "Value" };
			tableModel = new DefaultTableModel(new Object[][]{
					{"Trace : ", sortedSet}, 
					{"Trace name", XConceptExtension.instance().extractName(ptrace.getTrace())}}, columnNames);
			
			ProMTable promTable = new ProMTable(tableModel);
			promTable.setMinimumSize(new Dimension(200, 100));
			promTable.setMaximumSize(new Dimension(600, 1000));
			promTable.setPreferredSize(new Dimension(400, 300));

			this.add(promTable);
		}


		private void initLayout() {
//			setMinimumSize(new Dimension(200, 120));
			setMaximumSize(new Dimension(600, 1020));
//			setPreferredSize(new Dimension(600, 220));
//			add(Box.createHorizontalStrut(5), BorderLayout.WEST);
//			add(Box.createHorizontalStrut(5), BorderLayout.EAST);
			
		}

		public void updateInfo(POEdge edge) {
			if(edge.getRelation() != null && edge.getRelation() instanceof PDependencyDataAware){
				PDependencyDataAware relation = (PDependencyDataAware) edge.getRelation();
				/*
				 * XX: get the PODependency relation of current selected trace
				 * this is correct because in the PartialOrderILPLinearAlg.addPOReplayResults() method
				 * we compared the linear traces instead of partially ordered traces. 
				 * Thus, the index of event of po-alignment remain the same. 
				 */
				int s = relation.getSource();
				int p = relation.getTarget();
				PDependency curRelation = data.getPoLog().get(currentTrace).getDependency(p,s);
				
				if(curRelation != null && curRelation instanceof PDependencyDataAware){
				
						List<Object[]> infoSingleEdge = new ArrayList<Object[]>();
						for( Object[] values : ((PDependencyDataAware)curRelation).getDataDependencyValues()){
							EnumDataDependency d = (EnumDataDependency) values[3];
							
							Object[] entry = new Object[]{values[0], 
									VisUtil.getStringOfSourceAttribute(d, (XAttribute) values[1]), 
									VisUtil.getStringOfTargetAttribute(d, (XAttribute) values[2]), 
									values[3]};
							infoSingleEdge.add(entry);
							
						}
						Object[] columnNames = new Object[] { "Attribute key", "Source value", "Target value", "Dep" };
						tableModel.setDataVector(infoSingleEdge.toArray(new Object[0][]), columnNames);
						tableModel.fireTableStructureChanged();
					
				}
			}
			
		}

		public void updateInfo(POEventNode node) {
			
				XEvent e = node.getEvent();
				List<Object[]> infoSingleNode = new ArrayList<Object[]>();
				if(e != null){
					//Object[][] infoSingleNode = new Object[e.getAttributes().size()+1][2];
					infoSingleNode.add(new Object[]{"Trace index ", node.getTrace()} );
					infoSingleNode.add(new Object[]{"Event index ", node.getEventIndex()} );
					
					for( Entry<String, XAttribute> entry : e.getAttributes().entrySet()){
						Object[] values = new Object[]{entry.getKey(), VisUtil.getStringOfAttribute(entry.getValue())};
						if(entry.getKey().equals(XConceptExtension.KEY_NAME)){
							infoSingleNode.add(0, values);
						} else {
							infoSingleNode.add(values);
						}					
					}			
				}
			
			
			Object[] columnNames = new Object[] { "Property", "Value" };
			tableModel.setDataVector(infoSingleNode.toArray(new Object[0][]), columnNames);
			tableModel.fireTableStructureChanged();
			
		}
		
		public void updateInfo(PONodeMove node){
			List<Object[]> infoSingleNode = new ArrayList<Object[]>();
			infoSingleNode.add(new Object[]{"Trace index ", currentTrace} );
			infoSingleNode.add(new Object[]{"Step nr.", node.getStepIndex()});
			infoSingleNode.add(new Object[]{"Step type", node.toStringType()});
			if(node instanceof POModelMoveVisibleNode || node instanceof POSyncMoveNode) {
				Transition t = node.getTransition();
				if(t != null){
		//			int size = t.getVisiblePredecessors().size() + t.getVisibleSuccessors().size() + 1;
		//			Object[][] infoSingleNode = new Object[size][2];
					
					infoSingleNode.add(new Object[]{"Transition", t.getLabel() == null ? "Tow" : t.getLabel()});
					infoSingleNode.add(new Object[]{"Invisible", String.valueOf(t.isInvisible())});
					
					for( Transition pre : GraphUtil.getPredessors(t.getGraph(), t)){
						Object[] values = new Object[]{"Predecessor", pre.getLabel()};
						infoSingleNode.add(values);				
					}
					for( Transition suc : GraphUtil.getSuccessors(t.getGraph(), t)){
						Object[] values = new Object[]{"Successor", suc.getLabel()};
						infoSingleNode.add(values);				
					}
					
				}
			}
			if(node instanceof POLogMoveNode || node instanceof POSyncMoveNode){
				
				XEvent e = data.getLog().get(currentTrace).get(node.getEventIndex());
				if(e != null){
					//Object[][] infoSingleNode = new Object[e.getAttributes().size()+1][2];
					
					infoSingleNode.add(new Object[]{"Event index ", node.getEventIndex()} );
					
					for( Entry<String, XAttribute> entry : e.getAttributes().entrySet()){
						Object[] values = new Object[]{entry.getKey(), VisUtil.getStringOfAttribute(entry.getValue())};
						if(entry.getKey().equals(XConceptExtension.KEY_NAME)){
							infoSingleNode.add(0, values);
						} else {
							infoSingleNode.add(values);
						}					
					}		
				}
			}
			
			Object[] columnNames = new Object[] { "Property", "Value" };
			tableModel.setDataVector(infoSingleNode.toArray(new Object[0][]), columnNames);
			tableModel.fireTableStructureChanged();
		}

		// TODO update graph panel and info panel
		@SuppressWarnings("rawtypes")
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				JComboBox localCombo = (JComboBox) e.getSource();
				currentTrace = (Integer) localCombo.getSelectedItem();
				//updateAlignmentGraphs();
			}
			
		}
		
//		public void updateInfo(PartialOrderAlignNode node) {
//			Transition t = node.getTransition();
//			if(t != null){
//				int size = t.getVisiblePredecessors().size() + t.getVisibleSuccessors().size() + 1;
//				Object[][] infoSingleNode = new Object[size][2];
//				
//				infoSingleNode[0] = new Object[]{"Transition ", t.getLabel()};
//				int i = 1;
//				for( Transition pre : t.getVisiblePredecessors()){
//					Object[] values = new Object[]{"Predecessor", pre.getLabel()};
//					infoSingleNode[i++] = values;				
//				}
//				for( Transition suc : t.getVisibleSuccessors()){
//					Object[] values = new Object[]{"Successor", suc.getLabel()};
//					infoSingleNode[i++] = values;				
//				}
//				Object[] columnNames = new Object[] { "Property", "Value" };
//				tableModel.setDataVector(infoSingleNode, columnNames);
//				tableModel.fireTableStructureChanged();
//			}
//			
//		}
		

}
