package com.paypay.baymax.core.dao.security;

import java.util.ArrayList;
import java.util.List;

import com.paypay.baymax.core.dao.IGenericDAO;
import com.paypay.baymax.domain.security.Group_authorities;


public interface IGroupAuthoritiesDAO extends IGenericDAO<Group_authorities, Integer> {
	
	public List<String> getAuthoritiesByGroup(ArrayList<String> group);
    
    public List<Group_authorities> getGroupAuthoritiesList();
    
    public List<Group_authorities> getGroupAuthoritiesListByGrupo(Long grupo);
    
    public Group_authorities getGroupAuthorities(int id);
    
    public boolean existGroupAuthorities(Group_authorities ga);    
    
    public boolean userExist(String user);
    
}
