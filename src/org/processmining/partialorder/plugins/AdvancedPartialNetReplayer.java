package org.processmining.partialorder.plugins;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XLog;
import org.processmining.connection.PAlignmentResultConnection;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginCategory;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.ui.wizard.ListWizard;
import org.processmining.framework.util.ui.wizard.ProMWizardDisplay;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;
import org.processmining.models.connections.petrinets.PNRepResultAllRequiredParamConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.partialorder.plugins.replay.PartialOrderILPLinearAlg;
import org.processmining.partialorder.plugins.replay.StandardPTraceConverter;
import org.processmining.partialorder.ptrace.model.PLog;
import org.processmining.partialorder.ptrace.param.PAlignmentParameter;
import org.processmining.partialorder.ptrace.plugins.builder.PTraceConstructionFactory;
import org.processmining.partialorder.util.MarkingFactory;
import org.processmining.partialorder.zexperiment.M2HCaseStudy;
import org.processmining.plugins.astar.petrinet.PartialOrderBuilder;
import org.processmining.plugins.astar.petrinet.manifestreplay.CostBasedCompleteManifestParam;
import org.processmining.plugins.astar.petrinet.manifestreplay.ManifestFactory;
import org.processmining.plugins.astar.petrinet.manifestreplay.PNManifestFlattener;
import org.processmining.plugins.astar.petrinet.manifestreplay.ui.ChooseAlgorithmStep;
import org.processmining.plugins.astar.petrinet.manifestreplay.ui.CreatePatternPanel;
import org.processmining.plugins.astar.petrinet.manifestreplay.ui.CreatePatternStep;
import org.processmining.plugins.astar.petrinet.manifestreplay.ui.MapCostStep;
import org.processmining.plugins.astar.petrinet.manifestreplay.ui.MapPattern2TransStep;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.manifestreplayer.PNManifestReplayerParameter;
import org.processmining.plugins.petrinet.manifestreplayer.algorithms.IPNManifestReplayAlgorithm;
import org.processmining.plugins.petrinet.manifestreplayer.algorithms.PNManifestReplayerILPAlgorithm;
import org.processmining.plugins.petrinet.manifestreplayresult.Manifest;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNPartialOrderAwareReplayAlgorithm;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;

import nl.tue.astar.AStarException;

/**
 * 
 * @author xlu
 * 
 */
@Plugin(
		name = "Partial Advanced Replayer",
		returnLabels = { "PO-Replay Result" },
		returnTypes = { PNRepResult.class },
		help = "This plugin is similar to the Partial Aware Replayer, thus also checks conformance (using alignment) between the given log and the given model and is partial aware. "
				+ "In addition, this plugin has a more advanced configuration wizards that allows user to map multiple event classes to a single transition.",
				
				categories = { PluginCategory.ConformanceChecking,
				PluginCategory.Analytics }, keywords = { "Partial Order", "Conformance" },
				parameterLabels = {
				"Petri net", "Event Log", "Algorithm", "Parameters" }, userAccessible = true)
public class AdvancedPartialNetReplayer {
	

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Xixi Lu", email = "x.lu@tue.nl", pack = "PartialOrderReplayer")
	@PluginVariant(variantLabel = "From Petri net and Event Log", requiredParameterLabels = { 0, 1 })
	public PNRepResult replayLog(UIPluginContext context, Petrinet net, XLog log) throws AStarException {
		PAlignmentParameter parameter = new PAlignmentParameter();
		PLog pLog = PTraceConstructionFactory.callContructPLogWorkflow(context, log, parameter);
		//		XLog log = (XLog) plogs[1];
		//		PLog pLog = (PLog) plogs[0];

		PartialOrderBuilder poBuilder = new StandardPTraceConverter(pLog, parameter);

		PNManifestReplayerParameter parameters = null;
		IPNManifestReplayAlgorithm alg = null;
		boolean gui = true;
		if (gui) {
			Object[] obj = chooseAlgorithmAndParam(context, net, log);
			if (obj == null) {
				context.getFutureResult(0).cancel(true);
				return null;
			}
			//			alg = (IPNManifestReplayAlgorithm) obj[0];
			parameters = (PNManifestReplayerParameter) obj[1];

		}
		/*
		 * The following code is to get the mapping used in M2H paper.
		 */
		else {
			//			
			alg = new PNManifestReplayerILPAlgorithm();
			parameters = M2HCaseStudy.autoComputeHospitalLogParameters(context, net, log);

		}
		PNManifestFlattener flattener = new PNManifestFlattener(net, parameters); // stores everything about petri net

		PartialOrderILPLinearAlg selectedAlg = new PartialOrderILPLinearAlg();
		//		selectedAlg.setComputeLasy(parameter.isComputeLasy());

		CostBasedCompleteManifestParam algParameters = new CostBasedCompleteManifestParam(
				flattener.getMapEvClass2Cost(), flattener.getMapTrans2Cost(), flattener.getMapSync2Cost(),
				flattener.getInitMarking(), flattener.getFinalMarkings(), parameters.getMaxNumOfStates(),
				flattener.getFragmentTrans());

		// since based on GUI, create connection
		algParameters.setCreateConn(true);
		algParameters.setGUIMode(false);

		PNRepResult pnRepResult = replayLogPrivate(context, flattener.getNet(), log, flattener.getMap(), selectedAlg,
				poBuilder, algParameters);

		//		if (parameter.isBuildConnection()) {
		//			context.addConnection(new PNManifestConnection("Manifest connection", net, log, alg, parameter, manifest));
		//		}

		try {
			Manifest manifestation = ManifestFactory.construct(net, parameters.getInitMarking(),
					parameters.getFinalMarkings(), log, flattener, pnRepResult, parameters.getMapping());
			//			if (context != null) {
			//				context.getFutureResult(0).setLabel(
			//						"Sequence pattern manifestation in " + XConceptExtension.instance().extractName(log));
			//			}
			context.getProvidedObjectManager().createProvidedObject("Partial Manifest", manifestation, Manifest.class,
					context);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//		PNManifestReplayer replayer = new PNManifestReplayer();
		//		Manifest manifest = replayer.replayLog(context, net, log, alg, parameters);
		//		if (parameters.isBuildConnection()) {
		//			context.addConnection(new PNManifestConnection("Seq Manifest connection", net, log, alg, parameters,
		//					manifest));
		//		}
		//		context.getProvidedObjectManager().createProvidedObject("Sequential Manifest", manifest, Manifest.class,
		//				context);

		context.addConnection(new PAlignmentResultConnection(log, pLog, pnRepResult, flattener.getNet(), flattener
				.getMap(), algParameters, parameter));

		return pnRepResult;
	}

	/**
	 * Arya's GUI approach
	 * 
	 * @param context
	 * @param net
	 * @param log
	 * @return
	 */

	public Object[] chooseAlgorithmAndParam(UIPluginContext context, PetrinetGraph net, XLog log) {
		Marking initialMarking = MarkingFactory.createInitialMarking(context, net);

		Marking[] finalMarkings = MarkingFactory.createFinalMarkings(context, net);

		/**
		 * Utilities
		 */
		// generate create pattern GUI
		// list possible classifiers
		List<XEventClassifier> classList = new ArrayList<XEventClassifier>(log.getClassifiers());
		// add default classifiers
		if (!classList.contains(XLogInfoImpl.RESOURCE_CLASSIFIER)) {
			classList.add(0, XLogInfoImpl.RESOURCE_CLASSIFIER);
		}
		if (!classList.contains(XLogInfoImpl.NAME_CLASSIFIER)) {
			classList.add(0, XLogInfoImpl.NAME_CLASSIFIER);
		}
		if (!classList.contains(XLogInfoImpl.STANDARD_CLASSIFIER)) {
			classList.add(0, XLogInfoImpl.STANDARD_CLASSIFIER);
		}

		XEventClassifier[] availableClassifiers = classList.toArray(new XEventClassifier[classList.size()]);
		CreatePatternStep createPatternStep = new CreatePatternStep(log, availableClassifiers);

		// results, required earlier for wizard
		PNManifestReplayerParameter parameter = new PNManifestReplayerParameter();

		// generate pattern mapping GUI
		MapPattern2TransStep mapPatternStep = new MapPattern2TransStep(net, log,
				(CreatePatternPanel) createPatternStep.getComponent(parameter));

		ChooseAlgorithmStep chooseAlgorithmStep = new ChooseAlgorithmStep(net, log, initialMarking, finalMarkings);

		// generate cost setting GUI
		MapCostStep mapCostStep = new MapCostStep(createPatternStep.getPatternCreatorPanel(),
				mapPatternStep.getPatternMappingPanel());

		// construct dialog wizard
		ArrayList<ProMWizardStep<PNManifestReplayerParameter>> listSteps = new ArrayList<ProMWizardStep<PNManifestReplayerParameter>>(
				4);
		listSteps.add(createPatternStep);
		listSteps.add(mapPatternStep);
		listSteps.add(chooseAlgorithmStep);
		listSteps.add(mapCostStep);

		ListWizard<PNManifestReplayerParameter> wizard = new ListWizard<PNManifestReplayerParameter>(listSteps);

		// show wizard
		parameter = ProMWizardDisplay.show(context, wizard, parameter);

		if (parameter == null) {
			return null;
		}

		// show message: GUI mode
		parameter.setGUIMode(true);

		IPNManifestReplayAlgorithm alg = chooseAlgorithmStep.getSelectedAlgorithm();
		return new Object[] { alg, parameter };
	}

	public PNRepResult replayLogPrivate(PluginContext context, PetrinetGraph net, XLog log,
			TransEvClassMapping mapping, IPNPartialOrderAwareReplayAlgorithm selectedAlg,
			PartialOrderBuilder poBuilder, IPNReplayParameter parameters) throws AStarException {
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

				context.log("Replay is finished in " + nf.format(period / 1000000000) + " seconds");
			} else {
				replayRes = selectedAlg.replayLog(context, net, log, mapping, parameters);
			}

			// add connection
			if (replayRes != null) {
				if (parameters.isCreatingConn()) {
					context.addConnection(new PNRepResultAllRequiredParamConnection(
							"Connection between replay result, " + XConceptExtension.instance().extractName(log)
									+ ", and " + net.getLabel(), net, log, mapping, selectedAlg, parameters, replayRes));
				}
			}

			return replayRes;
		} else {
			if (context != null) {
				context.log("The provided parameters is not valid for the selected algorithm.");
				context.getFutureResult(0).cancel(true);
			}
			return null;
		}
	}

}
