package com.paypay.baymax.core.dao.security;

import java.util.List;

import com.paypay.baymax.core.dao.IGenericDAO;
import com.paypay.baymax.domain.security.Users;
import com.paypay.baymax.domain.security.sau.SAUPolicies;

public interface IUsersDAO extends IGenericDAO<Users, String> {
	
    Users getByUsername(String username);

    List<?> getTreeList();
    
	List<Users> getList();
	
	List<Users> getActiveList();
	
	List<Users> getAdminListActivos();
    
	void mergeUsers(Users userDTO);

	void truncateUsers();
		
	boolean existUser(String user);

	SAUPolicies getSAUPoliciesByUsername(String username);	

}
