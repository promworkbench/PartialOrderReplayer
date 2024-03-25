package org.processmining.partialorder.models.graph.node;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.shapes.RoundedRect;
import org.processmining.partialorder.models.graph.PartialOrderGraph;
import org.processmining.partialorder.models.graph.shape.ShapeSyncMove;
import org.processmining.partialorder.models.palignment.Move;
import org.processmining.partialorder.plugins.vis.PGraphColorStyle;
import org.processmining.partialorder.util.GraphUtil;
import org.processmining.partialorder.util.LogUtil;
import org.processmining.partialorder.util.VisUtil;

/**
 * A POSyncMoveNode in the partially ordered graph represents a synchronized
 * move between the trace and the model A POSyncMoveNode node is assigned the
 * specific shape {@link ShapeSyncMove}
 * 
 * @author xlu
 */
public class POSyncMoveNode extends PONodeMove {

	public POSyncMoveNode(PartialOrderGraph graph, Move move) {
		super(graph, move);
		initAttributeMap();
	}

	protected void initAttributeMap() {
//		getAttributeMap().put(AttributeMap.SHAPE, new ShapeSyncMove());
		getAttributeMap().put(AttributeMap.SHAPE, new RoundedRect());
		getAttributeMap().put(AttributeMap.SIZE, new Dimension(50, 50));
		getAttributeMap().put(AttributeMap.BORDERWIDTH, 2);

		getAttributeMap().put(AttributeMap.LABEL, LogUtil.getName(getEvent()));
		getAttributeMap().put(AttributeMap.SHOWLABEL, true);
		setColor(PGraphColorStyle.COLOR_SYNC_RELATION);
	}

	public String toStringType() {
		return "Synchronous move";
	}

	public Object[][] getInfo() {
		List<Object[]> infoSingleNode = new ArrayList<Object[]>();
		infoSingleNode.add(new Object[]{"Trace index ", getTrace()} );
		infoSingleNode.add(new Object[]{"Step nr.", getStepIndex()});
		infoSingleNode.add(new Object[]{"Step type", toStringType()});
			Transition t = getTransition();
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
		
			XEvent e = this.getEvent();
			if(e != null){
				//Object[][] infoSingleNode = new Object[e.getAttributes().size()+1][2];
				
				infoSingleNode.add(new Object[]{"Event index ", getEventIndex()} );
				
				for( Entry<String, XAttribute> entry : e.getAttributes().entrySet()){
					Object[] values = new Object[]{entry.getKey(), VisUtil.getStringOfAttribute(entry.getValue())};
					if(entry.getKey().equals(XConceptExtension.KEY_NAME)){
						infoSingleNode.add(0, values);
					} else {
						infoSingleNode.add(values);
					}					
				}		
			}
		
		return infoSingleNode.toArray(new Object[0][]);
	}

}
