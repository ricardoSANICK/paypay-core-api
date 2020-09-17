package com.paypay.baymax.core.dao.security;

import java.util.List;

import com.paypay.baymax.core.dao.IGenericDAO;
import com.paypay.baymax.domain.security.Groups;

public interface IGroupsDAO extends IGenericDAO<Groups, Long> {

	public List<Groups> getGroupsByClaves(List<Long> claves);
	
}
