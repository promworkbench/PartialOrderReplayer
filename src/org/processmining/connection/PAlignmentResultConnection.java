package org.processmining.connection;

import org.deckfour.xes.model.XLog;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.partialorder.ptrace.model.PLog;
import org.processmining.partialorder.ptrace.param.PTraceParameter;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;

public class PAlignmentResultConnection extends PLogConnection {
	

	public final static String REPLAYRESULT = "ReplayResult";
	public final static String PN = "Petrinet";
	public final static String MAPPING = "Mapping";
	public final static String ALGPARAMETER = "Param";

	public PAlignmentResultConnection(XLog log, PLog poTraces, PNRepResult pnResult, PetrinetGraph graph,
			TransEvClassMapping mapping, CostBasedCompleteParam parameters, PTraceParameter poparameters) {
		super("PAlignments of Log", log, poTraces, poparameters);
		put(REPLAYRESULT, pnResult);
		put(PN, graph);
		put(MAPPING, mapping);
		put(ALGPARAMETER, parameters);
	}





}
