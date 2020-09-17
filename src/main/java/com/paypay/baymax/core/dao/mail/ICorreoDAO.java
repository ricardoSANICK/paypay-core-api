package com.paypay.baymax.core.dao.mail;

import java.util.List;

import com.paypay.baymax.core.dao.IGenericDAO;
import com.paypay.baymax.domain.mail.TCorreo;

public interface ICorreoDAO extends IGenericDAO<TCorreo, Integer> {

	public List<TCorreo> getListByCorreo(String estatus);
		    
}
