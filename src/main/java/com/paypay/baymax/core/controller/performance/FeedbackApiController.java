package com.paypay.baymax.core.controller.performance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypay.baymax.commons.api.FeedbackApi;
import com.paypay.baymax.commons.type.FeedbackListType;
import com.paypay.baymax.commons.type.FeedbackType;
import com.paypay.baymax.commons.type.OperFeedbackAllType;
import com.paypay.baymax.commons.type.OperFeedbackListAllType;
import com.paypay.baymax.core.filters.performance.FeedbackFilter;
import com.paypay.baymax.core.service.catalog.IEmployeeService;
import com.paypay.baymax.core.service.performance.IFeedbackService;

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
public class FeedbackApiController implements FeedbackApi {

    private static final Logger log = LoggerFactory.getLogger(FeedbackApiController.class);
    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;
    private final IFeedbackService feedbackService;

    @org.springframework.beans.factory.annotation.Autowired
    public FeedbackApiController(ObjectMapper objectMapper, HttpServletRequest request,
    		IFeedbackService feedbackService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.feedbackService = feedbackService;
    }

	@Override
	public ResponseEntity<Void> disable(
			@ApiParam(value = "Feedback ID",required=true) @PathVariable("id") Long id) {
		String accept = request.getHeader("Accept");
		return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
	}

	@Override
	public ResponseEntity<OperFeedbackListAllType> getAll() {
		OperFeedbackListAllType operFeedbackListAllType = new OperFeedbackListAllType();
		
		try {
			FeedbackListType feedbackListType = this.feedbackService.getAllFeedbacks();
			operFeedbackListAllType.setFeedbacks(feedbackListType);
		} catch (Exception e) {
			
		} finally {
			
		}
		return ResponseEntity.ok().body(operFeedbackListAllType);
	}

	@Override
	public ResponseEntity<OperFeedbackAllType> getFeedbackById(
			@ApiParam(value = "Feedback ID",required=true) @PathVariable("id") Long id) {
		OperFeedbackAllType operFeedbackAllType = new OperFeedbackAllType();
		try {
			FeedbackType feedbackType = this.feedbackService.getFeedbackById(id);
			operFeedbackAllType.setFeedback(feedbackType);
		} catch (Exception e) {
			
		} finally {
			
		}
		return ResponseEntity.ok().body(operFeedbackAllType);
	}

	@Override
	public ResponseEntity<OperFeedbackAllType> save(
			@ApiParam(value = "Insert a feedback object." ,required=true )  
			@Valid @RequestBody OperFeedbackAllType body) {
		OperFeedbackAllType operFeedbackAllType = new OperFeedbackAllType();
		try {
			 String filter = FeedbackFilter.validateRequest(body.getFeedback(), false);
			 FeedbackType feedbackType = this.feedbackService.saveFeedback(body.getFeedback());
			 operFeedbackAllType.setFeedback(feedbackType);
		} catch (Exception e) {
			
		} 
		return ResponseEntity.ok().body(operFeedbackAllType);
	}

	@Override
	public ResponseEntity<OperFeedbackAllType> update(
			@ApiParam(value = "Update a feedback object." ,required=true )  
			@Valid @RequestBody OperFeedbackAllType body) {
		OperFeedbackAllType operFeedbackAllType = new OperFeedbackAllType();
		try {
			String filter = FeedbackFilter.validateRequest(body.getFeedback(), true);
			FeedbackType feedbackType = this.feedbackService.updateFeedback(body.getFeedback());
			 operFeedbackAllType.setFeedback(feedbackType);
		} catch (Exception e) {
			
		} 
		return ResponseEntity.ok().body(operFeedbackAllType);
	}
}
