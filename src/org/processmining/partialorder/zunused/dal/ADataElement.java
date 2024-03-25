package org.processmining.partialorder.zunused.dal;
//package org.processmining.partialorder.dal.models;
//
//
//
//
//
//public class ADataElement {
//	private final String varName;
////	private final Class type;
////	private final Comparable minValue;
////	private final Comparable maxValue;
//	
//	public ADataElement(String varName/*, Class type, Comparable minValue, Comparable maxValue*/) {
////		if (minValue!=null && !minValue.getClass().equals(type))
////			throw new IllegalArgumentException("The minimum value is incompatible with "+type);
////		if (maxValue!=null && !maxValue.getClass().equals(type))
////			throw new IllegalArgumentException("The maximum value is incompatible with "+type);		
////		if (maxValue!=null && minValue!=null && maxValue.compareTo(minValue)<0)
////			throw new IllegalArgumentException(maxValue+ "is smaller than "+minValue);
//		this.varName = varName;
////		this.minValue = minValue;
////		this.maxValue = maxValue;
////		this.type=type;
//	}
//	
//
//	
////	private String getToolTip()
////	{
////		String tooltip="<html><center><table><tr><td><b>Attribute type:</b></td></tr>";
////		if (minValue!=null)
////			tooltip+="<tr><td><b>Minimum Value:</b></td><td>"+minValue+"</td></tr>";
////		if (maxValue!=null)
////			tooltip+="<tr><td><b>Maximum Value:</b></td><td>"+maxValue+"</td></tr>";
////		tooltip+="</table></html>";	
////		return tooltip;
////	}
//	
//	public String toString()
//	{
//		return getVarName();
//	}
//	
//	public String getVarName() {
//		return varName;
//	}
//	
////	@SuppressWarnings("rawtypes")
////	public Comparable getMinValue() {
////		return minValue;
////	}
////	
////	@SuppressWarnings("rawtypes")
////	public Comparable getMaxValue() {
////		return maxValue;
////	}
////	
////	@SuppressWarnings("rawtypes")
////	public Class getType()
////	{
////		return type;
////	}
//
//	@Override
//	public int hashCode() {
//		return varName.hashCode();
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		ADataElement other = (ADataElement) obj;
//		return varName.equals(other.varName);
//	}
//
//	
//	
//}
//
