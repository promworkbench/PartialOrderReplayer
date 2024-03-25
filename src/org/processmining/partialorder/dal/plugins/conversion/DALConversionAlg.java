package org.processmining.partialorder.dal.plugins.conversion;

import org.deckfour.xes.model.XLog;
import org.processmining.partialorder.dal.param.DALConversionParameters;

public interface DALConversionAlg {

	/**
	 * 
	 * @param log
	 * @param param
	 * @return
	 */
	public XLog convert(XLog log, DALConversionParameters param);

}
