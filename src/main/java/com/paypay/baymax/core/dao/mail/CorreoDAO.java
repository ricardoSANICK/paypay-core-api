package com.paypay.baymax.core.dao.mail;

import org.springframework.stereotype.Repository;

import com.paypay.baymax.core.dao.GenericDAOImpl;
import com.paypay.baymax.domain.mail.TCorreo;

import java.util.List;

@Repository
public class CorreoDAO extends GenericDAOImpl<TCorreo, Integer> implements ICorreoDAO {

	@SuppressWarnings("unchecked")
	@Override
	public List<TCorreo> getListByCorreo(String estatus) {

		StringBuilder query = new StringBuilder("from TCorreo c WHERE ");
		query.append("estatus = :estatus");
		return sessionFactory.getCurrentSession().createQuery(query.toString()).setParameter("estatus", estatus).list();
	}

}