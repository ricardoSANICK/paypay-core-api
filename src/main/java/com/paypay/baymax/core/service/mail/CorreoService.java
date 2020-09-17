package com.paypay.baymax.core.service.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paypay.baymax.commons.DTO.mail.CorreoDTO;
import com.paypay.baymax.commons.DTO.pagination.DataTableRequest;
import com.paypay.baymax.commons.DTO.pagination.DataTableResults;
import com.paypay.baymax.core.dao.mail.ICorreoDAO;
import com.paypay.baymax.core.service.GenericServiceImpl;
import com.paypay.baymax.core.service.IServerSideService;
import com.paypay.baymax.core.service.ServerSideCM;
import com.paypay.baymax.domain.mail.TCorreo;
import com.paypay.baymax.commons.DTB.mail.CorreoDTB;

import java.util.List;

@Service
public class CorreoService extends GenericServiceImpl<TCorreo, Integer, CorreoDTO> implements ICorreoService, IServerSideService<CorreoDTB> {
	
	private final ICorreoDAO correoDAO;
	
	@Autowired
	public CorreoService(ICorreoDAO correoDAO) {
		this.correoDAO = correoDAO;
	}
	
	@Transactional(readOnly=true)
	@Override
	public DataTableResults<CorreoDTB> getServerSideList(DataTableRequest<Object> dtReq) {
		ServerSideCM<TCorreo, CorreoDTB> ssCM;
		ssCM = new ServerSideCM<>(new TCorreo()
								  , new CorreoDTB()
								  , "c"
								  , "id"
								  , super.getGenericDao().getSessionFactory().getCurrentSession());
		
		return ssCM.getServerSideList(dtReq);
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<TCorreo> getListCorreo(String estatus) {
		return correoDAO.getListByCorreo(estatus);
	}
	
}
