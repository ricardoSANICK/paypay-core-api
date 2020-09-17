package com.paypay.baymax.core.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paypay.baymax.commons.DTO.pagination.DataTableRequest;
import com.paypay.baymax.commons.DTO.pagination.DataTableResults;
import com.paypay.baymax.commons.DTO.security.Group_membersDTO;
import com.paypay.baymax.core.dao.security.IGroupMembersDAO;
import com.paypay.baymax.core.service.GenericServiceImpl;
import com.paypay.baymax.core.service.IServerSideService;
import com.paypay.baymax.core.service.ServerSideCM;
import com.paypay.baymax.domain.security.Group_members;
import com.paypay.baymax.commons.DTB.security.Group_membersDTB;

import java.util.List;

@Service
public class GroupMembersService extends GenericServiceImpl<Group_members, Integer, Group_membersDTO>
		implements IGroupMembersService, IServerSideService<Group_membersDTB> {

	@Transactional(readOnly = true)
	public DataTableResults<Group_membersDTB> getServerSideList(DataTableRequest<Object> dtReq) {
		ServerSideCM<Group_members, Group_membersDTB> ssCM;
		ssCM = new ServerSideCM<>(new Group_members(), new Group_membersDTB(), "gm", "id",
				super.getGenericDao().getSessionFactory().getCurrentSession());

		return ssCM.getServerSideList(dtReq);
	}

	@Autowired
	private IGroupMembersDAO GroupMembersDAO;

	@Override
	@Transactional
	public List<Group_members> getGroupMembersList() {
		return GroupMembersDAO.getGroupMembersList();
	}

	@Override
	@Transactional
	public boolean existGroupMembers(Group_members gm) {
		return GroupMembersDAO.existGroupMembers(gm);
	}

	@Override
	@Transactional
	public Group_members getGroupMembers(Integer id) {
		return GroupMembersDAO.getGroupMembers(id);
	}

	@Override
	@Transactional
	public Group_members getGroupMembersByUsername(String username) {
		return GroupMembersDAO.getGroupMembersByUsername(username);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Group_members> getGMByUsername(String username) {
		return GroupMembersDAO.getGMByUsername(username);
	}

	@Override
	@Transactional
	public List<Group_members> getGroupMembersListByGrupo(Long grupo) {
		return GroupMembersDAO.getGroupMembersListByGrupo(grupo);
	}

	@Override
	@Transactional
	public void deleteGM(long group_id, String username) {
		super.getGenericDao().getSessionFactory().getCurrentSession()
				.createSQLQuery("delete from " + Group_members.class.getSimpleName()
						+ " where group_id =:gid and username =:username")
				.setParameter("gid", group_id).setParameter("username", username).executeUpdate();

	}
}
