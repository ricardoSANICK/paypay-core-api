package com.paypay.baymax.core.service.performance;

import com.paypay.baymax.commons.DTO.performance.ReviewDTO;
import com.paypay.baymax.commons.type.ReviewListType;
import com.paypay.baymax.commons.type.ReviewType;
import com.paypay.baymax.core.service.IGenericService;
import com.paypay.baymax.domain.performance.TReview;

public interface IReviewService extends IGenericService<TReview, Long, ReviewDTO> {
	
	public ReviewType saveReview(ReviewType review);
	public ReviewType updateReview(ReviewType review);
	public ReviewListType getAllReviews();
	public ReviewType getReviewById(Long id);
	public void disableReviewById(Long id);

}
