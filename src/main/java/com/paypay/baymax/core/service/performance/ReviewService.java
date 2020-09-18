package com.paypay.baymax.core.service.performance;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paypay.baymax.commons.DTO.performance.ReviewDTO;
import com.paypay.baymax.commons.type.ReviewListType;
import com.paypay.baymax.commons.type.ReviewType;
import com.paypay.baymax.core.converter.performance.ReviewConverter;
import com.paypay.baymax.core.service.GenericServiceImpl;
import com.paypay.baymax.domain.performance.TReview;

@Service
public class ReviewService extends GenericServiceImpl<TReview, Long, ReviewDTO> implements IReviewService {

	@Override
	@Transactional
	public ReviewType saveReview(ReviewType review) {
		try {
			TReview tReview = ReviewConverter.convertTypeToEntity(review);
			this.add(tReview);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return review;
	}

	@Override
	@Transactional
	public ReviewType updateReview(ReviewType review) {
		try {
			TReview tReview = ReviewConverter.convertTypeToEntity(review);
			this.update(tReview);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return review;
	}

	@Override
	@Transactional
	public ReviewListType getAllReviews() {
		ReviewListType reviewListType = new ReviewListType();
		try {
			List<TReview> tReview = this.getAll();
			List<ReviewType> reviewType = ReviewConverter.convertListEntityToType(tReview);
			reviewListType.setReviews(reviewType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reviewListType;
	}

	@Override
	@Transactional
	public ReviewType getReviewById(Long id) {
		ReviewType reviewType = new ReviewType();
		try {
			TReview tReview = this.get(id);
			reviewType = ReviewConverter.convertEntityToType(tReview);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reviewType;
	}

	@Override
	@Transactional
	public void disableReviewById(Long id) {
		try {
			TReview tReview = this.get(id);
			tReview.setUpdateDate(new Date());
			tReview.setUpdateUsername("Admin");
			tReview.setStatus(false);
			this.update(tReview);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
