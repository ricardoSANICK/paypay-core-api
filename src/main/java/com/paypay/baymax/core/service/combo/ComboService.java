package com.paypay.baymax.core.service.combo;

import java.util.List;
import java.util.concurrent.Future;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paypay.baymax.commons.type.ComboListType;
import com.paypay.baymax.commons.type.ComboType;
import com.paypay.baymax.commons.util.Modulos;
import com.paypay.baymax.core.converter.generales.ComboConverter;
import com.paypay.baymax.core.dao.combo.IComboDAO;
import com.paypay.baymax.domain.employee.TEmployee;
import com.paypay.baymax.domain.performance.TReview;

@Service
@Transactional(readOnly = true)
public class ComboService implements IComboService {

	private final IComboDAO dao;

	@Autowired
	public ComboService(IComboDAO dao) {
		this.dao = dao;
	}

	@Override
	public ComboListType getEmployees() {
		ComboListType comboListType = new ComboListType();
		List<?> lstTCombo = dao.getEmployees();
		List<ComboType> newsType = ComboConverter.convertListEntityToType(lstTCombo);
		comboListType.setCombo(newsType);
		return comboListType;
	}
	
	@Override
	public ComboListType getReviews() {
		ComboListType comboListType = new ComboListType();
		List<?> lstTCombo = dao.getReviews();
		List<ComboType> newsType = ComboConverter.convertListEntityToType(lstTCombo);
		comboListType.setCombo(newsType);
		return comboListType;
	}

	@Override
	public Future<Boolean> refreshCacheOfCurrentEventEntity(String entity) {
		if(entity.equals(Modulos.ENTIDADES.get(TEmployee.class.getSimpleName()))) {
			dao.refreshCacheEmployees();
		} else if(entity.equals(Modulos.ENTIDADES.get(TReview.class.getSimpleName()))) {
			dao.refreshCacheReviews();
		}
		return new AsyncResult<>(Boolean.TRUE);
	}
	
}
