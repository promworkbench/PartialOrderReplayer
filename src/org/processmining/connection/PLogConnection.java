package org.processmining.connection;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.partialorder.ptrace.model.PLog;
import org.processmining.partialorder.ptrace.param.PTraceParameter;

public class PLogConnection extends AbstractConnection {
	
	public final static String LINEARLOG = "LinearLog";
	public final static String PARTIALLOG = "PartialLog";
	public final static String POPARAMETER = "PoParam";
	
	public PLogConnection(XLog log, PLog poTraces, PTraceParameter poparameters) {
		this("PTraces of Linear Log", log, poTraces, poparameters);
	}
	
	public PLogConnection(String label, XLog log, PLog poTraces, PTraceParameter poparameters) {
		super(label);
		put(LINEARLOG, log);
		put(PARTIALLOG, poTraces);
		put(POPARAMETER, poparameters);
	}

}
