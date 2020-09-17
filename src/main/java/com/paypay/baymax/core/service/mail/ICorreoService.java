package com.paypay.baymax.core.service.mail;

import java.util.List;

import com.paypay.baymax.commons.DTO.mail.CorreoDTO;
import com.paypay.baymax.commons.DTO.pagination.DataTableRequest;
import com.paypay.baymax.core.service.IGenericService;
import com.paypay.baymax.domain.mail.TCorreo;

public interface ICorreoService extends IGenericService<TCorreo, Integer, CorreoDTO>{
	
	public Object getServerSideList(DataTableRequest<Object> dtReq);
	public List<TCorreo> getListCorreo(String estatus);

}
