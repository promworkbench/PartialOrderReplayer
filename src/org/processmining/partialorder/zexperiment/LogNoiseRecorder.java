package org.processmining.partialorder.zexperiment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.id.XID;
import org.deckfour.xes.id.XIDFactory;
import org.deckfour.xes.model.XEvent;
import org.processmining.partialorder.models.dependency.Dependency;
import org.processmining.partialorder.models.dependency.DependencyImp;

public class LogNoiseRecorder {
	/*
	 * maps containing the noise Type: trace index -> a list of MoveNoise
	 * introduced in the trace
	 */
	Map<Integer, List<MoveRemoved>> removed;
	Map<Integer, List<MoveAdded>> added;
	Map<Integer, List<Dependency<XEvent>>> depsAdded;

	Set<XID> usedEventIds;

	public LogNoiseRecorder() {
		removed = new HashMap<Integer, List<MoveRemoved>>();
		added = new HashMap<Integer, List<MoveAdded>>();
		usedEventIds = new HashSet<XID>();
		depsAdded = new HashMap<Integer, List<Dependency<XEvent>>>();
	}

	public void addRemovedEvent(int traceIndex, XEvent e) {
		MoveRemoved m = new MoveRemoved(e);

		if (!removed.containsKey(traceIndex)) {
			removed.put(traceIndex, new ArrayList<MoveRemoved>());
		}
		removed.get(traceIndex).add(m);
	}

	public List<MoveRemoved> getRemoved(int trace) {
		if (removed.containsKey(trace)) {
			return new ArrayList<MoveRemoved>(removed.get(trace));
		}
		return new ArrayList<MoveRemoved>();
	}

	public List<MoveAdded> getAdded(int trace) {
		if (added.containsKey(trace)) {
			return new ArrayList<MoveAdded>(added.get(trace));
		}
		return new ArrayList<MoveAdded>();
	}

	public void addRemovedData(int i, int index, XEvent e, String dataKey) {
		// TODO Auto-generated method stub

	}

	public void addAddedEvent(int traceIndex, XEvent origEvent, XEvent newEvent) {
		MoveAdded m = new MoveAdded(origEvent, newEvent);

		if (!added.containsKey(traceIndex)) {
			added.put(traceIndex, new ArrayList<MoveAdded>());
		}
		added.get(traceIndex).add(m);
	}

	public XID generateNewIdAndStore() {
		XID id = XIDFactory.instance().createId();
		while (usedEventIds.contains(id)) {
			id = XIDFactory.instance().createId();
		}
		usedEventIds.add(id);
		return id;
	}

	public void addAddedDependency(int traceIndex, XEvent source, XEvent target) {
		Dependency<XEvent> dep = new DependencyImp<XEvent>(source, target);
		if (!depsAdded.containsKey(traceIndex)) {
			depsAdded.put(traceIndex, new ArrayList<Dependency<XEvent>>());
		}
		depsAdded.get(traceIndex).add(dep);
	}

	public List<Dependency<XEvent>> getAddedDependencies(int traceIndex) {
		if (!depsAdded.containsKey(traceIndex)) {
			return new ArrayList<Dependency<XEvent>>();
		}
		return depsAdded.get(traceIndex);
	}

	public boolean isAddedNoise(int i, XEvent origEvent) {
		if (origEvent == null) {
			return false;
		}

		if (added.containsKey(i)) {
			List<MoveAdded> noises = added.get(i);
			for (MoveAdded noise : noises) {
				if (noise.getNewEvent().equals(origEvent)) {
					return true;
				}
			}
		}
		return false;
	}

	public List<Dependency<XEvent>> getAddedDependencies(int traceIndex, MoveAdded added) {
		List<Dependency<XEvent>> results = new ArrayList<Dependency<XEvent>>();
		List<Dependency<XEvent>> deps = getAddedDependencies(traceIndex);
		for(Dependency<XEvent> dep : deps){
			if(dep.getSource().equals(added.getNewEvent()) 
					|| dep.getTarget().equals(added.getNewEvent())){
				results.add(dep);
			}
			
		}
		return results;
		
	}

	public void storeId(XID id) {
		usedEventIds.add(id);		
	}


}
