package com.paypay.baymax.core.service.security;

import java.util.ArrayList;
import java.util.List;

import com.paypay.baymax.commons.DTO.pagination.DataTableRequest;
import com.paypay.baymax.commons.DTO.security.Group_authoritiesDTO;
import com.paypay.baymax.core.service.IGenericService;
import com.paypay.baymax.domain.security.Group_authorities;

public interface IGroupAuthoritiesService extends IGenericService<Group_authorities, Integer, Group_authoritiesDTO>{
	
	public List<String> getAuthoritiesByGroup(ArrayList<String> group);
	
	public List<Group_authorities> getGroupAuthoritiesList();
	
	public List<Group_authorities> getGroupAuthoritiesListByGrupo(Long grupo);
	
    public Group_authorities getGroupAuthorities(int id);	
    
    public boolean existGroupAuthorities(Group_authorities ga);   
    
    public boolean userExist(String user);

	public Object getServerSideList(DataTableRequest<Object> dtReq);
	
	public void deleteGroup_Authority(int group_id, String authority);
	
	public List<Group_authorities> getGAListByGroup(long id);

	List<Group_authorities> getAllMigracion();
}
