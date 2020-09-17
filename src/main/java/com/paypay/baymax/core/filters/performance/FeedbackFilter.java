package com.paypay.baymax.core.filters.performance;

import com.paypay.baymax.commons.type.FeedbackType;

public class FeedbackFilter {
	
	public static String validateRequest(FeedbackType type, Boolean isUpdate) {
		if (type == null) {
			return "";
		}
		if (isUpdate) {
			if (type.getId() == null) {
				return "";
			}
		}
		if (type.getDescription() == null) {
			return "";
		}
		return null;
	}

}
