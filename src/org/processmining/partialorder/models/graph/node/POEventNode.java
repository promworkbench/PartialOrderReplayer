package org.processmining.partialorder.models.graph.node;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.partialorder.models.graph.PONode;
import org.processmining.partialorder.models.graph.PartialOrderGraph;
import org.processmining.partialorder.models.graph.shape.ShapeLogMove;
import org.processmining.partialorder.plugins.vis.PGraphColorStyle;
import org.processmining.partialorder.util.VisUtil;

/**
 * The class extends the {@link PONode} and represents an event in the graph.
 * Only used to visualize partially ordered event net (not in alignment)
 * 
 * @author xlu
 * 
 */
public class POEventNode extends PONode {
	protected int eventIndex;
	protected XEvent event;

	public POEventNode(int trace, PartialOrderGraph parentGraph, int eventIndex, XEvent event) {
		super(parentGraph);
		this.event = event;
		this.eventIndex = eventIndex;
		initAttributeMap();
	}

	protected void initAttributeMap() {
		getAttributeMap().put(AttributeMap.LABEL, XConceptExtension.instance().extractName(event));
		getAttributeMap().put(AttributeMap.BORDERWIDTH, 2);
		getAttributeMap().put(AttributeMap.SHAPE, new ShapeLogMove());
		getAttributeMap().put(AttributeMap.SIZE, new Dimension(50, 50));
		getAttributeMap().put(AttributeMap.FILLCOLOR, PGraphColorStyle.EVENT_NODE_COLOR);
	}

	public int getEventIndex() {
		return eventIndex;
	}

	public void setEventIndex(int eventIndex) {
		this.eventIndex = eventIndex;
	}

	public XEvent getEvent() {
		return event;
	}

	public void setEvent(XEvent event) {
		this.event = event;
	}

	public String toStringType() {
		return "Event";
	}

	public Object[][] getInfo() {
		XEvent e = getEvent();
		List<Object[]> infoSingleNode = new ArrayList<Object[]>();
		if(e != null){
			//Object[][] infoSingleNode = new Object[e.getAttributes().size()+1][2];
			infoSingleNode.add(new Object[]{"Trace index ", getTrace()} );
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
