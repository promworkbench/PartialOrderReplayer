package org.processmining.partialorder.dal.param;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.partialorder.dal.dialog.DALConversionParameterDialog;

public class DALParameterFactory {
	public static DALConversionParameters getParameterViaGui(UIPluginContext context) {
		DALConversionParameters param = new DALConversionParameters();
		DALConversionParameterDialog dialog = new DALConversionParameterDialog(context, param);
	    InteractionResult result = context.showWizard("Configure XES to DAL conversion", true, true, dialog);
	    if (result == InteractionResult.FINISHED) {
	    	return param;
	    }	    
		return param;
	}

	public static DALConversionParameters getParameter() {
		return new DALConversionParameters();
	}
}
