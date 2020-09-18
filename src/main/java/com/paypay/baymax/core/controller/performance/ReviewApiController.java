package com.paypay.baymax.core.controller.performance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypay.baymax.commons.api.ReviewApi;
import com.paypay.baymax.commons.type.OperReviewAllType;
import com.paypay.baymax.commons.type.OperReviewListAllType;
import com.paypay.baymax.commons.type.ReviewListType;
import com.paypay.baymax.commons.type.ReviewType;
import com.paypay.baymax.core.filters.performance.ReviewFilter;
import com.paypay.baymax.core.service.performance.IReviewService;

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
public class ReviewApiController implements ReviewApi {

    private static final Logger log = LoggerFactory.getLogger(ReviewApiController.class);
    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;
    private final IReviewService reviewService;

    @org.springframework.beans.factory.annotation.Autowired
    public ReviewApiController(ObjectMapper objectMapper, HttpServletRequest request,
    		IReviewService reviewService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.reviewService = reviewService;
    }

	@Override
	public ResponseEntity<Void> disable(
			@ApiParam(value = "Review ID",required=true) @PathVariable("id") Long id) {
		try {
			this.reviewService.disableReviewById(id);
		} catch (Exception e) {
			
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<OperReviewListAllType> getAll() {
		OperReviewListAllType operReviewListAllType = new OperReviewListAllType();
		
		try {
			ReviewListType reviewListType = this.reviewService.getAllReviews();
			operReviewListAllType.setReviews(reviewListType);
		} catch (Exception e) {
			
		} finally {
			
		}
		return ResponseEntity.ok().body(operReviewListAllType);
	}

	@Override
	public ResponseEntity<OperReviewAllType> getReviewById(
			@ApiParam(value = "Review ID",required=true) @PathVariable("id") Long id) {
		OperReviewAllType operReviewAllType = new OperReviewAllType();
		try {
			ReviewType reviewType = this.reviewService.getReviewById(id);
			operReviewAllType.setReview(reviewType);
		} catch (Exception e) {
			
		} finally {
			
		}
		return ResponseEntity.ok().body(operReviewAllType);
	}

	@Override
	public ResponseEntity<OperReviewAllType> save(
			@ApiParam(value = "Insert a review object." ,required=true )  
			@Valid @RequestBody OperReviewAllType body) {
		OperReviewAllType operReviewAllType = new OperReviewAllType();
		try {
			 String filter = ReviewFilter.validateRequest(body.getReview(), false);
			 ReviewType reviewType = this.reviewService.saveReview(body.getReview());
			 operReviewAllType.setReview(reviewType);
		} catch (Exception e) {
			
		}
		return ResponseEntity.ok().body(operReviewAllType);
	}

	@Override
	public ResponseEntity<OperReviewAllType> update(
			@ApiParam(value = "Update a review object." ,required=true )  
			@Valid @RequestBody OperReviewAllType body) {
		OperReviewAllType operReviewAllType = new OperReviewAllType();
		try {
			String filter = ReviewFilter.validateRequest(body.getReview(), true);
			ReviewType reviewType = this.reviewService.updateReview(body.getReview());
			 operReviewAllType.setReview(reviewType);
		} catch (Exception e) {
			
		}
		return ResponseEntity.ok().body(operReviewAllType);
	}

}
