package org.processmining.partialorder.ptrace.plugins.vis;

import javax.swing.JComponent;

import org.processmining.connection.PAlignmentResultConnection;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.partialorder.ptrace.model.PLog;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;

public class PTracesVisualizer {

	@Plugin(name = "Visualize P-Traces as Graphs", returnLabels = { "Visualized PTraces" }, returnTypes = {
			JComponent.class }, parameterLabels = { "PO Traces" }, userAccessible = true)
	@Visualizer
	@PluginVariant(requiredParameterLabels = { 0 })
	public JComponent visualizeAsGraph(PluginContext context, PNRepResult logReplayResult) {
		PLog poLog = null;
		try {
			PAlignmentResultConnection conn = context.getConnectionManager()
					.getFirstConnection(PAlignmentResultConnection.class, context, logReplayResult);

			poLog = conn.getObjectWithRole(PAlignmentResultConnection.PARTIALLOG);
		} catch (Exception exc) {
			context.log("No net can be found for this log replay result");
			return null;
		}

		return new PTracesMainVisPanel(context, poLog);
	}

	@Plugin(name = "Visualize P-Traces as Graphs", returnLabels = { "Visualized PTraces" }, returnTypes = {
			JComponent.class }, parameterLabels = { "PO Traces" }, userAccessible = true)
	@Visualizer
	@PluginVariant(requiredParameterLabels = { 0 })
	public JComponent visualizeAsGraph(PluginContext context, PLog poLog) {
		return new PTracesMainVisPanel(context, poLog);
	}

}
