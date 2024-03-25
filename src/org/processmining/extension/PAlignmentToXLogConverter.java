package org.processmining.extension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.id.XID;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginCategory;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.partialorder.models.palignment.Move;
import org.processmining.partialorder.models.palignment.MoveDependency;
import org.processmining.partialorder.models.palignment.PAlignment;
import org.processmining.partialorder.models.palignment.PAlignmentConverter;
import org.processmining.partialorder.models.replay.POSyncReplayResult;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;
import org.processmining.xesalignmentextension.XAlignmentExtension;
import org.processmining.xesalignmentextension.XAlignmentExtension.MoveType;

import gnu.trove.map.hash.THashMap;
import nl.tue.astar.AStarException;

@Plugin(name = "Convert POAlignment to XES log", returnLabels = {
		"Aligned PO log" }, returnTypes = { XLog.class }, help = "", categories = {
				PluginCategory.ConformanceChecking,
				PluginCategory.Analytics }, keywords = { "Partial Order",
						"Conformance" }, parameterLabels = { "PO Alignments",
								"Log" }, userAccessible = true)
public class PAlignmentToXLogConverter {

	private XFactory factory = XFactoryRegistry.instance().currentDefault();
	private XAlignmentExtension alignExtension = XAlignmentExtension.instance();
	private XConceptExtension ce = XConceptExtension.instance();
	private XPOLogExtenstion poExt = XPOLogExtenstion.instance();

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Xixi Lu", email = "x.lu@tue.nl", pack = "PartialOrderReplayer")
	@PluginVariant(variantLabel = "Alignments to log", requiredParameterLabels = { 0, 1 })
	public XLog replayLog(UIPluginContext context, PNRepResult logReplayResult, XLog log)
			throws AStarException {
		XLog newLog = factory.createLog();
		for (SyncReplayResult res : logReplayResult) {
			/*
			 * For each trace that is associate with the alignment res,
			 * construct different graphs because the data dependencies are
			 * different
			 */
			for (int traceIndex : res.getTraceIndex()) {
				POSyncReplayResult pores = (POSyncReplayResult) res;

				XTrace t = copyAsXTrace(pores, log.get(traceIndex));
				newLog.add(t);
			}
		}
		return newLog;
	}

	public XTrace copyAsXTrace(final POSyncReplayResult pores, XTrace xTrace) {
		PAlignment pAlignment = pores.getPOAlignmentGraph();
		XTrace newTrace = factory.createTrace(xTrace.getAttributes());

//		XAlignmentExtension.instance().assignFitness(newTrace, pores.getInfo().get(arg0));

		Map<Move, XID> map = new THashMap<Move, XID>();
		for (Move move : pAlignment.getMoves()) {
			XEvent e = copyAsXEvent(move, xTrace);

			XID id = poExt.assignPOid(e);

			map.put(move, id);
			newTrace.add(e);
		}
		addPAlignmentDependencies(pAlignment, newTrace, map);
		return newTrace;
	}

	private void addPAlignmentDependencies(PAlignment alignment, XTrace newTrace,
			Map<Move, XID> map) {
		// TODO Auto-generated method stub
		List<Causality> traceListCauseDeps = new ArrayList<>();
		for (MoveDependency dep : alignment.getDependencies()) {
//			if (isMoveDependencyQualified(dep, visType)) {
			XID source = map.get(dep.getSource());
			XID target = map.get(dep.getTarget());
			traceListCauseDeps.add(new Causality(source, target));
		}

		poExt.putAllCauseDeps(newTrace.getAttributes(), traceListCauseDeps);

	}
//
//	private void addPAlignmentDependencies(PAlignment pAlignment, PartialOrderGraph graph,
//			Map<Move, PONode> map, PartialVisualType visType) {
//		for (MoveDependency dep : pAlignment.getDependencies()) {
//			if (isMoveDependencyQualified(dep, visType)) {
//				PONode source = map.get(dep.getSource());
//				PONode target = map.get(dep.getTarget());
//				POEdge edge = convert(source, target, dep, graph);
//				graph.addEdge(edge);
//			}
//		}
//		if (visType.equals(PartialVisualType.ALIGNMENT_MINIMAL)) {
//			PartialOrderGraphFactory.removeRedundantEdges(graph);
//		} else if (visType.equals(PartialVisualType.MAXIMAL_CLOSURE)) {
//			PartialOrderGraphFactory.addTransitiveEdges(graph);
//		}
//
//	}

	public XEvent copyAsXEvent(Move step, XTrace xTrace) {

		final XEvent event = factory.createEvent();

		// Log Move | Synchronous
		if (step.isLogMove()) {
			for (Entry<String, XAttribute> attr : xTrace.get(step.getEventIndex())
					.getAttributes().entrySet()) {
				event.getAttributes().put(attr.getKey(), attr.getValue());
			}
			alignExtension.assignMoveType(event, MoveType.LOG);
			alignExtension.assignLogMove(event, ce.extractName(step.getEvent()));
			alignExtension.assignIsObservable(event, true);

		} else if (step.isSyncMove()) {
			for (Entry<String, XAttribute> attr : xTrace.get(step.getEventIndex())
					.getAttributes().entrySet()) {
				event.getAttributes().put(attr.getKey(), attr.getValue());
			}

			alignExtension.assignMoveType(event, MoveType.SYNCHRONOUS);
			alignExtension.assignLogMove(event, ce.extractName(step.getEvent()));
			alignExtension.assignModelMove(event, step.getTransition().getLabel());
			alignExtension.assignIsObservable(event, true);

		} else {
			// Model Move
			alignExtension.assignMoveType(event, MoveType.MODEL);
			alignExtension.assignModelMove(event, step.getTransition().getLabel());
//			alignmentExtension.assignActivityId(event, convertTransitionId(step.getProcessView()));
			if (step.isSilentModelMove()) {
				alignExtension.assignIsObservable(event, false);
			} else {
				alignExtension.assignIsObservable(event, true);
			}
		}
		ce.assignName(event,
				PAlignmentConverter.getMoveLabel(step, new XEventNameClassifier()));
		return event;
	}

}
