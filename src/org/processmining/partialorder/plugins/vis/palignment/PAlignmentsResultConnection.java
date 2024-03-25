package org.processmining.partialorder.plugins.vis.palignment;

import org.deckfour.xes.model.XLog;
import org.processmining.connection.PLogConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.partialorder.ptrace.model.PLog;
import org.processmining.partialorder.ptrace.param.PTraceParameter;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;

public class PAlignmentsResultConnection extends PLogConnection {
	

	public final static String REPLAYRESULT = "ReplayResult";
	public final static String PN = "Petrinet";
	public final static String MAPPING = "Mapping";
	public final static String ALGPARAMETER = "Param";

	public PAlignmentsResultConnection(XLog log, PLog poTraces, PAlignments pnResult, PetrinetGraph graph,
			TransEvClassMapping mapping, CostBasedCompleteParam parameters, PTraceParameter poparameters) {
		super("PAlignments of Log", log, poTraces, poparameters);
		put(REPLAYRESULT, pnResult);
		put(PN, graph);
		put(MAPPING, mapping);
		put(ALGPARAMETER, parameters);
	}

}
