package com.paypay.baymax.core.service.security;

import java.util.List;

import com.paypay.baymax.commons.DTO.pagination.DataTableRequest;
import com.paypay.baymax.commons.DTO.security.Group_membersDTO;
import com.paypay.baymax.core.service.IGenericService;
import com.paypay.baymax.domain.security.Group_members;

public interface IGroupMembersService extends IGenericService<Group_members, Integer, Group_membersDTO>{
	
	public List<Group_members> getGroupMembersList();
	
	public List<Group_members> getGroupMembersListByGrupo(Long grupo);
	
	public boolean existGroupMembers(Group_members gm);

	public Group_members getGroupMembers(Integer id);
	
	public Group_members getGroupMembersByUsername(String username);
	
	public List<Group_members> getGMByUsername(String username);

	public Object getServerSideList(DataTableRequest<Object> dtReq);
	
	public void deleteGM(long group_id, String username);
}
