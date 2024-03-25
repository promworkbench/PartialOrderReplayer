package org.processmining.partialorder.plugins.vis.palignment;

import java.util.List;
import java.util.Map;

import org.processmining.partialorder.models.graph.PartialOrderGraph;

public class PAlignmentsImpl implements PAlignments {

	private Map<Integer, List<PartialOrderGraph>> graphs;
	
	public PAlignmentsImpl(Map<Integer, List<PartialOrderGraph>> results) {
		graphs = results;
	}

	public Map<Integer, List<PartialOrderGraph>> getGraphs() {
		return graphs;
	}
	
	

}
