package com.paypay.baymax.core.converter.catalog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.paypay.baymax.commons.type.EmployeeType;
import com.paypay.baymax.domain.employee.TEmployee;

public class EmployeeConverter {
	
	public static TEmployee convertTypeToEntity(EmployeeType employee) {
		TEmployee tEmployee = new TEmployee();
		if(employee != null) {
			if(employee.getId() != null) {
				tEmployee.setId(employee.getId());
			}
			if(employee.getName() != null) {
				tEmployee.setName(employee.getName());
			}
			if(employee.getRecordDate() == null) {
				tEmployee.setRecordDate(new Date());
			}else {
				tEmployee.setUpdateDate(new Date());
			}
			if(employee.getRecordUsername() == null) {
				tEmployee.setRecordUsername("Admin");
			}else {
				tEmployee.setUpdateUsername("Admin");
			}
			tEmployee.setStatus(employee.isStatus());
		}
		return tEmployee;
	}
	
	public static List<EmployeeType> convertListEntityToType(List<TEmployee> lstTEmployee) {
		List<EmployeeType> lstEmployeeType = new ArrayList<EmployeeType>();
		if(lstTEmployee.size() > 0 ) {
			for(TEmployee tlabel: lstTEmployee){
				lstEmployeeType.add(EmployeeConverter.convertEntityToType(tlabel));
			}
		}
		return lstEmployeeType;
		
	}
	
	public static EmployeeType convertEntityToType(TEmployee employee) {
		EmployeeType employeeType = new EmployeeType();
		if(employee != null) {
			if(employee.getId() != null) {
				employeeType.setId(employee.getId());
			}
			if(employee.getName() != null) {
				employeeType.setName(employee.getName());
			}
			employeeType.setStatus(employee.isStatus());
		}
		return employeeType;
	}

}
