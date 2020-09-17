package com.paypay.baymax.core.converter.generales;

import java.util.Date;

import com.paypay.baymax.commons.type.AuditType;
import com.paypay.baymax.domain.general.TAudit;

public class AuditConverter {
	
	public static TAudit convertTypeToEntity(AuditType type) {
		TAudit tAudit = new TAudit();
		if(type != null) {
			if(type.getRecordDate() == null) {
				tAudit.setRecordDate(new Date());
			}else {
				tAudit.setUpdateDate(new Date());
			}
			if(type.getRecordUsername() == null) {
				tAudit.setRecordUsername("Admin");
			}else {
				tAudit.setUpdateUsername("Admin");
			}
		}
		return null;
	}
	
}
