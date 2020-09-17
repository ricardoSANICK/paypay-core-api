package com.paypay.baymax.core.filters.performance;

import com.paypay.baymax.commons.type.ReviewType;

public class ReviewFilter {
	
	public static String validateRequest(ReviewType type, Boolean isUpdate) {
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
		if(type.getAssigners() != null) {
			return "";
		}
		return null;
	}

}
