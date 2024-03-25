package org.processmining.partialorder.models.replay;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.partialorder.models.dependency.PDependency;
import org.processmining.partialorder.ptrace.model.PTrace;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayresult.StepTypes;

public class SyncProductModel {
	Petrinet syncModel;
	PetrinetGraph orgModel;
	XEventClasses classes;
	Map<Transition, Transition> mapOrigToClone;
	Map<Place, Place> mapPlaceOrigToClone;
	Map<Transition, Transition> mapCloneToOrig;
	Map<Transition, Set<Place>> successors; // store clone 
	Map<Transition, Set<Place>> predecessors; // store clone
	Map<Integer, Transition> mapEvtToTrans;
	XTrace trace;

	Marking initMarking;
	Marking[] finalMarkings;




	public static enum PlaceType {
		M, L
	}

	public SyncProductModel(PetrinetGraph model, PTrace potrace, XEventClasses eclasses, Marking orgInitialMarking,
			Marking[] orgFinalMarkings, TransEvClassMapping mapping) {
		this.orgModel = model;
		this.syncModel = PetrinetFactory.newPetrinet("Unfolding trace " + potrace.getTraceIndex());
		this.classes = eclasses;
		this.trace = potrace.getTrace();
		Map<XEventClass, List<Transition>> transitionMapping = getEncodedEventMapping(mapping);

		initMarking = new Marking();
		finalMarkings = new Marking[orgFinalMarkings.length];

		// store mapping, successors, predecessors from original to clone
		mapOrigToClone = new HashMap<Transition, Transition>();
		mapPlaceOrigToClone = new HashMap<Place, Place>();
		mapCloneToOrig = new HashMap<Transition, Transition>();
		successors = new HashMap<Transition, Set<Place>>(); // store clone 
		predecessors = new HashMap<Transition, Set<Place>>(); // store clone
		mapEvtToTrans = new HashMap<Integer, Transition>(potrace.size());

		// 1. clone original model: each t in T, add (>>, t) to new model. 
		createMoveModelOnly(model, syncModel, mapOrigToClone, mapPlaceOrigToClone, mapCloneToOrig, predecessors,
				successors, classes);

		// 1.2 clone markings
		for (Place p : orgInitialMarking) {
			initMarking.add(mapPlaceOrigToClone.get(p));
		}
		for (int i = 0; i < orgFinalMarkings.length; i++) {
			Marking fm = orgFinalMarkings[i];
			finalMarkings[i] = new Marking();
			for (Place p : fm) {
				finalMarkings[i].add(mapPlaceOrigToClone.get(p));
			}
		}
		// 2. create partially-ordered-event-net: each e in trace, add (e, >>) as log move to new model.
		createMoveLogOnly(syncModel, potrace, mapEvtToTrans, predecessors, successors, initMarking, finalMarkings,
				classes);

		// 3. create synchronise moves. 
		createSynchronizeMove(mapEvtToTrans, transitionMapping, classes, syncModel, mapOrigToClone, mapCloneToOrig,
				predecessors, successors);

	}
	
	private void createMoveModelOnly(PetrinetGraph origNet, Petrinet netResult,
			Map<Transition, Transition> mapOrigToClone, Map<Place, Place> mapPlaceOrigToClone,
			Map<Transition, Transition> mapCloneToOrig, Map<Transition, Set<Place>> predecessors,
			Map<Transition, Set<Place>> successors, XEventClasses classes) {

//		Map<Place, Place> mapCloneToOrigPlaces = new THashMap<Place, Place>();
		for (Place p : origNet.getPlaces()) {
			Place clonePlace = netResult.addPlace(getModelPlaceLabel(p.getLabel()));
			mapPlaceOrigToClone.put(p, clonePlace);
//			mapCloneToOrigPlaces.put(clonePlace, p);
		}

		// move on model only, still with tokens
		for (Transition origTrans : origNet.getTransitions()) {
			// create transition
			Transition cloneTrans = netResult.addTransition(getTransitionLabel(origTrans));
			mapOrigToClone.put(origTrans, cloneTrans);
			mapCloneToOrig.put(cloneTrans, origTrans);

			// add input places
			Set<Place> sp = new HashSet<Place>();
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : origNet.getInEdges(origTrans)) {
				Place clonePlace = mapPlaceOrigToClone.get(edge.getSource());
				netResult.addArc(clonePlace, cloneTrans);
				sp.add(clonePlace);
			}
			predecessors.put(cloneTrans, sp);

			// add output places
			Set<Place> ss = new HashSet<Place>();
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : origNet.getOutEdges(origTrans)) {
				Place clonePlace = mapPlaceOrigToClone.get(edge.getTarget());
				netResult.addArc(cloneTrans, clonePlace);
				ss.add(clonePlace);
			}
			successors.put(cloneTrans, ss);

		}
	}

	private void createMoveLogOnly(Petrinet netResult, PTrace potrace, Map<Integer, Transition> mapEvtToTrans,
			Map<Transition, Set<Place>> predecessors, Map<Transition, Set<Place>> successors, Marking initialMarking,
			Marking[] finalMarkings, XEventClasses classes) {

		// partial order between events are determined by timestamp (can also be other things)
		// assume that event access already ordered by timestamp
		//		XTimeExtension xte = XTimeExtension.instance();

		XTrace trace = potrace.getTrace();

		for (Integer eventIndex : potrace.getEventIndices()) {
			XEvent e = trace.get(eventIndex);
			Transition t = netResult.addTransition(getTransitionLabel(eventIndex, e, StepTypes.L));
			mapEvtToTrans.put(eventIndex, t);
		}

		for (PDependency dataRelation : potrace.getDependencies()) {
			/* If only basic transitive edge */
			//			if(dataRelation.isTransitiveClosureEdge()){
			int predIndex = dataRelation.getSource();
			int succIndex = dataRelation.getTarget();
			Transition source = mapEvtToTrans.get(predIndex);
			Transition target = mapEvtToTrans.get(succIndex);
			Place p = netResult.addPlace(getLogPlaceLabel(predIndex, succIndex));
			netResult.addArc(source, p);
			netResult.addArc(p, target);

			if (!predecessors.containsKey(target)) {
				predecessors.put(target, new HashSet<Place>());
			}
			predecessors.get(target).add(p);
			if (!successors.containsKey(source)) {
				successors.put(source, new HashSet<Place>());
			}
			successors.get(source).add(p);
			//			}
		}
		for (Integer start : potrace.getStartEventIndices()) {
			//			XEvent startEvent = trace.get(start);
			Place p = netResult.addPlace(getLogPlaceLabel(-1, start));
			netResult.addArc(p, mapEvtToTrans.get(start));
			initialMarking.add(p);

			if (!predecessors.containsKey(mapEvtToTrans.get(start))) {
				predecessors.put(mapEvtToTrans.get(start), new HashSet<Place>());
			}
			predecessors.get(mapEvtToTrans.get(start)).add(p);

		}
		for (Integer end : potrace.getEndEventIndices()) {
			//			XEvent endEvent = trace.get(end);
			Place p = netResult.addPlace(getLogPlaceLabel(end, -1));
			netResult.addArc(mapEvtToTrans.get(end), p);

			if (!successors.containsKey(mapEvtToTrans.get(end))) {
				successors.put(mapEvtToTrans.get(end), new HashSet<Place>());
			}
			successors.get(mapEvtToTrans.get(end)).add(p);

			for (Marking fm : finalMarkings) {
				fm.add(p);
			}
		}

	}

	private void createSynchronizeMove(Map<Integer, Transition> mapEvtToTrans,
			Map<XEventClass, List<Transition>> transitionMapping, XEventClasses classes, Petrinet netResult,
			Map<Transition, Transition> mapOrigToClone, Map<Transition, Transition> mapCloneToOrig,
			Map<Transition, Set<Place>> predecessors, Map<Transition, Set<Place>> successors
	/* , Place costPool, int violatingCost, boolean identifyPartialViolating */) {
		for (Integer evt : mapEvtToTrans.keySet()) {
			// extract its event class
			if (!transitionMapping.containsKey(classes.getClassOf(trace.get(evt)))) {
				continue;
			}
			for (Transition origTrans : transitionMapping.get(classes.getClassOf(trace.get(evt)))) {
				Transition synchroTrans = netResult.addTransition(getTransitionLabel(evt, trace.get(evt),
						StepTypes.LMGOOD));

				// add mapping to original transition
				mapCloneToOrig.put(synchroTrans, origTrans);

				// model side
				Transition modelTrans = mapOrigToClone.get(origTrans);
				for (Place clonePlace : predecessors.get(modelTrans)) { // update predecessor 
					netResult.addArc(clonePlace, synchroTrans);
				}
				for (Place clonePlace : successors.get(modelTrans)) { // update successor 
					netResult.addArc(synchroTrans, clonePlace);
				}

				// log side
				Transition logTrans = mapEvtToTrans.get(evt);
				for (Place clonePlace : predecessors.get(logTrans)) { // update predecessor 
					netResult.addArc(clonePlace, synchroTrans);
				}

				for (Place clonePlace : successors.get(logTrans)) { // update successor 
					netResult.addArc(synchroTrans, clonePlace);

					//					if (violatingTransitions != null) {
					//						for (Transition violatingTrans : violatingTransitions) {
					//							netResult.addArc(violatingTrans, clonePlace);
					//						}
					//					}
				}

			}
		}
	}
	
	

	private String getLogPlaceLabel(int pred, int succ) {
		return MOVEONLOGONLY + "i:" + pred + "|o:" + succ;
	}

	// available move considered (match with GUI)
	private static String MOVEONLOGONLY = "[L]";
	private static String MOVEONMODELONLYINVI = "[Mi]";
	private static String MOVEONMODELONLYREAL = "[M]";
	//	@SuppressWarnings("unused")
	//	private static String MOVESYNCHRONIZEDVIOLATING = "S-"; // firing without taking any token
	//	@SuppressWarnings("unused")
	//	private static String MOVESYNCHRONIZEDVIOLATINGPARTIALLY = 4; // only take a part of token
	private static String MOVESYNCHRONIZED = "[S]";

	

	public static PlaceType getPlaceType(String label) {
		if (label.startsWith(String.valueOf(MOVEONMODELONLYREAL))) {
			return PlaceType.M;
		} else if (label.startsWith(String.valueOf(MOVEONLOGONLY))) {
			return PlaceType.L;
		}
		return null;
	}

	private String getModelPlaceLabel(String label) {
		return MOVEONMODELONLYREAL + label;
	}

	private String getTransitionLabel(Transition origTrans) {
		try {
			if (origTrans.isInvisible()) {
				return MOVEONMODELONLYINVI + origTrans.getLabel();
			} else {
				return MOVEONMODELONLYREAL + origTrans.getLabel();
			}
			//			else {
			//				throw new UnsupportedAttributeTypeException("Step type [" + type + "] not supported");
			//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	private String getTransitionLabel(Integer eventIndex, XEvent evt, StepTypes type) {
		if (type.equals(StepTypes.L)) {
			return MOVEONLOGONLY + classes.getClassOf(evt).toString() + "-(" + eventIndex + ")";
		} else {//if(type.equals(StepTypes.LMGOOD)){
			return MOVESYNCHRONIZED + classes.getClassOf(evt).toString() + "-(" + eventIndex + ")";
		}
	}

	public static StepTypes getTransitionType(String label) {
		if (label.startsWith(String.valueOf(MOVEONMODELONLYINVI))) {
			return StepTypes.MINVI;
		} else if (label.startsWith(String.valueOf(MOVEONMODELONLYREAL))) {
			return StepTypes.MREAL;
		} else if (label.startsWith(String.valueOf(MOVEONLOGONLY))) {
			return StepTypes.L;
		} else if (label.startsWith(String.valueOf(MOVESYNCHRONIZED))) {
			return StepTypes.LMGOOD;
		} // else  
		return null;
	}

	private static Map<XEventClass, List<Transition>> getEncodedEventMapping(TransEvClassMapping mapping) {
		Map<XEventClass, List<Transition>> res = new HashMap<XEventClass, List<Transition>>();

		for (Transition transition : mapping.keySet()) {
			XEventClass event = mapping.get(transition);
			if (res.containsKey(event)) {
				res.get(event).add(transition);
			} else {
				// create new list
				List<Transition> listShort = new LinkedList<Transition>();
				listShort.add(transition);
				res.put(event, listShort);
			}
		}
		return res;
	}


	public Petrinet getPetrinetModel() {
		return this.syncModel;
	}

	public Marking getInitialMarking() {
		return this.initMarking;
	}
	
	public Marking[] getFinalMarkings() {
		return finalMarkings;
	}

	public Transition getTransition(Object obj, Marking currMarking, StepTypes stepType, int eventIndex) {
		Transition res = null;
		LoopTrans: for (Transition t : syncModel.getTransitions()) {
			if (compareLabel(t, obj, stepType, eventIndex)) {
				for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> in : syncModel.getInEdges(t)) {
					Place p = (Place) in.getSource();
					if (!currMarking.contains(p)) {
						continue LoopTrans;
					}
				}
				res = t;
			}
		}
		return res;
	}

	private boolean compareLabel(Transition t, Object obj, StepTypes stepType, int eventIndex) {
		if (stepType.equals(StepTypes.MINVI) || stepType.equals(StepTypes.MREAL)) {
			return t.getLabel().equals(getTransitionLabel((Transition) obj));
		} else {
			return t.getLabel().equals(getTransitionLabel(eventIndex, (XEvent) obj, stepType));
		}
	}

	public static boolean isSyncMove(String label) {
		return label.startsWith(MOVESYNCHRONIZED) || label.contains(MOVESYNCHRONIZED);
	}

	public static boolean isModelMove(String label) {
		return  label.startsWith(MOVEONMODELONLYREAL)|| label.contains(MOVEONMODELONLYREAL);
	}
	
	public static boolean isSilentModelMove(String label) {
		return  label.startsWith(MOVEONMODELONLYINVI)|| label.contains(MOVEONMODELONLYINVI);
	}

	public static boolean isLogMove(String label) {
		return label.startsWith(MOVEONLOGONLY)|| label.contains(MOVEONLOGONLY);
	}

	public static int getIndex(String label) {
		int i = label.lastIndexOf("-(");
		i+= 2;
		int j = label.lastIndexOf(")");
		int index = Integer.parseInt(label.substring(i, j));
		return index;
	}

}
