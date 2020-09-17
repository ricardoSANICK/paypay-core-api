package com.paypay.baymax.core.dao.performance;

import org.springframework.stereotype.Repository;
import com.paypay.baymax.core.dao.GenericDAOImpl;
import com.paypay.baymax.domain.performance.TReview;

@Repository
public class ReviewDAO extends GenericDAOImpl<TReview, Long> implements IReviewDAO {

}
