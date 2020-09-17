package com.paypay.baymax.core.service.combo;


import java.util.concurrent.Future;
import org.springframework.scheduling.annotation.Async;
import com.paypay.baymax.commons.type.ComboListType;

public interface IComboService {
	
	ComboListType getEmployees();
	ComboListType getReviews();
	
	@Async
	Future<Boolean> refreshCacheOfCurrentEventEntity(String entity);

}
