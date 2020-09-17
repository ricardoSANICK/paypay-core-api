package com.paypay.baymax.core.filters.catalog;

import com.paypay.baymax.commons.type.EmployeeType;

public class EmployeeFilter {

	public static String validateRequest(EmployeeType type, Boolean isUpdate) {
		if (type == null) {
			return "";
		}
		if (isUpdate) {
			if (type.getId() == null) {
				return "";
			}
		}
		if (type.getName() == null) {
			return "";
		}
		return null;
	}

}
