package org.processmining.partialorder.models.palignment;

import java.util.Collection;

public interface PAlignment {

	public Collection<Move> getMoves();

	public Collection<Move> getLogMoves();

	public Collection<Move> getModelMoves();

	public Collection<Move> getRealModelMoves();
	public Collection<Move> getSilentModelMoves();
	public Collection<Move> getSyncMoves();
	public Collection<MoveDependency> getDependencies();
	public Collection<MoveDependency> getSyncDependencies();
	public Collection<MoveDependency> getModelDependencies();
	public Collection<MoveDependency> getLogDependencies();
	public Collection<MoveDependency> getDirectDependencies();
	public void addMove(Move move);
	public MoveDependency getDependency(Move predessor, Move successor);
	public void addDependency(MoveDependency depedency);
	public int getTraceIndex();
	public Collection<Move> getDirectParents(Move node);
	public Collection<Move> getDirectChildren(Move node);
	public void removeDependency(MoveDependency edge);
	public void setTraceIndex(int traceIndex);
}
