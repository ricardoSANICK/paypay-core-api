package com.paypay.baymax.core.service.catalog;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paypay.baymax.commons.DTO.catalog.EmployeeDTO;
import com.paypay.baymax.commons.type.EmployeeListType;
import com.paypay.baymax.commons.type.EmployeeType;
import com.paypay.baymax.core.converter.catalog.EmployeeConverter;
import com.paypay.baymax.core.service.GenericServiceImpl;
import com.paypay.baymax.domain.employee.TEmployee;

@Service
public class EmployeeService extends GenericServiceImpl<TEmployee, Long, EmployeeDTO> implements IEmployeeService {

	@Override
	@Transactional
	public EmployeeType saveEmployee(EmployeeType employee) {
		try {
			TEmployee tEmployee = EmployeeConverter.convertTypeToEntity(employee);
			this.add(tEmployee);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return employee;
	}

	@Override
	@Transactional
	public EmployeeType updateEmployee(EmployeeType employee) {
		try {
			TEmployee tEmployee = EmployeeConverter.convertTypeToEntity(employee);
			this.update(tEmployee);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return employee;
	}

	@Override
	@Transactional
	public EmployeeListType getAllEmployees() {
		EmployeeListType employeeListType = new EmployeeListType();
		try {
			List<TEmployee> tEmployee = this.getAll();
			List<EmployeeType> employeeType = EmployeeConverter.convertListEntityToType(tEmployee);
			employeeListType.setEmployees(employeeType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return employeeListType;
	}

	@Override
	@Transactional
	public EmployeeType getEmployeeById(Long id) {
		EmployeeType employeeType = new EmployeeType();
		try {
			TEmployee tEmployee = this.get(id);
			employeeType = EmployeeConverter.convertEntityToType(tEmployee);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return employeeType;
	}

}
