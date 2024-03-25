package org.processmining.partialorder.models.replay;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.util.List;

import org.processmining.partialorder.models.graph.PartialOrderGraph;
import org.processmining.partialorder.models.palignment.PAlignment;
import org.processmining.partialorder.ptrace.model.PTrace;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

/**
 * Extended class of SyncReplayResult to include information that is needed to compute
 * partially ordered alignment
 * <ul>
 * <li>Added: a list "indices" from a move in alignment to the corresponding index in the trace.</li>
 * <li>Added: the alignment graph of {@link PartialOrderGraph}</li>
 * <li>Added: the unfolded petrinet of {@link POAlignmtUnfoldedNet}</li>
 * <li>Added: the synchronous product of {@link SyncProductModel}</li>
 * <li>Added: the partially ordered trace {@link PTrace}</li>
 * </ul>
 * @author xlu
 *
 */
public class POSyncReplayResult extends SyncReplayResult {
	private TIntList indeces;
	
	private PAlignment graph;
	private POAlignmtUnfoldedNet unfoldednet;
	private SyncProductModel syncModel;
	private PTrace potrace;

	public POSyncReplayResult(List<Object> nodeInstance, List<StepTypes> stepTypes, int traceIndex, TIntList indeces) {
		super(nodeInstance, stepTypes, traceIndex);
		this.setIndeces(indeces == null? new TIntArrayList() : indeces);
	}

	/**
	 * 
	 * @return a list "indeces" of int, with size of the alignment. 
	 * indeces[i] >= 0 gives the corresponding index of the move in the original trace.
	 * indeces[i] < 0 indicates a model move. 
	 */
	public TIntList getIndeces() {
		return indeces;
	}

	public void setIndeces(TIntList indeces) {
		this.indeces = indeces;
	}
		
	public PAlignment getPOAlignmentGraph() {
		return graph;
	}

	public void setGraph(PAlignment graph) {
		this.graph = graph;
	}

	public POAlignmtUnfoldedNet getUnfoldednet() {
		return unfoldednet;
	}

	public void setUnfoldednet(POAlignmtUnfoldedNet unfoldednet) {
		this.unfoldednet = unfoldednet;
	}

	public SyncProductModel getSyncModel() {
		return syncModel;
	}

	public void setSyncModel(SyncProductModel syncModel) {
		this.syncModel = syncModel;
	}

	public PTrace getPotrace() {
		return potrace;
	}

	public void setPotrace(PTrace potrace) {
		this.potrace = potrace;
	}




	

}
