package com.paypay.baymax.core.dao.combo;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;
import com.paypay.baymax.commons.constants.ConstantesCombos;
import com.paypay.baymax.domain.employee.TEmployee;
import com.paypay.baymax.domain.performance.TReview;

@Repository
public class ComboDAO implements IComboDAO {

	private final SessionFactory sessionFactory;

	@Autowired
	public ComboDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	@CacheEvict(value = ConstantesCombos.EMPLOYEE, allEntries = true)
	public void refreshCacheEmployees() {}
	
	@Override
	@CacheEvict(value = ConstantesCombos.REVIEW, allEntries = true)
	public void refreshCacheReviews() {}


	@Override
	public List<?> getEmployees() {
		List<?> list = sessionFactory.getCurrentSession()
				.createSQLQuery("SELECT id, name, status FROM " + TEmployee.class.getSimpleName())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		return list != null ? list : new ArrayList<>();
	}

	@Override
	public List<?> getReviews() {
		List<?> list = sessionFactory.getCurrentSession()
				.createSQLQuery("SELECT id, description AS name, status FROM " + TReview.class.getSimpleName())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		return list != null ? list : new ArrayList<>();
	}

	

}
