package com.paypay.baymax.core.service.performance;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.paypay.baymax.commons.DTO.performance.FeedbackDTO;
import com.paypay.baymax.commons.type.FeedbackListType;
import com.paypay.baymax.commons.type.FeedbackType;
import com.paypay.baymax.core.converter.performance.FeedbackConverter;
import com.paypay.baymax.core.service.GenericServiceImpl;
import com.paypay.baymax.domain.performance.TFeedback;

@Service
public class FeedbackService extends GenericServiceImpl<TFeedback, Long, FeedbackDTO> implements IFeedbackService {

	@Override
	@Transactional
	public FeedbackType saveFeedback(FeedbackType feedback) {
		try {
			TFeedback tFeedback = FeedbackConverter.convertTypeToEntity(feedback);
			this.add(tFeedback);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return feedback;
	}

	@Override
	@Transactional
	public FeedbackType updateFeedback(FeedbackType feedback) {
		try {
			TFeedback tFeedback = FeedbackConverter.convertTypeToEntity(feedback);
			this.update(tFeedback);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return feedback;
	}

	@Override
	@Transactional
	public FeedbackListType getAllFeedbacks() {
		FeedbackListType feedbackListType = new FeedbackListType();
		try {
			List<TFeedback> tFeedback = this.getAll();
			List<FeedbackType> feedbackType = FeedbackConverter.convertListEntityToType(tFeedback);
			feedbackListType.setFeedbacks(feedbackType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return feedbackListType;
	}

	@Override
	@Transactional
	public FeedbackType getFeedbackById(Long id) {
		FeedbackType feedbackType = new FeedbackType();
		try {
			TFeedback tFeedback = this.get(id);
			feedbackType = FeedbackConverter.convertEntityToType(tFeedback);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return feedbackType;
	}

	@Override
	@Transactional
	public void disableFeedbackById(Long id) {
		try {
			TFeedback tFeedback = this.get(id);
			tFeedback.setUpdateDate(new Date());
			tFeedback.setUpdateUsername("Admin");
			tFeedback.setStatus(false);
			this.update(tFeedback);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
