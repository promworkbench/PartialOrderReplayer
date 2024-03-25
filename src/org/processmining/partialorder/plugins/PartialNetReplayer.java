package org.processmining.partialorder.plugins;

import java.text.NumberFormat;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.processmining.connection.PAlignmentResultConnection;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginCategory;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.connections.petrinets.EvClassLogPetrinetConnection;
import org.processmining.models.connections.petrinets.PNRepResultAllRequiredParamConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.partialorder.dialogs.PartialNetReplayerUI;
import org.processmining.partialorder.plugins.replay.PartialOrderILPLinearAlg;
import org.processmining.partialorder.plugins.replay.StandardPTraceConverter;
import org.processmining.partialorder.ptrace.model.PLog;
import org.processmining.partialorder.ptrace.param.PAlignmentParameter;
import org.processmining.partialorder.ptrace.plugins.builder.PLogPlugin;
import org.processmining.partialorder.ptrace.plugins.builder.PTraceConstructionFactory;
import org.processmining.partialorder.util.PartialUtil;
import org.processmining.plugins.astar.petrinet.PartialOrderBuilder;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNPartialOrderAwareReplayAlgorithm;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;
import org.processmining.plugins.petrinet.replayer.ui.PNReplayerUI;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import nl.tue.astar.AStarException;

@Plugin(name = "Partial Aware Replay", returnLabels = {
		"Petrinet log replay result" }, returnTypes = {
				PNRepResult.class }, parameterLabels = { "Petri net", "Event Log",
						"Mapping", "Replay Algorithm",
						"Parameters" }, help = "This plugin checks conformance (using alignment) between the given log and the given model and is partial aware. "
								+ "Partial aware means that user can select various ways to convert the sequential traces "
								+ "in the log to partially ordered traces that consider certain subsets of events to be "
								+ "concurrent and can be aligned freely independent of their ordering in the log. "
								+ "Further details can be found in the papers. <br/><br/>"
								+ "After getting results, to see the partially ordered alignments, select the visualizer \"Visualize P-Alignments as Graphs\". "
								+ "Or to see the partially ordered traces, select the visualizer \"Visualize P-Traces as Graphs\"<br/><br/>"
								+ "Input sample and user guide can be downloaded at https://svn.win.tue.nl/repos/prom/Documentation/PartialOrderReplayer", categories = {
										PluginCategory.ConformanceChecking,
										PluginCategory.Analytics }, keywords = {
												"Partial Order",
												"Conformance" }, userAccessible = true)
public class PartialNetReplayer {

	/**
	 * GUI variants
	 */
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Xixi Lu", email = "x.lu@tue.nl", pack = "PartialOrderReplayer")
	@PluginVariant(variantLabel = "Using net, log, and standard parameter", requiredParameterLabels = {
			0, 1 })
	public PNRepResult replayLogGUIStandard(final UIPluginContext context, Petrinet net,
			XLog log) throws ConnectionCannotBeObtained, AStarException {
		return replayLogGUI(context, net, log, new PAlignmentParameter());
	}

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Xixi Lu", email = "x.lu@tue.nl", pack = "PartialOrderReplayer")
	@PluginVariant(variantLabel = "From Petri net and Event Log", requiredParameterLabels = {
			0 })
	public PNRepResult replayLog(UIPluginContext context, Petrinet net)
			throws AStarException {
		PAlignmentParameter parameter = new PAlignmentParameter();
		Petrinet object = PetrinetFactory.newPetrinet("Rename ");
		Map<Object, Object> map = new THashMap<>();
		Set<String> transitionLabels = new THashSet<>();
		int i = 0;
		for (Transition t : net.getTransitions()) {
			String label = null;
			if (!transitionLabels.contains(t.getLabel())) {
				label = t.getLabel() + "-" + i++;

			} else {
				label = t.getLabel();
			}
			Transition newTrans = object.addTransition(label);
			newTrans.setInvisible(t.isInvisible());
			transitionLabels.add(label);
			map.put(t, newTrans);
		}
		for (Place p : net.getPlaces()) {
			String label = null;
			if (transitionLabels.contains(p.getLabel())) {
				label = p.getLabel() + "-" + i++;

			} else {
				label = p.getLabel();
			}
			Place newPlace = object.addPlace(label);
			transitionLabels.add(label);
			map.put(p, newPlace);
			
			for( PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> in : net.getInEdges(p)){
				object.addArc((Transition) map.get(in.getSource()), newPlace);
			}
			
			for( PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> out : net.getOutEdges(p)){
				object.addArc(newPlace, (Transition) map.get(out.getTarget()));
			}

		}

		context.getProvidedObjectManager().createProvidedObject("new net", object,
				Petrinet.class, context);
		return null;
	}

	private void newTrans(boolean invisible) {
		// TODO Auto-generated method stub

	}

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Xixi Lu", email = "x.lu@tue.nl", pack = "PartialOrderReplayer")
	@PluginVariant(variantLabel = "Using Net and Log, standard parameter", requiredParameterLabels = {
			0, 1, 4 })
	public PNRepResult replayLogGUIConfigured(final UIPluginContext context, Petrinet net,
			XLog log, PAlignmentParameter parameter)
			throws ConnectionCannotBeObtained, AStarException {
		return replayLogGUI(context, net, log, parameter);
	}

	public PNRepResult replayLogGUI(final UIPluginContext context, Petrinet net, XLog log,
			PAlignmentParameter parameter)
			throws ConnectionCannotBeObtained, AStarException {

		PLog pLog = PTraceConstructionFactory.callContructPLogWorkflow(context, log,
				parameter);
		//		XLog log = (XLog) plogs[1];
		//		PLog pLog = (PLog) plogs[0];

		PartialOrderBuilder poBuilder = new StandardPTraceConverter(pLog, parameter);

		// XXC: Copied from replayLogGUI and set self the partial log builder of the algorithm 
		PartialNetReplayerUI partialnetReplayerUI = new PartialNetReplayerUI();
		Object[] resultConfiguration = partialnetReplayerUI.getConfiguration(context, net,
				log);
		if (resultConfiguration == null) {
			context.getFutureResult(0).cancel(true);
			return null;
		}

		// if all parameters are set, replay log
		if (resultConfiguration[PNReplayerUI.MAPPING] != null) {
			context.log("replay is performed. All parameters are set.");

			// This connection MUST exists, as it is constructed by the configuration if necessary
			context.getConnectionManager().getFirstConnection(
					EvClassLogPetrinetConnection.class, context, net, log);

			// get all parameters
			IPNPartialOrderAwareReplayAlgorithm selectedAlg = (IPNPartialOrderAwareReplayAlgorithm) resultConfiguration[PNReplayerUI.ALGORITHM];
			IPNReplayParameter algParameters = (IPNReplayParameter) resultConfiguration[PNReplayerUI.PARAMETERS];
			TransEvClassMapping mapping = (TransEvClassMapping) resultConfiguration[PNReplayerUI.MAPPING];

			// since based on GUI, create connection
			algParameters.setCreateConn(true);
			algParameters.setGUIMode(false);

			//			if(selectedAlg instanceof PartialOrderILPLinearAlg){
			//				((PartialOrderILPLinearAlg) selectedAlg).setComputeLasy(parameter.isComputeLasy());
			//			}

			PNRepResult res = replayLog(context, net, log, mapping, selectedAlg,
					poBuilder, algParameters);

			//			
			context.getProvidedObjectManager().createProvidedObject(
					PartialUtil.getPartialLogName(log), pLog, PLog.class, context);
			context.addConnection(new PAlignmentResultConnection(log, pLog, res, net,
					mapping, (CostBasedCompleteParam) algParameters, parameter));

			context.getFutureResult(0)
					.setLabel("Replay result - log "
							+ XConceptExtension.instance().extractName(log) + " on "
							+ net.getLabel() + " using " + selectedAlg.toString());

			return res;

		} else {
			context.log(
					"replay is not performed because not enough parameter is submitted");
			context.getFutureResult(0).cancel(true);
			return null;
		}
	}

	/**
	 * No GUI variants
	 */
	@PluginVariant(variantLabel = "Complete parameters", requiredParameterLabels = { 0, 1,
			2, 3, 4 })
	public PNRepResult replayLog(PluginContext context, Petrinet net, XLog log,
			TransEvClassMapping mapping, IPNPartialOrderAwareReplayAlgorithm selectedAlg,
			PartialOrderBuilder poBuilder, IPNReplayParameter parameters)
			throws AStarException {
		return replayLogPrivate(context, net, log, mapping, selectedAlg, poBuilder,
				parameters);
	}

	public PNRepResult replayLog(PluginContext context, PetrinetGraph net, XLog log,
			TransEvClassMapping mapping, IPNPartialOrderAwareReplayAlgorithm selectedAlg,
			PartialOrderBuilder poBuilder, IPNReplayParameter parameters)
			throws AStarException {
		return replayLogPrivate(context, net, log, mapping, selectedAlg, poBuilder,
				parameters);
	}

	/**
	 * Experiment variants
	 */
	public PNRepResult replayLogExperiment(UIPluginContext context, Petrinet net,
			XLog log, TransEvClassMapping mapping,
			IPNPartialOrderAwareReplayAlgorithm selectedAlg,
			IPNReplayParameter algParameters, PAlignmentParameter param)
			throws AStarException {
		PLogPlugin converter = new PLogPlugin();
		PLog pLog = converter.computePTraces(context, log, param);
		//		XLog log = (XLog) plogs[1];
		//		PLog pLog = (PLog) plogs[0];

		if (selectedAlg instanceof PartialOrderILPLinearAlg) {
			((PartialOrderILPLinearAlg) selectedAlg)
					.setComputeLasy(param.isComputeLasy());
		}

		PartialOrderBuilder poBuilder = new StandardPTraceConverter(pLog, param);
		return replayLogPrivate(context, net, log, mapping, selectedAlg, poBuilder,
				algParameters);
	}

	/**
	 * Main method to replay log.
	 * 
	 * @param context
	 * @param net
	 * @param log
	 * @param mapping
	 * @param selectedAlg
	 * @param parameters
	 * @return
	 * @throws AStarException
	 */
	public PNRepResult replayLogPrivate(PluginContext context, PetrinetGraph net,
			XLog log, TransEvClassMapping mapping,
			IPNPartialOrderAwareReplayAlgorithm selectedAlg,
			PartialOrderBuilder poBuilder, IPNReplayParameter parameters)
			throws AStarException {
		if (selectedAlg.isAllReqSatisfied(context, net, log, mapping, parameters)) {
			// XXC: we need to have a poBuilder because the default poBuilder does not work. 
			// XXC: because we need to keep track of event indeces. 
			// XXA: set PartialOrderBuilder and set use partial ordered events always true.
			selectedAlg.setPartialOrderBuilder(poBuilder);

			// XXC: set partial log builder 
			if (parameters instanceof CostBasedCompleteParam) {
				((CostBasedCompleteParam) parameters).setUsePartialOrderedEvents(true);
			}

			// for each trace, replay according to the algorithm. Only returns two objects
			PNRepResult replayRes = null;

			if (parameters.isGUIMode()) {
				long start = System.nanoTime();

				replayRes = selectedAlg.replayLog(context, net, log, mapping, parameters);

				long period = System.nanoTime() - start;
				NumberFormat nf = NumberFormat.getInstance();
				nf.setMinimumFractionDigits(2);
				nf.setMaximumFractionDigits(2);

				context.log("Replay is finished in " + nf.format(period / 1000000000)
						+ " seconds");
			} else {
				replayRes = selectedAlg.replayLog(context, net, log, mapping, parameters);
			}

			// add connection
			if (replayRes != null) {
				if (parameters.isCreatingConn()) {
					context.addConnection(new PNRepResultAllRequiredParamConnection(
							"Connection between replay result, "
									+ XConceptExtension.instance().extractName(log)
									+ ", and " + net.getLabel(),
							net, log, mapping, selectedAlg, parameters, replayRes));
				}
			}

			return replayRes;
		} else {
			if (context != null) {
				context.log(
						"The provided parameters is not valid for the selected algorithm.");
				context.getFutureResult(0).cancel(true);
			}
			return null;
		}
	}

	//	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Xixi Lu", email = "x.lu@tue.nl", pack = "")
	//	@PluginVariant(variantLabel = "From Reset net and Event Log", requiredParameterLabels = { 0, 1 })
	//	public PNRepResult replayLog(final UIPluginContext context, ResetNet net, XLog log) throws ConnectionCannotBeObtained, AStarException {
	//		return replayLogGUI(context, net, log);
	//	}

	//	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Xixi Lu", email = "x.lu@tue.nl", pack = "")
	//	@PluginVariant(variantLabel = "From Reset Inhibitor net and Event Log", requiredParameterLabels = { 0, 1 })
	//	public PNRepResult replayLog(final UIPluginContext context, ResetInhibitorNet net, XLog log) throws ConnectionCannotBeObtained, AStarException {
	//		return replayLogGUI(context, net, log);
	//	}

	//	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Xixi Lu", email = "x.lu@tue.nl", pack = "")
	//	@PluginVariant(variantLabel = "From Inhibitor net and Event Log", requiredParameterLabels = { 0, 1 })
	//	public PNRepResult replayLog(final UIPluginContext context, InhibitorNet net, XLog log) throws ConnectionCannotBeObtained, AStarException {
	//		return replayLogGUI(context, net, log);
	//	}

	//	@PluginVariant(variantLabel = "Complete parameters", requiredParameterLabels = { 0, 1, 2, 3, 4 })
	//	public PNRepResult replayLog(PluginContext context, ResetNet net, XLog log, TransEvClassMapping mapping,
	//			IPNPartialOrderAwareReplayAlgorithm selectedAlg, PartialOrderBuilder poBuilder,
	//			IPNReplayParameter parameters) throws AStarException {
	//		return replayLogPrivate(context, net, log, mapping, selectedAlg, poBuilder, parameters);
	//	}

	//	@PluginVariant(variantLabel = "Complete parameters", requiredParameterLabels = { 0, 1, 2, 3, 4 })
	//	public PNRepResult replayLog(PluginContext context, ResetInhibitorNet net, XLog log, TransEvClassMapping mapping,
	//			IPNPartialOrderAwareReplayAlgorithm selectedAlg, PartialOrderBuilder poBuilder,
	//			IPNReplayParameter parameters) throws AStarException {
	//		return replayLogPrivate(context, net, log, mapping, selectedAlg, poBuilder, parameters);
	//	}

	//	@PluginVariant(variantLabel = "Complete parameters", requiredParameterLabels = { 0, 1, 2, 3, 4 })
	//	public PNRepResult replayLog(PluginContext context, InhibitorNet net, XLog log, TransEvClassMapping mapping,
	//			IPNPartialOrderAwareReplayAlgorithm selectedAlg, PartialOrderBuilder poBuilder,
	//			IPNReplayParameter parameters) throws AStarException {
	//		return replayLogPrivate(context, net, log, mapping, selectedAlg, poBuilder, parameters);
	//	}

}
