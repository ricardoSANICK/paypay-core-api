package com.paypay.baymax.core.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paypay.baymax.commons.DTO.pagination.DataTableRequest;
import com.paypay.baymax.commons.DTO.pagination.DataTableResults;
import com.paypay.baymax.commons.DTO.security.Group_authoritiesDTO;
import com.paypay.baymax.core.dao.security.IGroupAuthoritiesDAO;
import com.paypay.baymax.core.service.GenericServiceImpl;
import com.paypay.baymax.core.service.IServerSideService;
import com.paypay.baymax.core.service.ServerSideCM;
import com.paypay.baymax.domain.security.Group_authorities;
import com.paypay.baymax.commons.DTB.security.Group_authoritiesDTB;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupAuthoritiesService extends GenericServiceImpl<Group_authorities, Integer, Group_authoritiesDTO>
		implements IGroupAuthoritiesService, IServerSideService<Group_authoritiesDTB> {

	@Autowired
	private IGroupAuthoritiesDAO groupAuthoritiesDAO;

	@Override
	@Transactional(readOnly = true)
	public List<String> getAuthoritiesByGroup(ArrayList<String> group) {
		return this.groupAuthoritiesDAO.getAuthoritiesByGroup(group);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Group_authorities> getGroupAuthoritiesList() {
		return this.groupAuthoritiesDAO.getGroupAuthoritiesList();
	}

	@Override
	@Transactional(readOnly = true)
	public Group_authorities getGroupAuthorities(int id) {
		return this.groupAuthoritiesDAO.getGroupAuthorities(id);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existGroupAuthorities(Group_authorities ga) {
		return this.groupAuthoritiesDAO.existGroupAuthorities(ga);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean userExist(String user) {
		return this.groupAuthoritiesDAO.userExist(user);
	}

	@Transactional(readOnly = true)
	public DataTableResults<Group_authoritiesDTB> getServerSideList(DataTableRequest<Object> dtReq) {

		ServerSideCM<Group_authorities, Group_authoritiesDTB> ssCM;
		ssCM = new ServerSideCM<>(new Group_authorities(), new Group_authoritiesDTB(), "c", "id",
				super.getGenericDao().getSessionFactory().getCurrentSession());

		return ssCM.getServerSideList(dtReq);
	}

	@Override
	@Transactional
	public List<Group_authorities> getGroupAuthoritiesListByGrupo(Long grupo) {
		return this.groupAuthoritiesDAO.getGroupAuthoritiesListByGrupo(grupo);
	}

	@Override
	@Transactional
	public void deleteGroup_Authority(int group_id, String authority) {
		super.getGenericDao().getSessionFactory().getCurrentSession()
				.createSQLQuery("delete from " + Group_authorities.class.getSimpleName()
						+ " where group_id =:gid and authority =:auth")
				.setParameter("gid", group_id).setParameter("auth", authority).executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public List<Group_authorities> getGAListByGroup(long id) {
		return super.getGenericDao().getSessionFactory().getCurrentSession()
				.createQuery("from " + Group_authorities.class.getSimpleName()
						+ " ga inner join fetch ga.group_id g where g.id =:gid order by ga.authority asc")
				.setParameter("gid", id).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public List<Group_authorities> getAllMigracion() {
		return super.getGenericDao().getSessionFactory().getCurrentSession()
				.createQuery("from " + Group_authorities.class.getSimpleName() + " ga inner join fetch ga.group_id g")
				.list();
	}

}
