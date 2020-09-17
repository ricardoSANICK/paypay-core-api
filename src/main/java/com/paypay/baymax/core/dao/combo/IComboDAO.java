package com.paypay.baymax.core.dao.combo;

import java.util.List;

public interface IComboDAO {
	
	List<?> getEmployees();
	List<?> getReviews();

	void refreshCacheEmployees();
	void refreshCacheReviews();
	
}
