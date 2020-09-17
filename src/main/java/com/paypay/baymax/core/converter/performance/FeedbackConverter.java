package com.paypay.baymax.core.converter.performance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.paypay.baymax.commons.type.EmployeeType;
import com.paypay.baymax.commons.type.FeedbackType;
import com.paypay.baymax.commons.type.ReviewType;
import com.paypay.baymax.domain.employee.TEmployee;
import com.paypay.baymax.domain.performance.TFeedback;
import com.paypay.baymax.domain.performance.TReview;

public class FeedbackConverter {

	public static TFeedback convertTypeToEntity(FeedbackType feedback) {
		TFeedback tFeedback = new TFeedback();
		if(feedback != null) {
			if(feedback.getId() != null) {
				tFeedback.setId(feedback.getId());
			}
			if(feedback.getDescription() != null) {
				tFeedback.setDescription(feedback.getDescription());
			}
			
			if(feedback.getAssigner() != null) {
				TEmployee tEmployee = new TEmployee();
				if(feedback.getAssigner().getId() != null) {
					tEmployee.setId(feedback.getAssigner().getId());
				}
				if(feedback.getAssigner().getName() != null) {
					tEmployee.setName(feedback.getAssigner().getName());
				}
				tFeedback.setAssigner(tEmployee);
			}
			if(feedback.getAssigned() != null) {
				TEmployee tEmployee = new TEmployee();
				if(feedback.getAssigned().getId() != null) {
					tEmployee.setId(feedback.getAssigned().getId());
				}
				if(feedback.getAssigned().getName() != null) {
					tEmployee.setName(feedback.getAssigned().getName());
				}
				tFeedback.setAssigned(tEmployee);
			}
			if(feedback.getReview() != null) {
				TReview tReview = new TReview();
				if(feedback.getReview().getId() != null) {
					tReview.setId(feedback.getReview().getId());
				}
				if(feedback.getReview().getDescription() != null) {
					tReview.setDescription(feedback.getReview().getDescription());
				}
				tFeedback.setReview(tReview);
			}
			if(feedback.getRecordDate() == null) {
				tFeedback.setRecordDate(new Date());
			}else {
				tFeedback.setUpdateDate(new Date());
			}
			if(feedback.getRecordUsername() == null) {
				tFeedback.setRecordUsername("Admin");
			}else {
				tFeedback.setUpdateUsername("Admin");
			}
			tFeedback.setStatus(feedback.isStatus());
		}
		return tFeedback;
	}
	
	public static List<FeedbackType> convertListEntityToType(List<TFeedback> lstTFeedback) {
		List<FeedbackType> lstFeedbackType = new ArrayList<FeedbackType>();
		if(lstTFeedback.size() > 0 ) {
			for(TFeedback tFeedback: lstTFeedback){
				lstFeedbackType.add(FeedbackConverter.convertEntityToType(tFeedback));
			}
		}
		return lstFeedbackType;
		
	}

	public static FeedbackType convertEntityToType(TFeedback feedback) {
		FeedbackType feedbackType = new FeedbackType();
		if(feedback != null) {
			if(feedback.getId() != null) {
				feedbackType.setId(feedback.getId());
			}
			if(feedback.getDescription() != null) {
				feedbackType.setDescription(feedback.getDescription());
			}
			if(feedback.getAssigner() != null) {
				EmployeeType employeeType = new EmployeeType();
				if(feedback.getAssigner().getId() != null) {
					employeeType.setId(feedback.getAssigner().getId());
				}
				if(feedback.getAssigner().getName() != null) {
					employeeType.setName(feedback.getAssigner().getName());
				}
				feedbackType.setAssigner(employeeType);
			}
			if(feedback.getAssigned() != null) {
				EmployeeType employeeType = new EmployeeType();
				if(feedback.getAssigned().getId() != null) {
					employeeType.setId(feedback.getAssigned().getId());
				}
				if(feedback.getAssigned().getName() != null) {
					employeeType.setName(feedback.getAssigned().getName());
				}
				feedbackType.setAssigned(employeeType);
			}
			if(feedback.getReview() != null) {
				ReviewType reviewType = new ReviewType();
				if(feedback.getReview().getId() != null) {
					reviewType.setId(feedback.getReview().getId());
				}
				if(feedback.getReview().getDescription() != null) {
					reviewType.setDescription(feedback.getReview().getDescription());
				}
				feedbackType.setReview(reviewType);
			}
			feedbackType.setStatus(feedback.isStatus());
		}
		return feedbackType;
	}

}
