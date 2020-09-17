package com.paypay.baymax.core.controller.combo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypay.baymax.commons.api.ComboApi;
import com.paypay.baymax.commons.constants.ConstantesCombos;
import com.paypay.baymax.commons.type.ComboListType;
import com.paypay.baymax.commons.type.OperComboListAllType;
import com.paypay.baymax.core.service.combo.IComboService;

import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2019-07-18T23:13:42.544-05:00[America/Mexico_City]")
@Controller
public class ComboApiController implements ComboApi {

	private static final Logger log = LoggerFactory.getLogger(ComboApiController.class);
	private final ObjectMapper objectMapper;
	private final HttpServletRequest request;
	private final IComboService comboService;

	@org.springframework.beans.factory.annotation.Autowired
	public ComboApiController(ObjectMapper objectMapper, HttpServletRequest request, IComboService comboService) {
		this.objectMapper = objectMapper;
		this.request = request;
		this.comboService = comboService;
	}

	public ResponseEntity<OperComboListAllType> getCombo(
			@ApiParam(value = "Combo", required = true) @PathVariable("option") String option) {
		OperComboListAllType operComboListAllType = new OperComboListAllType();
		ComboListType comboListType = new ComboListType();
		try {
			switch (option) {
			case ConstantesCombos.EMPLOYEE:
				comboListType = comboService.getEmployees();
				break;
			case ConstantesCombos.REVIEW:
				comboListType = comboService.getReviews();
				break;
			default:
				break;

			}
			operComboListAllType.setCombo(comboListType);
		} catch (Exception e) {
		}
		return ResponseEntity.ok().body(operComboListAllType);
	}

	@Override
	public ResponseEntity<OperComboListAllType> getComboByParameter(
			@ApiParam(value = "Combo",required=true) @PathVariable("option") String option,
			@ApiParam(value = "Combo",required=true) @PathVariable("parameter") String parameter) {
		OperComboListAllType operComboListAllType = new OperComboListAllType();
		ComboListType comboListType = new ComboListType();
		try {
			
			operComboListAllType.setCombo(comboListType);
		} catch (Exception e) {
		}
		return ResponseEntity.ok().body(operComboListAllType);
	}

}
