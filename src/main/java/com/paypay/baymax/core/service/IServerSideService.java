package com.paypay.baymax.core.service;

import com.paypay.baymax.commons.DTO.pagination.DataTableRequest;
import com.paypay.baymax.commons.DTO.pagination.DataTableResults;

public interface IServerSideService<E> {
	
	public DataTableResults<E> getServerSideList(DataTableRequest<Object> dtReq);
	
}
