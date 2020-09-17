package com.paypay.baymax.core.controller.employee;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypay.baymax.commons.api.EmployeeApi;
import com.paypay.baymax.commons.type.EmployeeListType;
import com.paypay.baymax.commons.type.EmployeeType;
import com.paypay.baymax.commons.type.OperEmployeeAllType;
import com.paypay.baymax.commons.type.OperEmployeeListAllType;
import com.paypay.baymax.commons.util.Modulos;
import com.paypay.baymax.core.filters.catalog.EmployeeFilter;
import com.paypay.baymax.core.service.catalog.IEmployeeService;
import com.paypay.baymax.core.service.combo.IComboService;
import com.paypay.baymax.domain.employee.TEmployee;

import io.swagger.annotations.ApiParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-09-13T17:05:15.456-05:00[America/Mexico_City]")
@Controller
public class EmployeeApiController implements EmployeeApi {

    private static final Logger log = LoggerFactory.getLogger(EmployeeApiController.class);
    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;
    private final IEmployeeService employeeService;
	private final IComboService comboService;
	private final String modulo = Modulos.ENTIDADES.get(TEmployee.class.getSimpleName());

    @org.springframework.beans.factory.annotation.Autowired
    public EmployeeApiController(ObjectMapper objectMapper, HttpServletRequest request,
    		IEmployeeService employeeService, IComboService comboService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.employeeService = employeeService;
		this.comboService = comboService;
    }

	@Override
	public ResponseEntity<Void> disable(
			@ApiParam(value = "Employee ID",required=true) @PathVariable("id") Long id) {
		String accept = request.getHeader("Accept");
		return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
	}

	@Override
	public ResponseEntity<OperEmployeeListAllType> getAll() {
		OperEmployeeListAllType operEmployeeListAllType = new OperEmployeeListAllType();
		
		try {
			EmployeeListType employeeListType = this.employeeService.getAllEmployees();
			operEmployeeListAllType.setEmployees(employeeListType);
		} catch (Exception e) {
			
		} finally {
			
		}
		return ResponseEntity.ok().body(operEmployeeListAllType);
	}

	@Override
	public ResponseEntity<OperEmployeeAllType> getEmployeeById(
			@ApiParam(value = "Employee ID",required=true) @PathVariable("id") Long id) {
		OperEmployeeAllType operEmployeeAllType = new OperEmployeeAllType();
		try {
			EmployeeType employeeType = this.employeeService.getEmployeeById(id);
			operEmployeeAllType.setEmployee(employeeType);
		} catch (Exception e) {
			
		} finally {
			
		}
		return ResponseEntity.ok().body(operEmployeeAllType);
	}

	@Override
	public ResponseEntity<OperEmployeeAllType> save(
			@ApiParam(value = "Insert a employee object." ,required=true ) 
			@Valid @RequestBody OperEmployeeAllType body) {
		OperEmployeeAllType operEmployeeAllType = new OperEmployeeAllType();
		try {
			 String filter = EmployeeFilter.validateRequest(body.getEmployee(), false);
			 EmployeeType employeeType = this.employeeService.saveEmployee(body.getEmployee());
			 operEmployeeAllType.setEmployee(employeeType);
		} catch (Exception e) {
			
		} finally {
			comboService.refreshCacheOfCurrentEventEntity(modulo);
		}
		return ResponseEntity.ok().body(operEmployeeAllType);
	}

	@Override
	public ResponseEntity<OperEmployeeAllType> update(
			@ApiParam(value = "Update a employee object." ,required=true )  
			@Valid @RequestBody OperEmployeeAllType body) {
		OperEmployeeAllType operEmployeeAllType = new OperEmployeeAllType();
		try {
			String filter = EmployeeFilter.validateRequest(body.getEmployee(), true);
			EmployeeType employeeType = this.employeeService.updateEmployee(body.getEmployee());
			 operEmployeeAllType.setEmployee(employeeType);
		} catch (Exception e) {
			
		} finally {
			comboService.refreshCacheOfCurrentEventEntity(modulo);
		}
		return ResponseEntity.ok().body(operEmployeeAllType);
	}

}
