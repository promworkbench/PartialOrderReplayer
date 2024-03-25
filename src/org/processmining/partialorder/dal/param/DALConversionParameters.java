package org.processmining.partialorder.dal.param;

import java.util.Map;

import org.deckfour.xes.classification.XEventAndClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.processmining.partialorder.dal.models.EnumDataAccessType;
import org.processmining.partialorder.dal.plugins.conversion.DALConversionAlg;
import org.processmining.partialorder.dal.plugins.conversion.alg.DALConvFreqHeuristicAlgImp;

public class DALConversionParameters {

	// @isAttrOfStartInput = true indicates that non basic attributes of a start event 
	// are automatically considered to be input attributes
	private boolean isAttrOfStartInput = false;
	private int ratio = 20;
	private DALConversionAlg alg;
	private XEventClassifier classifier;
	Map<String, Map<String, EnumDataAccessType>> mapEvent2DataKey2IO;
	
	
	public DALConversionParameters(){
		setAttrOfStartInput(true);	
		classifier = new XEventAndClassifier(new XEventNameClassifier());
		setConversionAlgorithm(new DALConvFreqHeuristicAlgImp());
	}

	public boolean isAttrOfStartInput() {
		return isAttrOfStartInput;
	}

	public void setAttrOfStartInput(boolean isAttrOfStartInput) {
		this.isAttrOfStartInput = isAttrOfStartInput;
	}


	public Map<String, Map<String, EnumDataAccessType>> getMapEvent2DataKey2IO() {
		return mapEvent2DataKey2IO;
	}

	public void setMapEvent2DataKey2IO(Map<String, Map<String, EnumDataAccessType>> mapEvent2DataKey2IO) {
		this.mapEvent2DataKey2IO = mapEvent2DataKey2IO;
	}
	

	public void setClassifier(XEventClassifier classifier) {
		if (classifier != null) {
			this.classifier = classifier;
		}
	}
	public XEventClassifier getClassifier() {
		return classifier;
	}

	public int getRatio() {
		return ratio;
	}
	
	public void setRatio(int ratio) {
		this.ratio = ratio;
	}

	public DALConversionAlg getConversionAlgorithm() {
		return alg;
	}

	public void setConversionAlgorithm(DALConversionAlg alg) {
		this.alg = alg;
	}
}
