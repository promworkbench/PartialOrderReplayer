package org.processmining.partialorder.plugins.vis.projection;

import info.clearthought.layout.TableLayout;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.AttributeMap.ArrowType;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.jgraph.ProMJGraph;
import org.processmining.models.jgraph.elements.ProMGraphCell;
import org.processmining.partialorder.dal.models.EnumDataAccessType;
import org.processmining.partialorder.models.projection.DataElementNode;
import org.processmining.partialorder.models.projection.DataPattern;
import org.processmining.partialorder.models.projection.LogMovePattern;
import org.processmining.partialorder.models.projection.LogMoveTransition;
import org.processmining.partialorder.models.projection.POAlignmentOnModelDataProvider;
import org.processmining.partialorder.models.projection.POProjectedPetrinetGraph;
import org.processmining.partialorder.util.VisUtil;

public class PAlignmentsOnModelVisPanel extends JComponent implements GraphSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4457882322034117320L;
	
	POAlignmentOnModelDataProvider data;
	ProMJGraph jprojection;
	POProjectedPetrinetGraph projection;

	private PProjectionConfigPanel configPanel;
	
	public static final int CONFIG_PANEL_WIDTH = 300;

	public PAlignmentsOnModelVisPanel(PluginContext context, POAlignmentOnModelDataProvider data) {
		this.data = data;
		
		/* init main layout */
		TableLayout mainLayout = new TableLayout(new double[][] { { TableLayout.FILL, CONFIG_PANEL_WIDTH }, { TableLayout.FILL } });
		setLayout(mainLayout);
		setBorder(BorderFactory.createEmptyBorder());
		
		/*
		 * Clone Original model
		 */
		projection = getProjectedModel();
//		
//		POReplayProjectedVisPanel comp = new POReplayProjectedVisPanel(context, data.getGraph(), data.getInitialMarking(), data.getLog(),
//				data.getMapping(), data.getLogReplayResult());
		jprojection = VisUtil.getProMSimpleJGraph(projection, VisUtil.LightBackgroundColor);
		jprojection.addGraphSelectionListener(this);
		
		
		this.configPanel = new PProjectionConfigPanel(this, data);
//		
		this.add(VisUtil.getProMStyleScrollPane(jprojection),  "0,0");
		this.add(configPanel,  "1,0");
	}
	
	public void updateProjection(){
		jprojection.getModel().beginUpdate();
		// 
		for(LogMoveTransition t : projection.getLogMoveTransitions()){

			boolean visible =  data.isLogMoveEClassSelected(t.getLabel());
			t.setGraphVisVisible(visible);
				
			for( PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge:projection.getInEdges(t)){
				
				setGraphVisVisible(edge, visible);
			}
			for( PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge:projection.getOutEdges(t)){
				setGraphVisVisible(edge,  visible);
			}
		}
		
		for(DataElementNode d: projection.getDataNodes()){
			boolean visible =  data.isDataElemSelected(d.getLabel());
			d.setGraphVisVisible(visible);
			
			for( PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge:projection.getInEdges(d)){
				boolean edgevisible = visible; 
				if(edge.getSource() instanceof LogMoveTransition){
					edgevisible = edgevisible &&  data.isLogMoveEClassSelected(edge.getSource().getLabel());
				}
					
				setDataArcVisible(edge, edgevisible);
			}
			for( PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge:projection.getOutEdges(d)){
				boolean edgevisible = visible; 
				if(edge.getTarget() instanceof LogMoveTransition){
					edgevisible = edgevisible &&  data.isLogMoveEClassSelected(edge.getTarget().getLabel());
				}
				setDataArcVisible(edge,  edgevisible);
			}
		}
		
		
		jprojection.getModel().endUpdate();
		jprojection.refresh();
		jprojection.revalidate();
		jprojection.repaint();
	}



	

	@SuppressWarnings("rawtypes")
	private void setDataArcVisible(PetrinetEdge edge, boolean visible) {
		if(visible){
			edge.getAttributeMap().put(AttributeMap.EDGEEND, ArrowType.ARROWTYPE_SIMPLE);
			edge.getAttributeMap().put(AttributeMap.EDGECOLOR, new Color(169,169,169));
			edge.getAttributeMap().put(AttributeMap.DASHPATTERN, new float[] {2, 2*2});
		} else {
			edge.getAttributeMap().put(AttributeMap.SHOWLABEL, false);
			edge.getAttributeMap().put(AttributeMap.EDGECOLOR, new Color(0, 0, 0, 0));
			edge.getAttributeMap().put(AttributeMap.FILLCOLOR, new Color(0, 0, 0, 0));
		}	
		
	}

	@SuppressWarnings("rawtypes")
	private void setGraphVisVisible(PetrinetEdge edge, boolean visible) {
		if(visible){
			edge.getAttributeMap().put(AttributeMap.SHOWLABEL, true);
			edge.getAttributeMap().put(AttributeMap.EDGECOLOR, Color.RED);
		} else {
			edge.getAttributeMap().put(AttributeMap.SHOWLABEL, false);
			edge.getAttributeMap().put(AttributeMap.EDGECOLOR, new Color(0, 0, 0, 0));
			edge.getAttributeMap().put(AttributeMap.FILLCOLOR, new Color(0, 0, 0, 0));
		}		
	}

	private POProjectedPetrinetGraph getProjectedModel() {
		PetrinetGraph graph = data.getGraph();		
		Map<PetrinetNode, PetrinetNode> mapOrigToClone = new HashMap<PetrinetNode, PetrinetNode>();

		POProjectedPetrinetGraph projection = new POProjectedPetrinetGraph(graph.getLabel());
		// Add model places
		for(Place p : graph.getPlaces()){
			Place np = projection.addPlace(p.getLabel());
			np.getAttributeMap().put(AttributeMap.FILLCOLOR, Color.WHITE);
			mapOrigToClone.put(p, np);
		}
		
		// Add original model transitions
		for(Transition t : graph.getTransitions()){
			Transition nt = projection.addTransition(t.getLabel());
			nt.getAttributeMap().put(AttributeMap.FILLCOLOR, Color.WHITE);
			nt.getAttributeMap().put(AttributeMap.SHOWLABEL, true);
			mapOrigToClone.put(t, nt);
		}
		// Add original arcs
		for( PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> e : graph.getEdges()){
			Arc a = null;
			if(e.getSource() instanceof Place){
				a = projection.addArc((Place)mapOrigToClone.get(e.getSource()), (Transition)mapOrigToClone.get(e.getTarget()));
			} else {
				a = projection.addArc((Transition)mapOrigToClone.get(e.getSource()), (Place)mapOrigToClone.get(e.getTarget()));
			}			
			a.getAttributeMap().put(AttributeMap.EDGECOLOR, Color.LIGHT_GRAY);
		}
		// Annotate the petrinet with data elements
		Map<String, DataElementNode> mapKeyToDataNode = new HashMap<String, DataElementNode>();
		for( String datakey : data.getDataElements()){
			DataElementNode dnode = projection.addDataElemNode(datakey);
			mapKeyToDataNode.put(datakey, dnode);
			Map<Transition, DataPattern> t2dpattern = data.getInputTransOfData(datakey);
			for(Transition inT : t2dpattern.keySet()){
				projection.AddDataAccessArc(dnode, (Transition)mapOrigToClone.get(inT));
			}
			t2dpattern = data.getOutputTransOfData(datakey);
			for(Transition outT: t2dpattern.keySet()){
				projection.AddDataAccessArc((Transition)mapOrigToClone.get(outT), dnode);
			}
		}
		// Annotate the petrinet with log moves
		for( LogMovePattern lm: data.getLogMovePatterns()){
			LogMoveTransition t = projection.addLogMoveTransition(lm.getLogMoveClassString());
			for(Transition pre : lm.getPreset()){
				projection.addLogMoveArc((Transition)mapOrigToClone.get(pre), t);
			}
			for(Transition post : lm.getPostset()){
				projection.addLogMoveArc(t, (Transition)mapOrigToClone.get(post));
			}
			for(DataPattern dp : lm.getDataPatterns()){
				if(dp.getType().equals(EnumDataAccessType.I)){
					projection.AddDataAccessArc(mapKeyToDataNode.get(dp.getDataKey()),t);
				} else {
					projection.AddDataAccessArc(t, mapKeyToDataNode.get(dp.getDataKey()));
				}
			}
			data.putLogMoveTransToPattern(t, lm);
		}
		
		
		return projection;
	}

	public void valueChanged(GraphSelectionEvent evt) {
		for (Object cell : evt.getCells()) {
			if (!evt.isAddedCell(cell)) {
				continue;
			}
			if(cell instanceof ProMGraphCell && ((ProMGraphCell) cell).getNode() instanceof LogMoveTransition){
				LogMoveTransition t = (LogMoveTransition) ((ProMGraphCell) cell).getNode();
				configPanel.updateInfo(data.getPattern(t));
			}
			
			if(cell instanceof ProMGraphCell && ((ProMGraphCell) cell).getNode() instanceof DataElementNode){
				DataElementNode t = (DataElementNode) ((ProMGraphCell) cell).getNode();
				configPanel.updateInfo(t.getLabel(), data.getDataPatternsOfKey(t.getLabel()));
			}
		}
		
	}


}
