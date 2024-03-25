package org.processmining.partialorder.plugins.vis;

import javax.swing.JComponent;

import org.deckfour.xes.model.XLog;
import org.processmining.connection.PAlignmentResultConnection;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.partialorder.models.projection.POAlignmentOnModelDataProvider;
import org.processmining.partialorder.models.replay.POAlignmentDataProvider;
import org.processmining.partialorder.plugins.vis.palignment.PAlignmentMainVisPanel;
import org.processmining.partialorder.plugins.vis.palignment.PAlignments;
import org.processmining.partialorder.plugins.vis.palignment.PAlignmentsUnfoldingVisPanel;
import org.processmining.partialorder.plugins.vis.projection.PAlignmentsOnModelVisPanel;
import org.processmining.partialorder.ptrace.model.PLog;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;

@Visualizer
public class PAlignmentVisualizer {

	@Visualizer
	@PluginVariant(requiredParameterLabels = { 0 })
	@Plugin(name = "Visualize P-Alignments as Graphs", 
		returnLabels = { "Visualized PO Alignment graphs" }, 
		returnTypes = { JComponent.class }, 
		parameterLabels = { "PO Traces" }, userAccessible = true)
	public JComponent visualize(PluginContext context, PNRepResult logReplayResult) {
		PLog poLog = null;
		XLog log = null;
		PetrinetGraph graph = null;
		TransEvClassMapping mapping = null;
		CostBasedCompleteParam param = null;
		try {
			PAlignmentResultConnection conn = context.getConnectionManager().getFirstConnection(
					PAlignmentResultConnection.class, context, logReplayResult);

			poLog = conn.getObjectWithRole(PAlignmentResultConnection.PARTIALLOG);
			log = conn.getObjectWithRole(PAlignmentResultConnection.LINEARLOG);
			graph = conn.getObjectWithRole(PAlignmentResultConnection.PN);
			mapping = conn.getObjectWithRole(PAlignmentResultConnection.MAPPING);
			param = conn.getObjectWithRole(PAlignmentResultConnection.ALGPARAMETER);

		} catch (Exception exc) {
			context.log("No net can be found for this log replay result");
			return null;
		}

		POAlignmentDataProvider data = new POAlignmentDataProvider(poLog, poLog.getXLog(), logReplayResult, graph,
				mapping, param);

		PAlignmentMainVisPanel panel = new PAlignmentMainVisPanel(context, data);

		return panel;
		//POTraceGraphConversion conv = new POTraceGraphConversion();
		//return ProMJGraphVisualizer.instance().visualizeGraph(context,conv.convert(poLog.get(0)));
	}

	@Visualizer
	@PluginVariant(requiredParameterLabels = { 0 })
	@Plugin(name = "Visualize PAlignments Projected on Model", returnLabels = { "Visualized PO Alignment On Model" }, returnTypes = { JComponent.class }, parameterLabels = { "PO Traces" }, userAccessible = false)
	public JComponent visualizeOnModel(PluginContext context, PNRepResult logReplayResult) {
		PLog poLog = null;
		XLog log = null;
		PetrinetGraph graph = null;
		TransEvClassMapping mapping = null;
		CostBasedCompleteParam param = null;

		try {
			PAlignmentResultConnection conn = context.getConnectionManager().getFirstConnection(
					PAlignmentResultConnection.class, context, logReplayResult);

			poLog = conn.getObjectWithRole(PAlignmentResultConnection.PARTIALLOG);
			log = conn.getObjectWithRole(PAlignmentResultConnection.LINEARLOG);
			graph = conn.getObjectWithRole(PAlignmentResultConnection.PN);
			mapping = conn.getObjectWithRole(PAlignmentResultConnection.MAPPING);
			param = conn.getObjectWithRole(PAlignmentResultConnection.ALGPARAMETER);

			//			POAlignmentsConnection conn2 = context.getConnectionManager().getFirstConnection(
			//					POAlignmentsConnection.class, context, logReplayResult);
			//			poalignments = conn2.getObjectWithRole(POAlignmentsConnection.PO);

		} catch (Exception exc) {
			context.log("No net can be found for this log replay result");
			return null;
		}

		POAlignmentOnModelDataProvider data = new POAlignmentOnModelDataProvider(poLog, log, logReplayResult, graph,
				mapping, param);
		PAlignmentsOnModelVisPanel panel = new PAlignmentsOnModelVisPanel(context, data);

		return panel;

		//POTraceGraphConversion conv = new POTraceGraphConversion();
		//return ProMJGraphVisualizer.instance().visualizeGraph(context,conv.convert(poLog.get(0)));
	}

	@Visualizer
	@PluginVariant(requiredParameterLabels = { 0 })
	@Plugin(name = "Visualize P-Alignments as Graphs", returnLabels = { "Visualized PO Alignment graphs" }, returnTypes = { JComponent.class }, parameterLabels = { "PO Traces" }, userAccessible = true)
	public JComponent visualize(PluginContext context, PAlignments logReplayResult) {
//		PLog poLog = null;
//		XLog log = null;
//		PetrinetGraph graph = null;
//		TransEvClassMapping mapping = null;
//		CostBasedCompleteParam param = null;
//		try {
//			PAlignmentsResultConnection conn = context.getConnectionManager().getFirstConnection(
//					PAlignmentsResultConnection.class, context, logReplayResult);
//
//			poLog = conn.getObjectWithRole(PAlignmentResultConnection.PARTIALLOG);
//			log = conn.getObjectWithRole(PAlignmentResultConnection.LINEARLOG);
//			graph = conn.getObjectWithRole(PAlignmentResultConnection.PN);
//			mapping = conn.getObjectWithRole(PAlignmentResultConnection.MAPPING);
//			param = conn.getObjectWithRole(PAlignmentResultConnection.ALGPARAMETER);
//
//		} catch (Exception exc) {
//			context.log("No net can be found for this log replay result");
//			return null;
//		}

//		POAlignmentDataProvider data = new POAlignmentDataProvider(poLog, poLog.getXLog(), logReplayResult, graph,
//				mapping, param);

		PAlignmentsUnfoldingVisPanel panel = new PAlignmentsUnfoldingVisPanel(context, logReplayResult);

		return panel;
		//POTraceGraphConversion conv = new POTraceGraphConversion();
		//return ProMJGraphVisualizer.instance().visualizeGraph(context,conv.convert(poLog.get(0)));
	}

}