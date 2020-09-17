package com.paypay.baymax.core.dao.security;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.paypay.baymax.core.dao.GenericDAOImpl;
import com.paypay.baymax.domain.security.Group_members;
import com.paypay.baymax.domain.security.Groups;

import java.util.List;

@Repository
public class GroupMembersDAO extends GenericDAOImpl<Group_members, Integer> implements IGroupMembersDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@SuppressWarnings("unchecked")
	@Override
	public List<Group_members> getGroupMembersList() {
		return sessionFactory.getCurrentSession().createQuery("from Group_members gm inner join fetch gm.group_id")
				.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Group_members> getGroupMembersListByGrupo(Long grupo) {
		return sessionFactory.getCurrentSession()
				.createQuery("from Group_members gm inner join fetch gm.group_id where gm.group_id = " + grupo).list();
	}

	@Override
	public boolean existGroupMembers(Group_members gm) {
		boolean response = false;
		String queryString = "from Group_members gm where gm.username = :username  and gm.group_id.id = :groupsId";
		Query consulta = sessionFactory.getCurrentSession().createQuery(queryString);
		consulta.setParameter("username", gm.getUsername());
		consulta.setParameter("groupsId", gm.getGroup_id().getId());
		consulta.setMaxResults(1);
		Object resultado = consulta.uniqueResult();
		if (resultado != null)
			response = true;
		return response;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Group_members getGroupMembers(int id) {
		List<Group_members> responselist;
		responselist = sessionFactory.getCurrentSession()
				.createQuery("from Group_members gm inner join fetch gm.group_id g where gm.id = " + id).list();
		Group_members ctGroupMembers = (Group_members) responselist.get(0);
		return ctGroupMembers;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Group_members getGroupMembersByUsername(String username) {

		Group_members ctGroupMembers = new Group_members();

		List<Group_members> groupMembersList = (List<Group_members>) sessionFactory.getCurrentSession()
				.createQuery("from Group_members where username = :username").setParameter("username", username).list();

		if (groupMembersList == null || groupMembersList.isEmpty()) {
			ctGroupMembers = null;
		} else {

			List<Groups> groupList = (List<Groups>) sessionFactory.getCurrentSession().createQuery("FROM Groups")
					.list();
			boolean moreThanCabina = false;

			for (Groups group : groupList) {
				for (Group_members gm : group.getGroupMemberses()) {
					for (Group_members gml : groupMembersList) {
						if (gml.getId().compareTo(gm.getId()) == 0) {
							long id = gm.getGroup_id().getId();
							if (group.getGroup_name().toUpperCase().compareTo("CABINA") == 0) {
								moreThanCabina = true;
								Groups g = new Groups(id, group.getGroup_name());
								ctGroupMembers.setGroup_id(g);
							}
							if (!moreThanCabina) {
								Groups g = new Groups(id, group.getGroup_name());
								ctGroupMembers.setGroup_id(g);
							}

						}
					}
				}
			}
		}
		return ctGroupMembers;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Group_members> getGMByUsername(String username) {
		return (List<Group_members>) sessionFactory.getCurrentSession().createQuery(
				"from Group_members gm inner join fetch gm.group_id where gm.username = :username order by gm.group_id.group_name asc")
				.setParameter("username", username).list();
	}

}
