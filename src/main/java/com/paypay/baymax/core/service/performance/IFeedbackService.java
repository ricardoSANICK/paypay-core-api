package com.paypay.baymax.core.service.performance;

import com.paypay.baymax.commons.DTO.performance.FeedbackDTO;
import com.paypay.baymax.commons.type.FeedbackListType;
import com.paypay.baymax.commons.type.FeedbackType;
import com.paypay.baymax.core.service.IGenericService;
import com.paypay.baymax.domain.performance.TFeedback;

public interface IFeedbackService extends IGenericService<TFeedback, Long, FeedbackDTO> {
	
	public FeedbackType saveFeedback(FeedbackType feedback);
	public FeedbackType updateFeedback(FeedbackType feedback);
	public FeedbackListType getAllFeedbacks();
	public FeedbackType getFeedbackById(Long id);
	public void disableFeedbackById(Long id);

}
