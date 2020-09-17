package com.paypay.baymax.core.dao.security;

import java.util.List;

import com.paypay.baymax.core.dao.IGenericDAO;
import com.paypay.baymax.domain.security.Group_members;

public interface IGroupMembersDAO extends IGenericDAO<Group_members, Integer>{

	public List<Group_members> getGroupMembersList();
	
	public List<Group_members> getGroupMembersListByGrupo(Long grupo);

	boolean existGroupMembers(Group_members gm);

	public Group_members getGroupMembers(int id);
	
	public Group_members getGroupMembersByUsername(String username);

	public List<Group_members> getGMByUsername(String username);

}
