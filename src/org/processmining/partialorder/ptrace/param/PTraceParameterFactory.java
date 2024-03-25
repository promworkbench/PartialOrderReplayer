package org.processmining.partialorder.ptrace.param;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.processmining.contexts.uitopia.UIPluginContext;

public class PTraceParameterFactory {

	public static PTraceParameter getParameterViaGui(UIPluginContext context) {
		PTraceParameter param = new PTraceParameter();
		return updateParameterViaGui(context, param);
	}
	
	public static PTraceParameter updateParameterViaGui(UIPluginContext context, PTraceParameter param) {
		PTraceParameterDialog dialog = new PTraceParameterDialog(context, param);
	    InteractionResult result = context.showWizard("Configure PTrace Builder Type", true, true, dialog);
	    if (result == InteractionResult.FINISHED) {
	    	return param;
	    } else if (result == InteractionResult.CANCEL) {
	    	return null;
	    }
		return param;
	}

	public static PTraceParameter getParameter() {
		return new PTraceParameter();
	}

}
