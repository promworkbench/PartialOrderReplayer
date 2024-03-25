package org.processmining.partialorder.ptrace.plugins.builder;

import javax.swing.JOptionPane;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.connection.PAlignmentResultConnection;
import org.processmining.connection.PLogConnection;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.extension.XPartialOrderExtension;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.partialorder.ptrace.model.PLog;
import org.processmining.partialorder.ptrace.param.PTraceParameter;
import org.processmining.partialorder.ptrace.plugins.builder.alg.PTraceBuilderAlg;
import org.processmining.partialorder.ptrace.plugins.builder.alg.PTraceDataAwareBuilderImp;
import org.processmining.partialorder.ptrace.plugins.builder.alg.PTraceExtensionBuilderImp;
import org.processmining.partialorder.ptrace.plugins.builder.alg.PTraceSameDayBuilderImp;
import org.processmining.partialorder.ptrace.plugins.builder.alg.PTraceSameTimeBuilderImp;
import org.processmining.partialorder.ptrace.plugins.builder.alg.PTraceSequentialBuilderImp;

public class PTraceConstructionFactory {

	/**
	 * This function executes the workflow to (re)construct a PLog from the
	 * input log
	 * @return result[0] = partially ordered log; result[1] = original sequential log
	 */
	public static PLog callContructPLogWorkflow(UIPluginContext context, XLog log, PTraceParameter parameter) {
		//XIdentityUtil.addIdentifiersToTracesAndEvents(log);
		PLog pLog = null;
		boolean isParialOrderExtended = XPartialOrderExtension.instance().isPartiallyOrderedLog(log);
		int n = 1;
		if (isParialOrderExtended) {
			Object[] options = { "Yes, please", "No, thanks"};
			n  =JOptionPane.showOptionDialog(null, "The log already contains partial order info, recompute Partially ordered trace?",
					"Recompute p-traces", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
					options[1]);
		}
		//		 FIXME: give option to reuse or compute new ptraces
		if (!isParialOrderExtended || n == 0) {
			pLog = (new PLogPlugin()).computePTracesViaGui(context, log, parameter);
//			pLog = (PLog) results[0];
//			log = (XLog) results[1];
		} else {
			pLog = tryToGetPLogViaConnection(context, log);
			if (pLog == null) {
				pLog = XLogWithParOExtensionToPLogConverter.convertLog(log, parameter.isComputeTransReduction());
			}
		}
		return pLog;
	}



	private static PLog tryToGetPLogViaConnection(UIPluginContext context, XLog log) {
		PLog poLog = null;
		try {
			PLogConnection conn = context.getConnectionManager().getFirstConnection(PAlignmentResultConnection.class,
					context, log);

			poLog = conn.getObjectWithRole(PAlignmentResultConnection.PARTIALLOG);
		} catch (ConnectionCannotBeObtained e) {
			context.log("No plog can be found for this log replay result");
			return poLog;
		}
		return poLog;
	}


	

	public static PTraceBuilderAlg getPTraceBuilder(int i, XTrace t, PTraceParameter param) {
		if(param.isComputingDependenciesBaseOnExtension()){
			return new PTraceExtensionBuilderImp(i, t, param);
		}
		if (param.isComputingSequentialTraces()) {
			return new PTraceSequentialBuilderImp(i, t, param);
		
		} else if (param.isComputingSameDayEqual()){ 
			return new PTraceSameDayBuilderImp(i, t, param);
			
		} else if (param.isComputingTimeEqual()){ 
			return new PTraceSameTimeBuilderImp(i, t, param);
			
		} else {
			return new PTraceDataAwareBuilderImp(i, t, param);
		}
	}


}
