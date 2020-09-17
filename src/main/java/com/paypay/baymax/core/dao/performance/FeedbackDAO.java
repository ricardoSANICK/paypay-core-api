package com.paypay.baymax.core.dao.performance;

import org.springframework.stereotype.Repository;
import com.paypay.baymax.core.dao.GenericDAOImpl;
import com.paypay.baymax.domain.performance.TFeedback;

@Repository
public class FeedbackDAO extends GenericDAOImpl<TFeedback, Long> implements IFeedbackDAO {

}
