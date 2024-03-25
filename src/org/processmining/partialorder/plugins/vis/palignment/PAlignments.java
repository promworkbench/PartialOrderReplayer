package org.processmining.partialorder.plugins.vis.palignment;

import java.util.List;
import java.util.Map;

import org.processmining.partialorder.models.graph.PartialOrderGraph;



public interface PAlignments  {
	
	Map<Integer, List<PartialOrderGraph>> getGraphs();

}
