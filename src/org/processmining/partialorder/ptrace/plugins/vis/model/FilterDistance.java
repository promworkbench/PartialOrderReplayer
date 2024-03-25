package org.processmining.partialorder.ptrace.plugins.vis.model;


public class FilterDistance {
	
	public enum ComparisonType {
		Equal, Equal_and_LessThan, Equal_and_GreaterThan, Concurrent 
	}
	
	
	protected String predecessor;
	protected String successor;
	protected ComparisonType compareType;
	protected int dist;
	
	public String getPredecessor() {
		return predecessor;
	}
	public void setPredecessor(String predecessor) {
		this.predecessor = predecessor;
	}
	public String getSuccessor() {
		return successor;
	}
	public void setSuccessor(String successor) {
		this.successor = successor;
	}
	public ComparisonType getCompareType() {
		return compareType;
	}
	public void setCompareType(ComparisonType compareType) {
		this.compareType = compareType;
	}
	public int getDist() {
		return dist;
	}
	public void setDist(int dist) {
		this.dist = dist;
	}
	public void update(String pred, String succ, ComparisonType type, int dist) {
		setPredecessor(pred);
		setSuccessor(succ);
		setCompareType(type);
		setDist(dist);
	}
	
}
