package org.processmining.partialorder.ptrace.plugins.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.deckfour.xes.extension.std.XIdentityExtension;
import org.deckfour.xes.id.XID;
import org.deckfour.xes.id.XIDFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginCategory;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.partialorder.ptrace.model.PLog;
import org.processmining.partialorder.ptrace.model.PTrace;
import org.processmining.partialorder.ptrace.model.imp.PLogImp;
import org.processmining.partialorder.ptrace.param.PTraceParameter;
import org.processmining.partialorder.ptrace.param.PTraceParameterFactory;
import org.processmining.partialorder.ptrace.plugins.builder.alg.PTraceBuilderAlg;

@Plugin(
		name = "Compute PTraces",
		returnLabels = { "PLog" },
		returnTypes = { PLog.class },
		parameterLabels = { "Event Log", "PTrace Builder", "Petri net", "Parameters" },
		categories = { PluginCategory.Analytics },
		keywords = { "Partial order", "Partially Ordered traces", "Log preprocessing", "Log conversion" },
		help = "This plugin converts the sequential traces in the given log into partially ordered traces based on timestamps or data dependencies. </br></br>"
				+ "Using the data dependencies to build partial orders requires a data annotated log. Please use the plugin \"Convert to Data Anotated Log\" to convert.",
		userAccessible = true)
public class PLogPlugin {

	/**
	 * ProM plugin interfaces
	 */
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Xixi Lu", email = "x.lu@tue.nl",
			pack = "PartialOrderReplayer")
	@PluginVariant(variantLabel = "", requiredParameterLabels = { 0 })
	public PLog computePTracesViaGui(UIPluginContext context, XLog log) {
		PTraceParameter param = PTraceParameterFactory.getParameterViaGui(context);
		return computePTraces(context, log, param);
	}

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Xixi Lu", email = "x.lu@tue.nl",
			pack = "PartialOrderReplayer")
	@PluginVariant(variantLabel = "", requiredParameterLabels = { 0 })
	public PLog computePTraces(PluginContext context, XLog log) {
		return computePTraces(context, log, PTraceParameterFactory.getParameter());
	}

	public PLog computePTracesViaGui(UIPluginContext context, XLog log, PTraceParameter parameter) {
		parameter = PTraceParameterFactory.updateParameterViaGui(context, parameter);
		return computePTraces(context, log, parameter);
	}

	public static PLog computePTraces(XLog origLog, PTraceParameter param){
		PLogPlugin plugin = new PLogPlugin();
		return plugin.computePTraces(null, origLog, param);
	}
	
	public static PLog computeSeqPTraces(XLog origLog){
		PLogPlugin plugin = new PLogPlugin();
		return plugin.computePTraces(null, origLog, PTraceParameterFactory.getParameter());
	}
	
	public PLog computePTraces(PluginContext context, XLog origLog, PTraceParameter param) {
		if (context != null) {
			context.getProgress().setMaximum(origLog.size());
		}
		if (param == null) { // Cancelled
			return null;
		}

		XLog log = updateEventsXID(origLog);
		//		XLog log = origLog;

		// Create Partially ordered log
		PLog plog = new PLogImp(log);

		final ExecutorService service = Executors.newFixedThreadPool(2);
		final List<Future<PTrace>> result = new ArrayList<Future<PTrace>>(log.size()); // executor completion service

		for (int i = 0; i < log.size(); i++) {
			if (context != null) {
				context.getProgress().inc();
			}
			final XTrace t = log.get(i);
			final PTraceBuilderAlg builder = PTraceConstructionFactory.getPTraceBuilder(i, t, param);

			result.add(service.submit(builder));
		}

		service.shutdown();
		//		service.isTerminated()
		try {
			while (!service.awaitTermination(60, TimeUnit.SECONDS)) {
				if (context != null && context.getProgress().isCancelled()) {
					service.shutdownNow();
					return null;
				}
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		for (int i = 0; i < log.size(); i++) {
			try {
				Future<PTrace> future = result.get(i);
				PTrace ptrace = future.get();
				//				context.getExecutor();
				plog.add(i, ptrace);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}

		return plog;
	}

	private XLog updateEventsXID(XLog origLog) {
		XLog log = (XLog) origLog.clone();
		for (XTrace t : log) {
			for (XEvent e : t) {
				XID id = XIdentityExtension.instance().extractID(e);
				if (id == null) {
					id = XIDFactory.instance().createId();
					XIdentityExtension.instance().assignID(e, id);
				}
			}
		}
		return log;
	}

}
