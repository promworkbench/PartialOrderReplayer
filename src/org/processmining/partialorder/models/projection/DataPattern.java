package org.processmining.partialorder.models.projection;

import java.util.HashMap;
import java.util.Map;

import org.deckfour.xes.model.XAttribute;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.partialorder.dal.models.EnumDataAccessType;

/**
 * A data pattern can be regarded as one edge (in the projection on process model) 
 * between a transition (or a log move) and a data attribute indicating
 * an access to a data element observed in the log. The direction of the edge depends on the access type (I/O). 
 * <p>
 * This class stores all information related to one data access pattern
 * including (1) the attribute key, (2) the transition or the log move pattern which caused the data access,
 * (3) the type (I/O) of the access, and (4) a map of all relevant trace indices and event indices to the XAttribute. 
 * </p>
 * @author xlu
 *
 */
public class DataPattern {
	
	protected String dataKey;			// the key of the data attribute
	protected Transition transition;	// Either transition is stored 
	protected LogMovePattern pattern; // or a log move pattern is stored
							// to which the data pattern is associated with.
	protected EnumDataAccessType type; // the type of access: either I or O.
	
	/*
	 * Store a map: for each trace, for each event of the trace, the relevant attribute;
	 */
	protected Map<Integer, Map<Integer, XAttribute>> mapTrace2Event2Attr;
	//Map<Integer, XAttribute> mapEventToAttr;
	
	
	/**
	 * 
	 * @return A log move pattern if this data access is performed by a log move
	 * 			else null;
	 */
	public LogMovePattern getPattern() {
		return pattern;
	}

	public void setPattern(LogMovePattern pattern) {
		this.pattern = pattern;
	}
	
	/**
	 * 
	 * @return A map that given a trace, an event of the trace, returns the relevant attribute;
	 */
	public Map<Integer, Map<Integer, XAttribute>> getMapTraceToEventToAttr() {
		return mapTrace2Event2Attr;
	}

	public void setMapTraceToEventToAttr(Map<Integer, Map<Integer, XAttribute>> mapTrace2Event2Attrs) {
		this.mapTrace2Event2Attr = mapTrace2Event2Attrs;
	}
	
	/**
	 * 
	 * @return The number of traces that has the data pattern
	 */
	public int getNumberTraces(){
		return mapTrace2Event2Attr.keySet().size();
	}
	
	/**
	 * 
	 * @return The total number of events that has the data pattern.
	 */
	public int getNumberEvents(){
		int count = 0;
		for(Map<Integer, XAttribute> entry : mapTrace2Event2Attr.values()){
			count += entry.keySet().size();
		}
		return count;
	}

	/**
	 * Construct a data pattern that is associated with a transition
	 * @param key Data attribute key that is accessed
	 * @param t Transition t that accessed the data
	 * @param i	Access type i that is either I (for input) or O (for output)
	 */
	public DataPattern(String key, Transition t, EnumDataAccessType i) {
		this.dataKey = key;
		this.transition = t;
		this.type = i;
		mapTrace2Event2Attr = new HashMap<Integer, Map<Integer,XAttribute>>();
		
		
	}

	/**
	 * Construct a data pattern that is associated with a log move pattern
	 * @param key Data attribute key that is accessed
	 * @param lmp The log move pattern that accessed the data
	 * @param i	Access type i that is either I (for input) or O (for output)
	 */
	public DataPattern(String key, LogMovePattern lmp, EnumDataAccessType i) {
		this.dataKey = key;
		this.pattern = lmp;
		this.type = i;
		mapTrace2Event2Attr = new HashMap<Integer, Map<Integer,XAttribute>>();
	}

	public String getDataKey() {
		return dataKey;
	}

	public void setDataKey(String dataKey) {
		this.dataKey = dataKey;
	}

	/**
	 * 
	 * @return  The relevant transition of the original model of this data pattern,
	 * 			else null (which means this data pattern associated with a log move pattern
	 */
	public Transition getTransition() {
		return transition;
	}

	public void setTransition(Transition transition) {
		this.transition = transition;
	}

	/**
	 * 
	 * @return The access type of this data pattern 
	 * that is either I (for input) or O (for output)
	 */
	public EnumDataAccessType getType() {
		return type;
	}

	/**
	 * 
	 * @param type The access type that is either I (for input) or O (for output)
	 */
	public void setType(EnumDataAccessType type) {
		this.type = type;
	}

	/**
	 * Add a data attribute accessed by event to this data pattern
	 * @param trace The index of trace that 
	 * @param event
	 * @param a
	 */
	public void addAttribute(int trace, int event, XAttribute a) {
		if(!a.getKey().equals(this.dataKey)){
			throw new IllegalArgumentException("Attribute " + a.getKey() 
					+ " does not match the data pattern key " + this.dataKey);
		}
		if(!mapTrace2Event2Attr.containsKey(trace)){
			mapTrace2Event2Attr.put(trace, new HashMap<Integer, XAttribute>());
		}
		mapTrace2Event2Attr.get(trace).put(event, a);
		
	}

	/**
	 * Equal keys, DataAccessType, and (Transition or pattern)
	 */
	public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof DataPattern))
            return false;

        DataPattern dp = (DataPattern) obj;
        
        return this.dataKey.equals(dp.dataKey) &&
        		this.type.equals(dp.type) &&
        		((this.transition == null && dp.transition == null) || this.transition.equals(dp.transition)) &&
        		((this.pattern == null && dp.pattern == null) || this.pattern.equals(dp.pattern)) ;
    }


}
