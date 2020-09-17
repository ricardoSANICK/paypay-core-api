package com.paypay.baymax.core.converter.performance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.paypay.baymax.commons.type.ReviewType;
import com.paypay.baymax.domain.performance.TReview;

public class ReviewConverter {

	public static TReview convertTypeToEntity(ReviewType review) {
		TReview tReview = new TReview();
		if(review != null) {
			if(review.getId() != null) {
				tReview.setId(review.getId());
			}
			if(review.getDescription() != null) {
				tReview.setDescription(review.getDescription());
			}
			if(review.getAssigners() != null) {
				tReview.setAssigners(review.getAssigners());
			}
			if(review.getRecordDate() == null) {
				tReview.setRecordDate(new Date());
			}else {
				tReview.setUpdateDate(new Date());
			}
			if(review.getRecordUsername() == null) {
				tReview.setRecordUsername("Admin");
			}else {
				tReview.setUpdateUsername("Admin");
			}
			tReview.setStatus(review.isStatus());
		}
		return tReview;
	}
	
	public static List<ReviewType> convertListEntityToType(List<TReview> lstTReview) {
		List<ReviewType> lstReviewType = new ArrayList<ReviewType>();
		if(lstTReview.size() > 0 ) {
			for(TReview tReview: lstTReview){
				lstReviewType.add(ReviewConverter.convertEntityToType(tReview));
			}
		}
		return lstReviewType;
		
	}

	public static ReviewType convertEntityToType(TReview review) {
		ReviewType reviewType = new ReviewType();
		if(review != null) {
			if(review.getId() != null) {
				reviewType.setId(review.getId());
			}
			if(review.getDescription() != null) {
				reviewType.setDescription(review.getDescription());
			}
			if(review.getAssigners() != null) {
				reviewType.setAssigners(review.getAssigners());
			}
			reviewType.setStatus(review.isStatus());
		}
		return reviewType;
	}

}
