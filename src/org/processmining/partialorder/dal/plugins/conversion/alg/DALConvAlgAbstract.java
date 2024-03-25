package org.processmining.partialorder.dal.plugins.conversion.alg;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XIdentityExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XSemanticExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XAttribute;
import org.processmining.partialorder.dal.plugins.conversion.DALConversionAlg;

public abstract class DALConvAlgAbstract implements DALConversionAlg {

	static public boolean isBasicAttribute(XAttribute value) {		
		return isBasicAttribute(value.getKey());
	}
	static public boolean isBasicAttribute(String attributeKey) {
		if (XConceptExtension.KEY_NAME.equals(attributeKey)) {
			return true;
		} else if (XConceptExtension.KEY_INSTANCE.equals(attributeKey)) {
			return true;
		} else if (XIdentityExtension.KEY_ID.equals(attributeKey)) {
			return true;
		} else if (XLifecycleExtension.KEY_MODEL.equals(attributeKey)) {
			return true;
		} else if (XLifecycleExtension.KEY_TRANSITION.equals(attributeKey)) {
			return true;
		} else if (XOrganizationalExtension.KEY_GROUP.equals(attributeKey)) {
			return true;
		} else if (XOrganizationalExtension.KEY_RESOURCE.equals(attributeKey)) {
			return true;
		} else if (XOrganizationalExtension.KEY_ROLE.equals(attributeKey)) {
			return true;
		} else if (XSemanticExtension.KEY_MODELREFERENCE.equals(attributeKey)) {
			return true;
		} else if (XTimeExtension.KEY_TIMESTAMP.equals(attributeKey)) {
			return true;
		}
		return false;
	}

}
