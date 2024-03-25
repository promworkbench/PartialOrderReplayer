package org.processmining.partialorder.ptrace.param;

public class PTraceParameter {

	public enum PTraceType {
		Sequential_Dependency("Seq"), 
		Non_Equal_Timestamp_Dependency("TimePO"), 
		Non_Same_Day_Dependency("DayPO"), 
		Data_Dependency("DataPO"),
		PLog_Extension("PLogPO");

		private final String text;
		private PTraceType(final String text) {
			this.text = text;
		}

		@Override
		public String toString() {
			return text;
		}
	}

	private PTraceType type;
	private boolean doComputeReduction = true;

	public PTraceParameter() {
		type = PTraceType.Sequential_Dependency;
	}

	public PTraceParameter(PTraceType type) {
		//		this();
		this.type = type;
	}

	public PTraceType getType() {
		return type;
	}

	public void setType(PTraceType type) {
		this.type = type;
	}

	public boolean isComputingDependenciesBaseOnExtension(){
		return type.equals(PTraceType.PLog_Extension);
	}
	public boolean isComputingDataDependencies() {
		return type.equals(PTraceType.Data_Dependency);
	}

	public boolean isComputingSequentialTraces() {
		return type.equals(PTraceType.Sequential_Dependency);
	}

	public boolean isComputingTimeEqual() {
		return type.equals(PTraceType.Non_Equal_Timestamp_Dependency);
	}

	public boolean isComputingSameDayEqual() {
		return type.equals(PTraceType.Non_Same_Day_Dependency);
	}

	public boolean isComputeTransReduction() {
		return doComputeReduction;
	}

	public void setComputeTransitive(boolean b) {
		this.doComputeReduction = b;
	}

	public boolean isComputeRelation() {
		return false;
	}

	@Override
	public String toString() {
		return this.type.toString();
	}

}
