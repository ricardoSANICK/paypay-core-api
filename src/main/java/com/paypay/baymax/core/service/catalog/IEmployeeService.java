package com.paypay.baymax.core.service.catalog;

import com.paypay.baymax.commons.DTO.catalog.EmployeeDTO;
import com.paypay.baymax.commons.type.EmployeeListType;
import com.paypay.baymax.commons.type.EmployeeType;
import com.paypay.baymax.core.service.IGenericService;
import com.paypay.baymax.domain.employee.TEmployee;

public interface IEmployeeService extends IGenericService<TEmployee, Long, EmployeeDTO> {
	
	public EmployeeType saveEmployee(EmployeeType employee);
	public EmployeeType updateEmployee(EmployeeType employee);
	public EmployeeListType getAllEmployees();
	public EmployeeType getEmployeeById(Long id);
	public void disableEmployeeById(Long id);

}
