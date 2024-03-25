package org.processmining.partialorder.models.dependency;

import java.util.ArrayList;
import java.util.List;

import org.deckfour.xes.model.XAttribute;

/**
 * The DataAwareRelation stores the preceding relation between two events in the partially ordered trace
 * 
 * @author xlu
 *
 */
public class PDependencyDataAware extends PDependencyImp {

	
	/*
	 * 
	 * a list of object[4] in which
	 * 		object[0] = attribute key
	 * 		object[1] = source XAttribute
	 * 		object[2] = target XAttribute
	 * 		object[3] = EnumDataDependency
	 */
	protected List<Object[]> dataDependencyValues;
	
	/**
	 * EnumDataDependency: Eight cases of different data dependencies
	 * e.g. II_SameValue : predecessor has input attribute which the successor also has, 
	 * 					   and both values have the same value
	 */
	public enum EnumDataDependency {
		II_SameValue, II_DiffValue, 
		IO_SameValue, IO_DiffValue, 
		OI_SameValue, OI_DiffValue, 
		OO_SameValue, OO_DiffValue, 
	}
	
	
	
	public PDependencyDataAware(int predIndex, int curIndex){
		super(predIndex, curIndex);
		init();
	}
	
	private void init() {
//		dataDependencies = new HashMap<EnumDataDependency, Set<String>>();		
//		for (EnumDataDependency v : EnumDataDependency.values()) {
//			dataDependencies.put(v, new HashSet<String>());
//		}	
		dataDependencyValues = new ArrayList<Object[]>();
	}	
	
	/**
	 * @return a list of object[4] in which
	 * 		object[0] = attribute key (String)
	 * 		object[1] = source XAttribute
	 * 		object[2] = target XAttribute
	 * 		object[3] = EnumDataDependency
	 */
	public List<Object[]> getDataDependencyValues(){
		return dataDependencyValues;
	}
	
	public void setDataDependencies(List<Object[]> dataDependencyValues) {
		this.dataDependencyValues = dataDependencyValues;
	}

	
	public void putDependency(EnumDataDependency type, String key, XAttribute source, XAttribute target){
//		dataDependencies.get(type).add(key);
		dataDependencyValues.add(new Object[]{key, source, target, type});
		
	}
	
	public boolean hasDepedency(){
//		for(Set<String> keys : dataDependencies.values()){
//			if (keys.size() > 0){
//				return true;
//			}
//		}
		return dataDependencyValues.size() > 0;
	}



}
