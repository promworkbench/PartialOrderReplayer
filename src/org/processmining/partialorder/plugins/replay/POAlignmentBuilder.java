package org.processmining.partialorder.plugins.replay;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.partialorder.models.dependency.PDependency;
import org.processmining.partialorder.models.palignment.Move;
import org.processmining.partialorder.models.palignment.MoveDependency;
import org.processmining.partialorder.models.palignment.MoveDependencyFactory;
import org.processmining.partialorder.models.palignment.MoveFactory;
import org.processmining.partialorder.models.palignment.PAlignment;
import org.processmining.partialorder.models.palignment.PAlignmentImp;
import org.processmining.partialorder.models.palignment.dependency.MoveDependencyLinear;
import org.processmining.partialorder.models.replay.POAlignmentDataProvider;
import org.processmining.partialorder.models.replay.POAlignmtUnfoldedNet;
import org.processmining.partialorder.models.replay.POSyncReplayResult;
import org.processmining.partialorder.models.replay.SyncProductModel;
import org.processmining.partialorder.models.replay.SyncProductModel.PlaceType;
import org.processmining.partialorder.ptrace.model.PTrace;
import org.processmining.partialorder.util.PartialUtil;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayresult.StepTypes;

import gnu.trove.list.TIntList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class POAlignmentBuilder {
	private static final ExecutorService service = Executors.newFixedThreadPool(2);

	public static PAlignment computePAlignmentAndUpdateResult(PetrinetGraph graph,
			Marking initialMarking, Marking[] finalMarkings, XEventClasses eventClasses,
			TransEvClassMapping mapping, XTrace trace, PTrace potrace, int traceIndex,
			POSyncReplayResult pores) {

		final Callable<PAlignment> callable = getCallableComputingPAlignment(graph,
				initialMarking, finalMarkings, eventClasses, mapping, trace, potrace,
				traceIndex, pores);

		final Future<PAlignment> result = service.submit(callable);

		PAlignment alignment = null;
		try {
			alignment = result.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return alignment;
	}

	private static Callable<PAlignment> getCallableComputingPAlignment(
			final PetrinetGraph graph, final Marking initialMarking,
			final Marking[] finalMarkings, final XEventClasses eventClasses,
			final TransEvClassMapping mapping, final XTrace trace, final PTrace potrace,
			final int traceIndex, final POSyncReplayResult pores) {

		Callable<PAlignment> callable = new Callable<PAlignment>() {
			public PAlignment call() throws Exception {
				//Debug
				if (isTrace(trace)) {
					System.out.println("Test");
				}
				Object[] result = POAlignmentBuilder.unfoldingAlignment(graph, potrace,
						traceIndex, pores, initialMarking, finalMarkings, eventClasses,
						mapping);
				SyncProductModel syncModel = (SyncProductModel) result[0];
				POAlignmtUnfoldedNet unfoldednet = (POAlignmtUnfoldedNet) result[1];

				// convert to a simple poalignment graph
				PAlignment palignment = POAlignmentBuilder.convertUnfoldingToPAlignment(
						potrace, trace, traceIndex, pores, syncModel, unfoldednet);

				pores.setPotrace(potrace);
				pores.setGraph(palignment);
				pores.setSyncModel(syncModel);
				pores.setUnfoldednet(unfoldednet);
				return palignment;
			}

			private boolean isTrace(XTrace trace) {
				String s = "";
				for (XEvent e : trace) {
					s += XConceptExtension.instance().extractName(e);

				}
				String list = "Confirmation of receiptT06 Determine necessity of stop adviceT07-4 Draft internal advice to hold for type 4T06 Determine necessity of stop adviceT10 Determine necessity to stop indicationT02 Check confirmation of receiptT04 Determine confirmation of receiptT05 Print and send confirmation of receipt";
				return s.equals(list);
			}
		};
		return callable;
	}

	/**
	 * Unfold the linear alignment on the synchronous product model to obtain a
	 * p-alignment
	 * 
	 * @param net
	 *            The original process model (currently only support
	 *            {@link Petrinet})
	 * @param potrace
	 *            The partially ordered trace
	 * @param traceIndex
	 *            The index of the linear trace in the log
	 * @param res
	 *            The linear replay result
	 * @param initMarking
	 *            The initial marking of the model
	 * @param finalMarkings
	 *            The set of final markings of the model
	 * @param classes
	 *            The set of event classes used in the replay
	 * @param mapping
	 *            The mapping between events and transitions used in the replay
	 * @return object[0] = (SyncProductModel) syncModel, object[1] =
	 *         (POAlignmentPetrinetGraph) unfolded net
	 */
	private static Object[] unfoldingAlignment(PetrinetGraph net, PTrace potrace,
			int traceIndex, POSyncReplayResult res, Marking initMarking,
			Marking[] finalMarkings, XEventClasses classes, TransEvClassMapping mapping) {
		// TODO for other petrinet class, currently only Petrinet.class
		SyncProductModel syncModel = new SyncProductModel(net, potrace, classes,
				initMarking, finalMarkings, mapping);

		/*
		 * reformat a list of objects using the alignment that can be replayed
		 * on syncModel
		 */
		List<Object> aligmtTrace = new LinkedList<Object>();
		for (int i = 0; i < res.getNodeInstance().size(); i++) {
			Object obj = res.getNodeInstance().get(i);
			StepTypes type = res.getStepTypes().get(i);
			if (obj instanceof Transition
					&& (type.equals(StepTypes.MINVI) || type.equals(StepTypes.MREAL))) {
				aligmtTrace.add(obj);
			} else {
				XEvent evt = potrace.getTrace().get(res.getIndeces().get(i));
				aligmtTrace.add(evt);
			}
		}

		/*
		 * unfold syncModel based on the result
		 */
		POAlignmtUnfoldedNet unfoldnet = unfoldingBase(syncModel, res, aligmtTrace);

		return new Object[] { syncModel, unfoldnet };
	}

	/*
	 * Create B_0 for unfolding
	 */
	private static POAlignmtUnfoldedNet unfoldingBase(SyncProductModel syncModel,
			POSyncReplayResult res, List<Object> result) {

		POAlignmtUnfoldedNet unfoldnet = new POAlignmtUnfoldedNet("");

		Map<Transition, Transition> mapClone2Orig = new HashMap<Transition, Transition>();
		Map<Place, Place> mapClonePlace2Orig = new HashMap<Place, Place>();
		Map<Place, Place> mapOrigPlace2Clone = new HashMap<Place, Place>();

		int i = 0;
		Marking currMarkingUnf = new Marking(); // the current marking of unfoldnet
		Marking currMarking = new Marking(); // the current marking of synchron model
		for (Place p : syncModel.getInitialMarking()) {
			Place newp = unfoldnet.addPlace(p.getLabel() + "-B" + i++);
			mapClonePlace2Orig.put(newp, p);
			mapOrigPlace2Clone.put(p, newp);
			currMarkingUnf.add(newp);
			currMarking.add(p);
		}

		unfoldingStep(0, syncModel, currMarking, res, unfoldnet, currMarkingUnf, i,
				result, mapOrigPlace2Clone, mapClonePlace2Orig, mapClone2Orig);

		return unfoldnet;

	}

	/*
	 * Compute the step B_i for unfolding
	 */
	private static void unfoldingStep(int stepIndex, SyncProductModel syncModel,
			Marking currMarking, POSyncReplayResult res, POAlignmtUnfoldedNet unfoldnet,
			Marking currMarkingUnf, int numConditions, List<Object> result,
			Map<Place, Place> mapOrigPlace2Clone, Map<Place, Place> mapClonePlace2Orig,
			Map<Transition, Transition> mapClone2Orig) {

		StepTypes stepType = res.getStepTypes().get(stepIndex);
		Object obj = result.get(stepIndex);

		// Get the step at stepIndex of the alignment and map it to an (enabled) transition.
		// TODO: check enabled....
		Transition synmTrans = syncModel.getTransition(obj, currMarking, stepType,
				res.getIndeces().get(stepIndex));

		if (synmTrans != null) {
			Transition t = unfoldnet.addTransition(synmTrans.getLabel(), stepIndex);
			// Append transition t and t* to unfoldnet
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> inEdge : syncModel
					.getPetrinetModel().getInEdges(synmTrans)) {
				Place orgInPlace = (Place) inEdge.getSource();
				Place inPlace = mapOrigPlace2Clone.get(orgInPlace);
				if (currMarkingUnf.contains(inPlace)) {
					// add arc (inplace, t)

					unfoldnet.addArc(inPlace, t);

					currMarkingUnf.remove(inPlace);
					currMarking.remove(orgInPlace);

				} else {
					// error
					System.err.println(
							"Found a transition enabled in synchronized model but not enabled in unfold net.");
				}
			}
			// Put new conditions of out places in unfold net.
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> outEdge : syncModel
					.getPetrinetModel().getOutEdges(synmTrans)) {
				Place orgOutPlace = (Place) outEdge.getTarget();
				Place newp = unfoldnet
						.addPlace(orgOutPlace.getLabel() + "-B" + numConditions++);
				unfoldnet.addArc(t, newp);
				mapOrigPlace2Clone.put(orgOutPlace, newp);
				mapClonePlace2Orig.put(newp, orgOutPlace);
				currMarking.add(orgOutPlace);
				currMarkingUnf.add(newp);
			}

		} else {
			System.err.println("No enabled transition");
		}
		// Added arcs (p, t) to unfoldnet 
		if (++stepIndex < res.getStepTypes().size()) {
			unfoldingStep(stepIndex, syncModel, currMarking, res, unfoldnet,
					currMarkingUnf, numConditions, result, mapOrigPlace2Clone,
					mapClonePlace2Orig, mapClone2Orig);

		}

	}

	//	/**
	//	 * Convert an unfolded petri net of a poalignment into a {@link PAlignment}
	//	 * 
	//	 * @param potrace The partially ordered trace
	//	 * @param trace The linear trace
	//	 * @param traceIndex The index of linear trace in the original log
	//	 * @param pores The linear replay result used to simply the unfoldednet
	//	 * @param syncModel The synchronous product used to unfold the linear alignment
	//	 * @param unfoldednet The unfolded alignment that is to be converted into a {@link PartialOrderGraph}
	//	 * @return A simplified alignment in {@link PartialOrderGraph}
	//	 */
	private static PAlignment convertUnfoldingToPAlignment(PTrace potrace, XTrace trace,
			int traceIndex, POSyncReplayResult pores, SyncProductModel syncModel,
			POAlignmtUnfoldedNet unfoldednet) {

		String name = PartialUtil.getPOTraceName(trace, traceIndex);
		PAlignment alignment = new PAlignmentImp(name, traceIndex);

		List<StepTypes> stepTypes = pores.getStepTypes();
		Map<Transition, Move> mapOrigToNode = new HashMap<Transition, Move>();

		/*
		 * Add nodes
		 */
		for (Transition t : unfoldednet.getTransitions()) {

			int stepIndex = unfoldednet.getTransitionStepIndex(t);
			StepTypes type = stepTypes.get(stepIndex);
			int eventIndex = pores.getIndeces().get(stepIndex);
			XEvent evt = eventIndex >= 0 ? trace.get(eventIndex) : null;
			Object origTrans = pores.getNodeInstance().get(stepIndex);

			Move move = MoveFactory.createMove(type, stepIndex, eventIndex, evt,
					origTrans);

			alignment.addMove(move);
			mapOrigToNode.put(t, move);

		}

		/*
		 * add edges
		 */
		for (Place p : unfoldednet.getPlaces()) {
			if (unfoldednet.getInEdges(p).size() > 1
					|| unfoldednet.getOutEdges(p).size() > 1) {
				System.err.println("Place " + p.getLabel()
						+ " has more than one incoming or outgoing edges");
			}
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> in : unfoldednet
					.getInEdges(p)) {
				Transition source = (Transition) in.getSource();
				for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> out : unfoldednet
						.getOutEdges(p)) {
					Transition target = (Transition) out.getTarget();
					Move snode = mapOrigToNode.get(source);
					Move tnode = mapOrigToNode.get(target);

					MoveDependency edge = alignment.getDependency(snode, tnode);
					PlaceType placeType = SyncProductModel.getPlaceType(p.getLabel());

					int sourceIndex = unfoldednet.getTransitionStepIndex(source);
					int targetIndex = unfoldednet.getTransitionStepIndex(target);
					int sourceEventIndex = pores.getIndeces().get(sourceIndex);
					int targetEventIndex = pores.getIndeces().get(targetIndex);
					PDependency r = null;
					if (targetEventIndex >= 0 && sourceEventIndex >= 0) {
						r = potrace.getDependency(sourceEventIndex, targetEventIndex);
					}
					MoveDependency newEdge = MoveDependencyFactory
							.updateDependency(placeType, snode, tnode, edge, r);
					alignment.removeDependency(edge);
					alignment.addDependency(newEdge);

				}
			}
		}

		return alignment;
	}

	public static PAlignment computeLinearPAlignment(POAlignmentDataProvider data,
			int traceIndex, POSyncReplayResult pores) {

		PTrace potrace = data.getPoLog().get(traceIndex);

		List<StepTypes> steps = pores.getStepTypes();
		TIntList indeces = pores.getIndeces();

		// reformat node instance list : list of strings
		List<String> result = new LinkedList<String>();
		for (Object obj : pores.getNodeInstance()) {
			if (obj instanceof Transition) {
				result.add(((Transition) obj).getLabel());
			} else if (obj instanceof String) {
				result.add((String) obj);
			} else {
				result.add(obj.toString());
			}
		}

		XTrace trace = data.getXTrace(traceIndex);
		String name = PartialUtil.getPOTraceName(trace, potrace.getTraceIndex());

		PAlignment graph = new PAlignmentImp(name, traceIndex);

		TIntObjectMap<Move> map = new TIntObjectHashMap<Move>();
		TIntObjectMap<Move> mapOrg = new TIntObjectHashMap<Move>();

		for (int i = 0; i < indeces.size(); i++) { // i = index in alignment
			int eventIndex = indeces.get(i);
			XEvent event = eventIndex >= 0 ? trace.get(eventIndex) : null;

			int stepIndex = i;
			StepTypes type = steps.get(stepIndex);
			Transition origTrans = (Transition) pores.getNodeInstance().get(stepIndex);

			Move move = MoveFactory.createMove(type, stepIndex, eventIndex, event,
					origTrans);

			graph.addMove(move);
			map.put(i, move);
		}

		addAlignmentLinearEdges(graph, pores, data.getPetrinet(), trace, potrace, indeces,
				steps, result, map, mapOrg);
		return graph;
	}

	private static void addAlignmentLinearEdges(PAlignment graph,
			POSyncReplayResult pores, PetrinetGraph model, XTrace trace, PTrace potrace,
			TIntList indeces, List<StepTypes> steps, List<String> result,
			TIntObjectMap<Move> map, TIntObjectMap<Move> mapOrg) {

		for (int i = 1; i < indeces.size(); i++) {
			MoveDependency e = new MoveDependencyLinear(map.get(i - 1), map.get(i), i);
			graph.addDependency(e);
		}

	}

	public static PAlignment updateAlignment(PAlignment palignment, int traceIndex,
			XTrace trace) {
		PAlignment newAlignment = new PAlignmentImp(String.valueOf(traceIndex),
				traceIndex);
		for (Move move : palignment.getMoves()) {

			move.setEvent(trace.get(move.getEventIndex()));
		}
		return newAlignment;
	}

}
