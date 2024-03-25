package org.processmining.partialorder.models.palignment;

import org.processmining.partialorder.models.dependency.PDependency;
import org.processmining.partialorder.models.palignment.dependency.MoveDependencyLogImp;
import org.processmining.partialorder.models.palignment.dependency.MoveDependencyModel;
import org.processmining.partialorder.models.palignment.dependency.MoveDependencySync;
import org.processmining.partialorder.models.replay.SyncProductModel.PlaceType;


public class MoveDependencyFactory {

	public static MoveDependency updateDependency(PlaceType placeType, Move snode, Move tnode, MoveDependency edge, PDependency r) {
		MoveDependency newEdge = null;
		if(placeType.equals(PlaceType.M)){
			if(edge == null){
				newEdge =  new MoveDependencyModel(snode, tnode);
			} else {
				newEdge = new MoveDependencySync(snode, tnode);
			}
		} 
		if (placeType.equals(PlaceType.L)) {			
			if(edge == null){
				newEdge = new MoveDependencyLogImp(snode, tnode, r);
			} else {
				newEdge = new MoveDependencySync(snode, tnode, r);
			}		
		}
		return newEdge;
	}

}
