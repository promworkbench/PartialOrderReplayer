package org.processmining.partialorder.dal.plugins.conversion;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginCategory;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.partialorder.dal.param.DALConversionParameters;
import org.processmining.partialorder.dal.param.DALParameterFactory;

/**
 * 
 * @author Xixi Lu (x.lu@tue.nl)
 * 
 */

@Plugin(name = "Convert to Data Anotated Log", 
	parameterLabels = { "Log", "Parameters" }, returnLabels = { "Log" }, 
			categories = { PluginCategory.Analytics }, 
			keywords = {"Data aware", "Access log", "Log preprocessing", "Log conversion"}, 
			help = "This plugin converts a log with data attributes in to a standardized data annotated log. "
					+ "The data attributes will be classified as input or output which are used for building partially ordered traces or partial aware conformance checking. ",
			returnTypes = { XLog.class })
public class DALConversionPlugin {

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Xixi Lu", email = "x.lu@tue.nl", pack = "PartialOrderReplayer")
	@PluginVariant(variantLabel = "Convert to DAL, default", requiredParameterLabels = { 0 })
	public XLog convertDefault(UIPluginContext context, XLog log) {
		DALConversionParameters param = DALParameterFactory.getParameterViaGui(context);
		return convertLogic(log, param);
	}

//	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "X. Lu", email = "x.lu@tue.nl")
//	@PluginVariant(variantLabel = "Convert to DAL, default", requiredParameterLabels = { 0 })
	public XLog convertDefault(PluginContext context, XLog log) {
		DALConversionParameters param = DALParameterFactory.getParameter();
		return convertLogic(log, param);
	}

	public XLog convertLogic(XLog log, DALConversionParameters param){
		DALConversionAlg alg = param.getConversionAlgorithm();
		return alg.convert(log, param);
	}

}
