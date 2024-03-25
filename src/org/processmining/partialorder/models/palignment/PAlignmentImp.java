package org.processmining.partialorder.models.palignment;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class PAlignmentImp extends DirectedSparseGraph<Move, MoveDependency> implements PAlignment {

	/**
	 * 
	 */
	private static final long serialVersionUID = -951550844799704022L;
	
	
	private int traceIndex = -1;

	enum MoveType {
		MoveLog, MoveModelInvisible, MoveModelVisible, MoveSync, MoveModel
	}
	
	
	public PAlignmentImp(String name, int traceIndex) {
		super();
		this.traceIndex = traceIndex;		
	}

	public Collection<Move> getMoves() {
		return this.getVertices();
	}

	public Collection<Move> getLogMoves() {
		return getMovesByType(MoveType.MoveLog);
	}

	public Collection<Move> getModelMoves() {
		return getMovesByType(MoveType.MoveModel);
	}

	public Collection<Move> getRealModelMoves() {
		return getMovesByType(MoveType.MoveModelVisible);
	}

	public Collection<Move> getSilentModelMoves() {
		return getMovesByType(MoveType.MoveModelInvisible);
	}

	public Collection<Move> getSyncMoves() {
		return getMovesByType(MoveType.MoveSync);
	}
	
	private Collection<Move> getMovesByType(MoveType type){
		Set<Move> moves = new HashSet<Move>();
		for(Move move : this.getVertices()){
			if(type.equals(MoveType.MoveLog) && move.isLogMove()){
				moves.add(move);
			} else if (type.equals(MoveType.MoveModelInvisible) && move.isSilentModelMove()){
				moves.add(move);
			} else if (type.equals(MoveType.MoveModelVisible) && move.isVisibleModelMove()){
				moves.add(move);
			} else if (type.equals(MoveType.MoveSync) && move.isSyncMove()){
				moves.add(move);
			} else if (type.equals(MoveType.MoveModel) && move.isModelMove()){
				moves.add(move);
			} 
		}
		return moves;
	}

	public Collection<MoveDependency> getDependencies() {
		return this.getEdges();
	}
	
	

	public Collection<MoveDependency> getSyncDependencies() {
		Set<MoveDependency> deps = new HashSet<MoveDependency>();
		for(MoveDependency dep : this.getEdges()){
			if(dep.isSyncDependency()){
				deps.add(dep);
			} 
		}
		return deps;
	}

	public Collection<MoveDependency> getModelDependencies() {
		Set<MoveDependency> deps = new HashSet<MoveDependency>();
		for(MoveDependency dep : this.getEdges()){
			if(dep.isModelDependency()){
				deps.add(dep);
			} 
		}
		return deps;
	}

	public Collection<MoveDependency> getLogDependencies() {
		Set<MoveDependency> deps = new HashSet<MoveDependency>();
		for(MoveDependency dep : this.getEdges()){
			if(dep.isLogDependency()){
				deps.add(dep);
			} 
		}
		return deps;
	}

	public Collection<MoveDependency> getDirectDependencies() {
		Set<MoveDependency> deps = new HashSet<MoveDependency>();
		for(MoveDependency dep : this.getEdges()){
			if(dep.isDirect()){
				deps.add(dep);
			} 
		}
		return deps;
	}

	public void addMove(Move move) {
		// REMOVE
		int count = this.vertices.size();
		this.addVertex(move);		
		assert count != this.vertices.size() + 1;
	}

	public MoveDependency getDependency(Move predessor, Move successor) {
		for(MoveDependency dep : this.getOutEdges(predessor)){
			if(dep.getTarget().equals(successor)){
				return dep;
			}
		}
		return null;
	}

	public void addDependency(MoveDependency depedency) {
		this.addEdge(depedency, depedency.getSource(), depedency.getTarget());
		
	}

	public int getTraceIndex() {
		return this.traceIndex;
	}

	public Collection<Move> getDirectParents(Move node) {
		Set<Move> parents = new HashSet<Move>();
		for(MoveDependency in : this.getInEdges(node)){
			parents.add(in.getSource());
		}
		return parents;
	}

	public Collection<Move> getDirectChildren(Move node) {
		Set<Move> children = new HashSet<Move>();
		for(MoveDependency out : this.getOutEdges(node)){
			children.add(out.getTarget());
		}
		return children;
	}

	public void removeDependency(MoveDependency edge) {
		this.removeEdge(edge);		
	}

	public void setTraceIndex(int traceIndex) {
		this.traceIndex = traceIndex;
	}

}
