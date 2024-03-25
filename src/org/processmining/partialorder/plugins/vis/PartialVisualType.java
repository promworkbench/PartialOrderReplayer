package org.processmining.partialorder.plugins.vis;

public enum PartialVisualType {
	ALIGNMENT_MINIMAL, 
	NO_PARALLEL_MINIMAL,
	MINIMAL_REDUTION, 
	AS_IS, MAXIMAL_CLOSURE; 
	
	public static PartialVisualType[] toPTraceVisualTypes() {
		return new PartialVisualType[]{MINIMAL_REDUTION, AS_IS, MAXIMAL_CLOSURE};
	}
	
	public static PartialVisualType[] toPAlignmentVisualTypes() {
		return new PartialVisualType[]{ALIGNMENT_MINIMAL, MINIMAL_REDUTION, AS_IS, MAXIMAL_CLOSURE};
	}
	
	public static PartialVisualType[] toComparableGraphVisualType() {
		return new PartialVisualType[]{NO_PARALLEL_MINIMAL, AS_IS};
	}
}

