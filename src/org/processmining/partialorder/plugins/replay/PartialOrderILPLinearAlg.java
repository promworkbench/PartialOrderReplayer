package org.processmining.partialorder.plugins.replay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.annotation.PartialNetReplayAlgorithm;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.KeepInProMCache;
import org.processmining.models.graphbased.directed.petrinet.InhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.ResetNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.partialorder.models.palignment.PAlignment;
import org.processmining.partialorder.models.replay.POSyncReplayResult;
import org.processmining.partialorder.models.replay.PartialAwarePILPDelegate;
import org.processmining.partialorder.ptrace.model.PTrace;
import org.processmining.plugins.astar.petrinet.AbstractPetrinetReplayer;
import org.processmining.plugins.astar.petrinet.impl.AbstractPDelegate;
import org.processmining.plugins.astar.petrinet.impl.AbstractPILPDelegate;
import org.processmining.plugins.astar.petrinet.impl.PHead;
import org.processmining.plugins.astar.petrinet.impl.PILPTail;
import org.processmining.plugins.astar.petrinet.impl.PRecord;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;
import org.processmining.plugins.petrinet.replayer.annotations.PNReplayAlgorithm;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.PNRepResultImpl;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import nl.tue.astar.AStarException;
import nl.tue.astar.AStarThread;
import nl.tue.astar.AStarThread.Canceller;
import nl.tue.astar.ObservableAStarThread;
import nl.tue.astar.Trace;
import nl.tue.astar.impl.memefficient.MemoryEfficientAStarAlgorithm;
import nl.tue.astar.util.PartiallyOrderedTrace;

@KeepInProMCache
@PNReplayAlgorithm
@PartialNetReplayAlgorithm
public class PartialOrderILPLinearAlg extends AbstractPetrinetReplayer<PILPTail, PartialAwarePILPDelegate> {
	public boolean isComputePAlignmentLasy = false;

	@Override
	public PNRepResult replayLog(final PluginContext context, PetrinetGraph net, final XLog log,
			TransEvClassMapping mapping, final IPNReplayParameter parameters) throws AStarException {
		importParameters((CostBasedCompleteParam) parameters);
		classifier = mapping.getEventClassifier();

		if (parameters.isGUIMode()) {
			if (maxNumOfStates != Integer.MAX_VALUE) {
				context.log("Starting replay with max state " + maxNumOfStates + "...");
			} else {
				context.log("Starting replay with no limit for max explored state...");
			}
		}

		final XLogInfo summary = XLogInfoFactory.createLogInfo(log, classifier);
		final XEventClasses classes = summary.getEventClasses();

		final int delta = 1000;
		final int threads = 1;
		/*
		 * XXC: delegate store all model dependent informations.
		 */
		final PartialAwarePILPDelegate delegate = getDelegate(net, log, classes, mapping, delta, threads);

		final MemoryEfficientAStarAlgorithm<PHead, PILPTail> aStar = new MemoryEfficientAStarAlgorithm<PHead, PILPTail>(
				delegate);

		ExecutorService pool = Executors.newFixedThreadPool(threads);
		/*
		 * XXC: raw result of threads of astar algorithms
		 */
		final List<Future<Result>> result = new ArrayList<Future<Result>>();
		/*
		 * XXC: a map from original trace index i to the index of the trace that
		 * is the same as this trace i.
		 */
		final TIntIntMap doneMap = new TIntIntHashMap();

		long start = System.currentTimeMillis();

		if (context != null) {
			context.getProgress().setMaximum(log.size() + 1);
		}
		TObjectIntMap<Trace> traces = new TObjectIntHashMap<Trace>(log.size() / 2, 0.5f, -1);

		/*
		 * XXC: List of constructed replay result
		 */
		final List<SyncReplayResult> col = new ArrayList<SyncReplayResult>();

		try {
			// calculate first cost of empty trace

			// CPU EFFICIENT:
			//TObjectIntMap<PHead> head2int = new TObjectIntHashMap<PHead>(256 * 1024);
			//List<State<PHead, T>> stateList = new ArrayList<State<PHead, T>>(256 * 1024);

			int minCostMoveModel =
			//	getMinBoundMoveModel(parameters.getCanceller(), log, net, mapping, classes, delta,
			//	threads, aStar);
			getMinBoundMoveModel(parameters, delta, aStar, delegate);
			//int minCostMoveModel = 0; // AA: temporarily

			final Canceller canceller = parameters.getCanceller() == null ? new Canceller() {
				public boolean isCancelled() {
					if (context != null) {
						return context.getProgress().isCancelled();
					}
					return false;
				}
			} : parameters.getCanceller();

			int numberUniqueTrace = 0;
			for (int i = 0; i < log.size(); i++) {
				if (parameters.getCanceller() != null) {
					if (parameters.getCanceller().isCancelled()) {
						break;
					}
				}

				PHead initial = constructHead(delegate, initMarking, log.get(i));

				//usePartialOrderEvents = false;
				//				final Trace trace = usePartialOrderEvents ? getPartialOrderBuilder().getPartiallyOrderedTrace(log, i,
				//						delegate, null, null) : getLinearTrace(log, i, delegate, null, null);
				final Trace trace = getPartialOrderBuilder().getPartiallyOrderedTrace(log, i, delegate, null, null);
				int first = traces.get(trace);

				if (first >= 0) {
					doneMap.put(i, first);
					System.out.println(i + "/" + log.size() + "-is the same as " + first);
					continue;
				} else {
					traces.put(trace, i);
					System.out.println(i + "/" + log.size() + "-is a unique trace (" + first + ")");
					numberUniqueTrace++;
				}

				final ObservableAStarThread<PHead, PILPTail> thread;

				// MEMORY EFFICIENT
				thread = new AStarThread.MemoryEfficient<PHead, PILPTail>(aStar, initial, trace, maxNumOfStates);

				// CPU EFFICIENT: removed commented code + see Arya ILP replay

				final int j = i;

				result.add(pool.submit(new Callable<Result>() {

					public Result call() throws Exception {
						Result result = new Result();
						result.trace = j;
						result.filteredTrace = trace;

						// long start = System.nanoTime();
						long start = System.currentTimeMillis();
						result.record = (PRecord) thread.getOptimalRecord(canceller);

						//long end = System.nanoTime();
						long end = System.currentTimeMillis();
						result.reliable = thread.wasReliable();

						//uncomment to have all classes of optimal alignments
						//while (thread.wasReliable()) {
						//thread.getOptimalRecord(c, result.record.getTotalCost());
						//}

						if (context != null) {
							synchronized (context) {
								if (parameters.isGUIMode() && (j % 100 == 0)) {
									context.log(j + "/" + log.size() + " queueing " + thread.getQueuedStateCount()
											+ " states, visiting " + thread.getVisitedStateCount() + " states took "
											+ (end - start) + " seconds.");
								}
								context.getProgress().inc();

							}
						}
						visitedStates += thread.getVisitedStateCount();
						queuedStates += thread.getQueuedStateCount();
						traversedArcs += thread.getTraversedArcCount();

						result.queuedStates = thread.getQueuedStateCount();
						result.states = thread.getVisitedStateCount();
						result.milliseconds = end - start;

						// uncomment the following two lines if state space graph is printed
						//graphObserver.close();
						//treeObserver.close();

						return result;

					}
				}));

			}
			if (context != null) {
				context.getProgress().inc();
			}
			pool.shutdown();
			while (!pool.isTerminated()) {
				try {
					pool.awaitTermination(10, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
				}
			}

			if (delegate != null) {
				((AbstractPILPDelegate<?>) delegate).deleteLPs();
			}

			long maxStateCount = 0;
			long time = 0;
			//			long ui = System.currentTimeMillis();

			for (Future<Result> f : result) {
				Result r = null;
				try {
					while (r == null) {
						try {
							r = f.get();
						} catch (InterruptedException e) {
						}
					}
					XTrace trace = log.get(r.trace);
					Map<Integer, SyncReplayResult> resMap = null;
					/*
					 * For each result from astar, compute the (PO-)alignment
					 * and added to col
					 */
					int states = addPOReplayResults(net, delegate, classes, mapping, trace, r, doneMap, log, col,
							r.trace, minCostMoveModel, resMap);

					maxStateCount = Math.max(maxStateCount, states);
					time += r.milliseconds;
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
			long end = System.currentTimeMillis();
			// each PRecord uses 56 bytes in memory

			maxStateCount *= 56;
			if (parameters.isGUIMode()) {
				context.log("Total time : " + (end - start) / 1000.0 + " seconds");
				context.log("Time for A*: " + time / 1000.0 + " seconds");
				context.log("In total " + visitedStates + " unique states were visited.");
				context.log("In total " + traversedArcs + " arcs were traversed.");
				context.log("In total " + queuedStates + " states were queued.");
				//				context.log("In total " + aStar.getStatespace().size()
				//						+ " marking-parikhvector pairs were stored in the statespace.");
				//				context.log("In total " + aStar.getStatespace().getMemory() / (1024.0 * 1024.0)
				//						+ " MB were needed for the statespace.");
				context.log("At most " + maxStateCount / (1024.0 * 1024.0)
						+ " MB was needed for a trace (overestimate).");
				context.log("States / second:  " + visitedStates / (time / 1000.0));
				context.log("Traversed arcs / second:  " + traversedArcs / (time / 1000.0));
				context.log("Queued states / second:  " + queuedStates / (time / 1000.0));
				//				context.log("Storage / second: " + aStar.getStatespace().size() / ((ui - start) / 1000.0));
				//				context.log("EQUAL calls:" + PHeadCompressor.EQUALCALLS);
				//				context.log("EQUAL hash: " + PHeadCompressor.EQUALHASH);
				//				context.log("UNEQUAL:    " + PHeadCompressor.NONEQUAL);
			}

			synchronized (col) {
				//				if (outputStream != null) {
				//					outputStream.close();
				//				}
				PNRepResult pnResult = new PNRepResultImpl(col);
				System.out.println("Number of calls of astar : " + numberUniqueTrace);
				return pnResult;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;// debug code
	}

	/*
	 * version 1.2 - XX: changed based addReplayResults version 1.1 function: po
	 * trace -> po alignment
	 */
	//@Override
	protected int addPOReplayResults(PetrinetGraph net, PartialAwarePILPDelegate delegate, XEventClasses classes,
			TransEvClassMapping mapping, XTrace trace, Result r, TIntIntMap doneMap, XLog log,
			List<SyncReplayResult> col, int traceIndex, int minCostMoveModel, Map<Integer, SyncReplayResult> mapRes) {
		POSyncReplayResult posrr = recordToResult(delegate, trace, r.filteredTrace, r.record, traceIndex, r.states,
				r.reliable, r.milliseconds, r.queuedStates, minCostMoveModel);
		//		System.out.println("Building result of trace " + traceIndex + "/" + log.size());
		/*
		 * XXA: added to construct partially ordered trace
		 */
		if (!isComputePAlignmentLasy) {
			PTrace potrace = delegate.getOrgPartialXTrace(traceIndex);

			if (potrace.getEventIndices().size() != trace.size()) {
				try {
					throw new Exception("Events removed from ptraces (size : " + potrace.getEventIndices().size()
							+ ", org: " + trace.size() + ") ");
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				//				System.out.println("Trace " + traceIndex + " is good");
			}

			POAlignmentBuilder.computePAlignmentAndUpdateResult(net, this.initMarking, this.finalMarkings, classes,
					mapping, log.get(traceIndex), potrace, traceIndex, posrr);

			//REMOVE
			PAlignment alignment = posrr.getPOAlignmentGraph();
			if (posrr.getNodeInstance().size() != alignment.getMoves().size()) {
				try {
					throw new Exception("Moves removed");
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				//				System.out.println("Trace " + traceIndex + " is good");
			}

		}

		/*
		 * 
		 */

		// XXQ: why? mapRes is not returned,
		// XXC: because either added in srr, or new call of this and srr added to col. 
		if (mapRes == null) {
			mapRes = new HashMap<Integer, SyncReplayResult>(4);
		}
		mapRes.put(traceIndex, posrr); // XXC: filled with Original trace => Result. 

		boolean done = false;
		// XXC: doneMap from trace index to equal trace index, else not in doneMap
		forLoop: for (int key : doneMap.keys()) {
			if (doneMap.get(key) == r.trace) {//XXC: for keys in doneMap, if the equal trace index = the alignment trace index
				// This should only be done for similar traces.
				XTrace keyTrace = log.get(key);
				//PartiallyOrderedXTrace similarPOTrace = delegate.getOrgPartialXTrace(key);
				// check if trace == keyTrace
				/*
				 * XXC: the filtered trace log.get(key) = trace of which the
				 * alignment is calculated - mapRes (specific for each
				 * alignment): maps the Original trace => Result (with a
				 * collection of trace index that is equal to this unfiltered
				 * trace)
				 */

				for (Integer keyMapRes : mapRes.keySet()) {
					if (compareEventClassList(delegate, log.get(keyMapRes), keyTrace)) {
						//PartiallyOrderedXTrace doneSimilarPOTrace = delegate.getOrgPartialXTrace(keyMapRes);
						/*
						 * XXC: compare the original partially ordered traces
						 * instead of the linear traces. XXD: compare potraces
						 * => unable to get original event info when visualize.
						 */
						//if (doneSimilarPOTrace.getOriginalSortedPOrace().equals(similarPOTrace.getOriginalSortedPOrace())){
						mapRes.get(keyMapRes).addNewCase(key);
						doneMap.put(key, -2);
						continue forLoop;
					}
				}
				if (!done) {
					/*
					 * XXC: for all original traces, of which the filtered trace
					 * is equal and the alignment is calculated no original
					 * trace is to the trace log.get(key), thus create a new
					 * result.
					 */
					// Now they are not the same.
					addPOReplayResults(net, delegate, classes, mapping, keyTrace, r, doneMap, log, col, key,
							minCostMoveModel, mapRes);
					done = true;
				}
			}
		}
		col.add(posrr);

		return r.states;
	}

	/*
	 * version 1.1 - XX: changed based Arya code function: potrace -> linear
	 * alignment
	 */
	//	@Override
	//	protected int addReplayResults(PartialAwarePILPDelegate delegate, XTrace trace, Result r, TIntIntMap doneMap,
	//			XLog log, List<SyncReplayResult> col, int traceIndex, int minCostMoveModel,
	//			Map<Integer, SyncReplayResult> mapRes) {
	//		SyncReplayResult srr = recordToResult(delegate, trace, r.filteredTrace, r.record, traceIndex, r.states,
	//				r.reliable, r.milliseconds, r.queuedStates, minCostMoveModel);
	//
	//		// XXQ: why? mapRes is not returned,
	//		// XXC: because either added in srr, or new call of this and srr added to col. 
	//		if (mapRes == null) {
	//			mapRes = new HashMap<Integer, SyncReplayResult>(4);
	//		}
	//		mapRes.put(traceIndex, srr); // XXC: filled with Original trace => Result. 
	//
	//		boolean done = false;
	//		// XXC: doneMap from trace index to equal trace index, else not in doneMap
	//		forLoop: for (int key : doneMap.keys()) {
	//			if (doneMap.get(key) == r.trace) {//XXC: for keys in doneMap, if the equal trace index = the alignment trace index
	//				// This should only be done for similar traces.
	//				XTrace keyTrace = log.get(key);
	//				
	//				// check if trace == keyTrace
	//				/* XXC:
	//				 * the filtered trace log.get(key) = trace of which the alignment is calculated
	//				 * - mapRes (specific for each alignment): 
	//				 * 	maps the Original trace => Result (with a collection of trace index that is equal to this unfiltered trace)
	//				 */			
	//				for (Integer keyMapRes : mapRes.keySet()) {
	//					if (compareEventClassList(delegate, log.get(keyMapRes), keyTrace)) {
	//						mapRes.get(keyMapRes).addNewCase(key);
	//						doneMap.put(key, -2);
	//						continue forLoop;
	//					}
	//				}
	//				if (!done) { 
	//					/*
	//					 * XXC: for all original traces, of which the filtered trace is equal and the alignment is calculated
	//					 * 		no original trace is to the trace log.get(key), thus create a new result.
	//					 */
	//					// Now they are not the same.
	//					addReplayResults(delegate, keyTrace, r, doneMap, log, col, key, minCostMoveModel, mapRes);
	//					done = true;
	//				}
	//			}
	//		}
	//		col.add(srr);
	//		
	//		return r.states;
	//	}

	/*
	 * version 1.1 - XX: function: linear alignment -> po alignment
	 */
	//	private void addPOReplayResults(PetrinetGraph net, PartialAwarePILPDelegate delegate, XTrace trace,
	//			SyncReplayResult ssr, TIntIntMap doneMap, XLog log, int traceIndex, XEventClasses classes, TransEvClassMapping mapping) {
	//		
	//		
	//		PartiallyOrderedXTrace potrace =  delegate.getOrgPartialXTrace(traceIndex);
	//		
	//		POSyncReplayResult pores = (POSyncReplayResult) ssr;
	//		// XX: calculate the synchronized model and unfolding alignment
	//		Object[] result = POAlignmentBuilder.unfoldingPOAlignment(net, delegate.getOrgPartialXTrace(traceIndex), 
	//				traceIndex, pores, this.initMarking, this.finalMarkings, classes, mapping);
	//		SyncProductModel syncModel = (SyncProductModel) result[0];
	//		POAlignmtUnfoldedNet unfoldednet = (POAlignmtUnfoldedNet) result[1];
	//		
	//		// convert to a simple poalignment graph
	//		PartialOrderGraph graph = POAlignmentBuilder.convertUnfoldingToAligmtGraph(
	//				potrace, trace,	 traceIndex, pores, syncModel, unfoldednet);
	//		
	//		POReplayResult res = new POReplayResult(potrace, syncModel, unfoldednet, graph);
	//		res.addTrace(traceIndex);
	//		//poalignmentsobject.getPoalignments().put(traceIndex, graph);
	//		
	//	}

	//@Override
	protected POSyncReplayResult recordToResult(AbstractPDelegate<?> d, XTrace trace, Trace filteredTrace, PRecord r,
			int traceIndex, int stateCount, boolean isReliable, long milliseconds, int queuedStates,
			int minCostMoveModel) {

		List<PRecord> history = PRecord.getHistory(r);
		double mmCost = 0; // total cost of move on model
		double mlCost = 0; // total cost of move on log
		double mSyncCost = 0; // total cost of synchronous move

		double mmUpper = 0; // total cost if all movements are move on model (including the synchronous one)
		double mlUpper = 0; // total cost if all events are move on log

		int eventInTrace = -1;
		List<StepTypes> stepTypes = new ArrayList<StepTypes>(history.size()); // XXQ Each move in the alignment?
		List<Object> nodeInstance = new ArrayList<Object>(); // XXQ each transition in the alignment??
		TIntList eventIndeces = new TIntArrayList();

		// XXA .... distinguish linear trace and partially ordered trace
		// XXA added
		double[] resDouble = tryPartialAlignment(trace, traceIndex, filteredTrace, history, stepTypes, nodeInstance,
				eventIndeces, (PartialAwarePILPDelegate) d, mmCost, mmUpper, mlCost, mSyncCost);
		mmCost = resDouble[0];
		mmUpper = resDouble[1];
		mlCost = resDouble[2];
		mSyncCost = resDouble[3];
		eventInTrace = trace.size();

		//todo
		// calculate mlUpper (because in cases where we have synchronous move in manifest, more than one events are aggregated
		// in one movement
		for (XEvent evt : trace) {
			mlUpper += mapEvClass2Cost.get(d.getClassOf(evt));
		}

		// XX: added eventIndeces
		POSyncReplayResult res = new POSyncReplayResult(nodeInstance, stepTypes, traceIndex, eventIndeces);

		res.setReliable(isReliable);
		Map<String, Double> info = new HashMap<String, Double>();
		info.put(PNRepResult.RAWFITNESSCOST, (mmCost + mlCost + mSyncCost));

		if (mlCost > 0) {
			info.put(PNRepResult.MOVELOGFITNESS, 1 - (mlCost / mlUpper));
		} else {
			info.put(PNRepResult.MOVELOGFITNESS, 1.0);
		}

		if (mmCost > 0) {
			info.put(PNRepResult.MOVEMODELFITNESS, 1 - (mmCost / mmUpper));
		} else {
			info.put(PNRepResult.MOVEMODELFITNESS, 1.0);
		}
		info.put(PNRepResult.NUMSTATEGENERATED, (double) stateCount);
		info.put(PNRepResult.QUEUEDSTATE, (double) queuedStates);

		// set info fitness
		info.put(PNRepResult.TRACEFITNESS, 1 - ((mmCost + mlCost + mSyncCost) / (mlUpper + minCostMoveModel)));
		info.put(PNRepResult.TIME, (double) milliseconds);
		info.put(PNRepResult.ORIGTRACELENGTH, (double) eventInTrace);
		res.setInfo(info);
		return res;
	}

	// XX:
	private double[] tryPartialAlignment(XTrace trace, int traceIndex, Trace filteredTrace, List<PRecord> history,
			List<StepTypes> stepTypes, List<Object> nodeInstance, TIntList eventIndeces, PartialAwarePILPDelegate d,
			double mmCost, double mmUpper, double mlCost, double mSyncCost) {

		boolean[] doneEvents = new boolean[trace.size()]; // keep a list of original events whether each of them is added into alignment

		for (PRecord rec : history) {
			if (rec.getMovedEvent() == AStarThread.NOMOVE) {
				// move model only
				Transition t = d.getTransition((short) rec.getModelMove());
				if (t.isInvisible()) {
					stepTypes.add(StepTypes.MINVI);
				} else {
					stepTypes.add(StepTypes.MREAL);
				}
				nodeInstance.add(t);
				eventIndeces.add(-1);
				mmCost += (d.getCostForMoveModel((short) rec.getModelMove()) - 1.0) / d.getDelta();
				mmUpper += (d.getCostForMoveModel((short) rec.getModelMove()) - 1.0) / d.getDelta();
			} else {
				// a move occurred in the log. Check if class aligns with class in trace			

				short a = (short) filteredTrace.get(rec.getMovedEvent()); // a is the event obtained from the replay

				int orgEventIndex = d.getTraceOriginalEventIndex(traceIndex, rec.getMovedEvent()); // orgEventIndex if the original event index of a in the original trace

				if (doneEvents[orgEventIndex] == true) {
					//todo: strange: something wrong
				}

				// All ancestors of a (todo should be orgEventIndex) that is not inserted in the alignment should be inserted. 
				List<Integer> ancestors = new ArrayList<Integer>();

				PartiallyOrderedTrace partialOrgTrace = d.getOrgPartialTrace(traceIndex);
				getAncestorWithoutRecursions(ancestors, orgEventIndex, partialOrgTrace, doneEvents);
				for (Integer orgPreEventIndex : ancestors) {
					// for each ancestor, get the original event index
					//int orgPreEventIndex = d.getTraceOriginalEventIndex(traceIndex, filteredPreEventIndex);
					// check if this original event index is already inserted
					if (doneEvents[orgPreEventIndex] == true) {
						//nothing
					} else {
						stepTypes.add(StepTypes.L);
						nodeInstance.add(d.getClassOf(trace.get(orgPreEventIndex)));
						eventIndeces.add(orgPreEventIndex);
						mlCost += mapEvClass2Cost.get(d.getClassOf(trace.get(orgPreEventIndex)));

						doneEvents[orgPreEventIndex] = true;
					}
				}

				if (rec.getModelMove() == AStarThread.NOMOVE) {
					// move log only
					stepTypes.add(StepTypes.L);
					nodeInstance.add(d.getEventClass(a));
					eventIndeces.add(orgEventIndex);
					mlCost += (d.getCostForMoveLog(a) - 1.0) / d.getDelta();
					//					mlUpper += (d.getCostForMoveLog(a) - 1.0) / d.getDelta();
				} else {
					// sync move
					stepTypes.add(StepTypes.LMGOOD);
					nodeInstance.add(d.getTransition((short) rec.getModelMove()));
					eventIndeces.add(orgEventIndex);
					mSyncCost += (d.getCostForMoveSync((short) rec.getModelMove()) - 1.0) / d.getDelta();
					//					mlUpper += (d.getCostForMoveLog(a) - 1.0) / d.getDelta();
					mmUpper += (d.getCostForMoveModel((short) rec.getModelMove()) - 1.0) / d.getDelta();
				}

				doneEvents[orgEventIndex] = true;
			}

		}

		// XX: to be tested. don't know how... 
		// add the rest of the trace
		for (int i = 0; i < doneEvents.length; i++) {
			// move log only
			if (doneEvents[i] == false) {
				XEventClass a = d.getClassOf(trace.get(i));

				// check if all its predecessors are executed
				List<Integer> ancestors = new ArrayList<Integer>();

				PartiallyOrderedTrace partialOrgTrace = d.getOrgPartialTrace(traceIndex);
				getAncestors(ancestors, i, partialOrgTrace, doneEvents);
				for (Integer orgPreEventIndex : ancestors) {
					// for each ancestor, get the original event index
					//int orgPreEventIndex = d.getTraceOriginalEventIndex(traceIndex, filteredPreEventIndex);
					// check if this original event index is already inserted
					if (doneEvents[orgPreEventIndex] == true) {
						//nothing , something wrong with getAncestors function!!
					} else {
						stepTypes.add(StepTypes.L);
						nodeInstance.add(d.getClassOf(trace.get(orgPreEventIndex)));
						eventIndeces.add(orgPreEventIndex);
						mlCost += mapEvClass2Cost.get(d.getClassOf(trace.get(orgPreEventIndex)));

						doneEvents[orgPreEventIndex] = true;
					}
				}

				stepTypes.add(StepTypes.L);
				nodeInstance.add(a);
				eventIndeces.add(i);
				mlCost += mapEvClass2Cost.get(a);
				doneEvents[i] = true;
				//			mlUpper += mapEvClass2Cost.get(a);
			}

		}
		return new double[] { mmCost, mmUpper, mlCost, mSyncCost };
	}

	//XX: not used because recursion is very slow
	// When compare two ptraces, they may only overlap in one events, and the rest events have to be added 
	// recursively, which can be as long as the length of the trace thus 1000 or even 10000... Recursion get stuck.
	private void getAncestors(List<Integer> ancestors, int eventIndex, PartiallyOrderedTrace partialTrace,
			boolean[] doneEvents) {
		int[] pres = partialTrace.getPredecessors(eventIndex);
		if (pres != null) {
			// XX: the oldest ancestor according to dependencies should be executed first
			for (int i = pres.length - 1; i >= 0; i--) {

				if (doneEvents[pres[i]] == false) {
					// XX: each ancestor only executed once, and as early as it should
					if (ancestors.contains(pres[i])) {
						ancestors.remove((Object) pres[i]);
					}
					ancestors.add(0, pres[i]);
					getAncestors(ancestors, pres[i], partialTrace, doneEvents);
				}
			}
		}

	}

	private void getAncestorWithoutRecursions(List<Integer> ancestors, int eventIndex,
			PartiallyOrderedTrace partialTrace, boolean[] doneEvents) {
		int[] pres = partialTrace.getPredecessors(eventIndex);
		Queue<Integer> queue = new LinkedList<>();
		if (pres != null) {
			// XX: the oldest ancestor according to dependencies should be executed first
			for (int i = pres.length - 1; i >= 0; i--) {
				queue.add(pres[i]);
			}
			while (!queue.isEmpty()) {
				Integer predIndex = queue.poll();

				if (doneEvents[predIndex] == false) {
					// XX: each ancestor only executed once, and as early as it should
					if (ancestors.contains(predIndex)) {
						ancestors.remove(predIndex);
					}
					ancestors.add(0, predIndex);

					for (int predOfPred : partialTrace.getPredecessors(predIndex)) {
						if (queue.contains(predOfPred)) {
							// move to the end of the queue
							queue.remove(predOfPred);
						}
						queue.add(predOfPred);
					}
				}
			}
		}
	}

	protected PartialAwarePILPDelegate getDelegate(PetrinetGraph net, XLog log, XEventClasses classes,
			TransEvClassMapping mapping, int delta, int threads) {
		if (net instanceof ResetInhibitorNet) {
			return new PartialAwarePILPDelegate((ResetInhibitorNet) net, log, classes, mapping, mapTrans2Cost,
					mapEvClass2Cost, mapSync2Cost, delta, threads, finalMarkings);
		} else if (net instanceof ResetNet) {
			return new PartialAwarePILPDelegate((ResetNet) net, log, classes, mapping, mapTrans2Cost, mapEvClass2Cost,
					mapSync2Cost, delta, threads, finalMarkings);
		} else if (net instanceof InhibitorNet) {
			return new PartialAwarePILPDelegate((InhibitorNet) net, log, classes, mapping, mapTrans2Cost,
					mapEvClass2Cost, mapSync2Cost, delta, threads, finalMarkings);
		} else if (net instanceof Petrinet) {
			return new PartialAwarePILPDelegate((Petrinet) net, log, classes, mapping, mapTrans2Cost, mapEvClass2Cost,
					mapSync2Cost, delta, threads, finalMarkings);
		}

		return null;
	}

	public String toString() {
		return "A* Cost-based Fitness Express with ILP and Partial aware, assuming at most " + Short.MAX_VALUE
				+ " tokens in each place.";
	}

	public void setComputeLasy(boolean computeLasy) {
		isComputePAlignmentLasy = computeLasy;
	}

}
