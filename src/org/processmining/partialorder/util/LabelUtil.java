package org.processmining.partialorder.util;


import org.processmining.partialorder.dal.models.EnumDataAccessType;

public class LabelUtil {
	public static boolean isEmptyLabel(String label) {
		return label == null || label.trim().equals("");
	}

	public static String createTransitionLabel(int i) {
		return "tr" + i;
	}

	public static String createStartTLabel(String translabel) {
		return translabel + "_s";
	}

	public static String createCompleteTLabel(String translabel) {
		return translabel + "_c";
	}

	public static String createTPlaceLabel(String tlabel) {
		return "pm_" + tlabel;
	}

	public static String createShortDataAccessTLabel(EnumDataAccessType type, String data, String role) {
		return  type + "_" + data.substring(0, 1) + "_" + role.substring(0, 1);
	}

	public static String createDataAccessTLabel(EnumDataAccessType type, String data, String role) {

		return type + "_" + data + "_" + role;
	}
}
