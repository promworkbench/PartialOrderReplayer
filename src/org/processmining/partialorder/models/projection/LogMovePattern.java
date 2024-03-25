package org.processmining.partialorder.models.projection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

/**
 * A log move pattern can regarded as a log move transition aggregated from all identical log moves
 *  (projected on process model) between its preset (preceding transitions) and its post set (succeeding transitions).
 * <p>
 * This class stores all information related to one log move pattern including
 * <ul>
 * <li>(1) the event class (according to the replayer) of the log move,</li> 
 * <li>(2) the preset of transition and </li> 
 * <li>(3) the postset of transitions (both only model transitions), </li>
 * <li>(4) A list of data patterns that is associated 
 * with this log move pattern, and </li>
 * <li>(5) a map of all relevant trace indices to the event indices.</li>
 * </ul>
 * @author xlu
 *
 */
public class LogMovePattern {
	private XEventClass logMoveClass;
	private Set<Transition> preset;
	private Set<Transition> postset;
	private Map<Integer, Set<Integer>> mapTrace2Evts;
	//private Set<Integer> eventIds;
	
	private List<DataPattern> dataPatterns;
	
	public LogMovePattern(XEventClass eclass, Set<Transition> preset, Set<Transition> postset ){
		this.logMoveClass = eclass;
		this.preset = preset;
		this.postset = postset;
		mapTrace2Evts = new HashMap<Integer, Set<Integer>>();
		//eventIds= new HashSet<Integer>();
		dataPatterns = new ArrayList<DataPattern>();
	}
	
	/**
	 * Two log pattern are equal if the event class, the preset and the postset are equal. 
	 */
	public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof LogMovePattern))
            return false;

        LogMovePattern lm = (LogMovePattern) obj;
        return this.logMoveClass.equals(lm.logMoveClass) &&
        		this.preset.equals(lm.preset) &&
        		this.postset.equals(lm.postset);
    }

	public XEventClass getLogMoveClass() {
		return logMoveClass;
	}
	
	public String getLogMoveClassString() {
		return logMoveClass.toString();
	}

	public void setLogMoveClass(XEventClass logMoveClass) {
		this.logMoveClass = logMoveClass;
	}

	public Set<Transition> getPreset() {
		return preset;
	}

	public void setPreset(Set<Transition> preset) {
		this.preset = preset;
	}

	public Set<Transition> getPostset() {
		return postset;
	}

	public void setPostset(Set<Transition> postset) {
		this.postset = postset;
	}

	public void addTraceAndEvent(int trace, int eventIndex) {
		if(!mapTrace2Evts.containsKey(trace)){
			mapTrace2Evts.put(trace, new HashSet<Integer>());
		}
		this.mapTrace2Evts.get(trace).add(eventIndex);
		
	}

//	public void addEventIndex() {
//		this.eventIds.add(eventIndex);
//		
//	}

	public int getNumberOfTraceIds() {
		
		return mapTrace2Evts.keySet().size();
	}
	
	public int getNumberOfEvents(){
		int count = 0;
		for(Set<Integer> v : mapTrace2Evts.values()){
			count+= v.size();
		}
		return count;
	}

	public List<DataPattern> getDataPatterns() {
		
		return this.dataPatterns;
	}

	public void addDataPattern(DataPattern pattern) {
		dataPatterns.add(pattern);
		
	}
}
