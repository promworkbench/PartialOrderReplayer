package org.processmining.partialorder.models.graph.node;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.shapes.RoundedRect;
import org.processmining.partialorder.models.graph.PartialOrderGraph;
import org.processmining.partialorder.models.graph.shape.ShapeLogMove;
import org.processmining.partialorder.models.palignment.Move;
import org.processmining.partialorder.plugins.vis.PGraphColorStyle;
import org.processmining.partialorder.util.LogUtil;
import org.processmining.partialorder.util.VisUtil;

/**
 * A POLogMoveNode in the partially ordered graph represents a move in the log A
 * POLogMoveNode node is assigned the specific shape {@link ShapeLogMove}
 * 
 * @author xlu
 */
public class POLogMoveNode extends PONodeMove {

	public POLogMoveNode(PartialOrderGraph graph, Move move) {
		super(graph, move);
	}

	protected void initAttributeMap() {
//		getAttributeMap().put(AttributeMap.SHAPE, new ShapeLogMove());
		getAttributeMap().put(AttributeMap.SHAPE, new RoundedRect());
		getAttributeMap().put(AttributeMap.SIZE, new Dimension(50, 50));
		getAttributeMap().put(AttributeMap.BORDERWIDTH, 2);

		getAttributeMap().put(AttributeMap.LABEL, LogUtil.getName(getEvent()));
		getAttributeMap().put(AttributeMap.SHOWLABEL, true);
		setColor(PGraphColorStyle.COLOR_LOG_RELATION);
	}

	public String toStringType() {
		return "Log move";
	}

	public Object[][] getInfo() {
		List<Object[]> infoSingleNode = new ArrayList<Object[]>();
		infoSingleNode.add(new Object[]{"Trace index ", getTrace()} );
		infoSingleNode.add(new Object[]{"Step nr.", getStepIndex()});
		infoSingleNode.add(new Object[]{"Step type", toStringType()});
		XEvent e = getEvent();
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
