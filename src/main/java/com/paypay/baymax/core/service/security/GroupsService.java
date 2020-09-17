package com.paypay.baymax.core.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paypay.baymax.commons.DTO.pagination.DataTableRequest;
import com.paypay.baymax.commons.DTO.pagination.DataTableResults;
import com.paypay.baymax.commons.DTO.security.GroupsDTO;
import com.paypay.baymax.commons.security.CustomGrantedAuthorities;
import com.paypay.baymax.core.dao.security.IGroupAuthoritiesDAO;
import com.paypay.baymax.core.service.GenericServiceImpl;
import com.paypay.baymax.core.service.IServerSideService;
import com.paypay.baymax.core.service.ServerSideCM;
import com.paypay.baymax.domain.security.Group_authorities;
import com.paypay.baymax.domain.security.Groups;
import com.paypay.baymax.commons.DTB.security.GroupsDTB;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupsService extends GenericServiceImpl<Groups, Long, GroupsDTO>
		implements IGroupsService, IServerSideService<GroupsDTB> {

	private final IGroupAuthoritiesDAO groupAuthoritiesDAO;

	@Autowired
	public GroupsService(IGroupAuthoritiesDAO groupAuthoritiesDAO) {
		this.groupAuthoritiesDAO = groupAuthoritiesDAO;
	}

	@Transactional(readOnly = true)
	public Groups saveGroup(Groups group) {
		super.getGenericDao().add(group);

		Group_authorities ga = new Group_authorities(null, group, CustomGrantedAuthorities.ROLE_ACCESO.getAuthority());
		ga.setRecordDate(group.getRecordDate());
		ga.setRecordUsername(group.getRecordUsername());

		groupAuthoritiesDAO.add(ga);

		return group;
	}

	@Override
	@Transactional(readOnly = true)
	public List<GroupsDTO> getList() {
		return converToDTOList(this.getGenericDao().getAll());
	}

	@Transactional(readOnly = true)
	public DataTableResults<GroupsDTB> getServerSideList(DataTableRequest<Object> dtReq) {
		ServerSideCM<Groups, GroupsDTB> ssCM;
		ssCM = new ServerSideCM<>(new Groups(), new GroupsDTB(), "g", "id",
				super.getGenericDao().getSessionFactory().getCurrentSession());

		return ssCM.getServerSideList(dtReq);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public List<Groups> getGroupByListID(List<Integer> listID) {
		return super.getGenericDao().getSessionFactory().getCurrentSession()
				.createQuery("from " + Groups.class.getSimpleName() + " g where g.id in(:listID)")
				.setParameterList("listID",
						listID.stream().map(val -> Long.parseLong(val.toString())).collect(Collectors.toList()))
				.list();
	}
}
