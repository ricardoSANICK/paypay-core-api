package com.paypay.baymax.core.dao.security;

import org.springframework.stereotype.Repository;

import com.paypay.baymax.core.dao.GenericDAOImpl;
import com.paypay.baymax.domain.security.Groups;

import java.util.List;

@Repository
public class GroupsDAO extends GenericDAOImpl<Groups, Long> implements IGroupsDAO {

	@SuppressWarnings("unchecked")
	@Override
	public List<Groups> getGroupsByClaves(List<Long> claves) {

		return super.sessionFactory.getCurrentSession()
				.createQuery("from " + Groups.class.getSimpleName() + " g where g.id in(:claves) ")
				.setParameterList("claves", claves).list();
	}

}
