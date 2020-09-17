package com.paypay.baymax.core.dao.security;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.paypay.baymax.core.dao.GenericDAOImpl;
import com.paypay.baymax.domain.security.Group_authorities;
import com.paypay.baymax.domain.security.Users;

import java.util.ArrayList;
import java.util.List;

@Repository
public class GroupAuthoritiesDAO extends GenericDAOImpl<Group_authorities, Integer> implements IGroupAuthoritiesDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAuthoritiesByGroup(ArrayList<String> group) {
		String hql = "select distinct(ga.authority) " + " from" + "   Group_authorities ga" + " where "
				+ " 	ga.group_id " + " 		in (select " + "	distinct(g.id) " + " from " + " Groups g "
				+ " where " + "	  g.group_name in (" + this.formatList2Query(group) + ") " + ")";
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		return query.list();
	}

	/**
	 * Transforma el contenido de una lista a una cadena en formato {elemento 1,
	 * elemento 2, elemento n} para que estos valores puedan ser utilizados en
	 * consultas dónde se tenga que utilizar una lista de valores.
	 * 
	 * @param lista Lista a transformar
	 * @return String elementos en formato {'elemento 1', 'elemento 2', 'elemento n'}
	 */
	private String formatList2Query(ArrayList<String> lista) {
		String response = "";
		for (String item : lista) {
			response += "'" + item.trim() + "',";
		}
		if (response.length() > 0) {
			return response.substring(0, response.length() - 1);
		} else {
			response = "''";
			return response;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Group_authorities> getGroupAuthoritiesList() {
		return sessionFactory.getCurrentSession().createQuery("from Group_authorities ga inner join fetch ga.group_id")
				.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Group_authorities> getGroupAuthoritiesListByGrupo(Long grupo) {
		return sessionFactory.getCurrentSession()
				.createQuery("from Group_authorities ga inner join fetch ga.group_id where ga.group_id = " + grupo)
				.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Group_authorities getGroupAuthorities(int id) {
		List<Group_authorities> responselist;
		responselist = sessionFactory.getCurrentSession()
				.createQuery("from Group_authorities ga inner join fetch ga.group_id where ga.id = " + id).list();
		Group_authorities ctGroupAuthoritie = (Group_authorities) responselist.get(0);
		return ctGroupAuthoritie;
	}

	@Override
	public boolean existGroupAuthorities(Group_authorities ga) {
		boolean response = false;
		String queryString = "from Group_authorities ga where ga.authority = :authority  and ga.group_id.id = :groupId";
		Query consulta = sessionFactory.getCurrentSession().createQuery(queryString);
		consulta.setParameter("authority", ga.getAuthority());
		consulta.setParameter("groupId", ga.getGroup_id().getId());
		consulta.setMaxResults(1);
		Object resultado = consulta.uniqueResult();
		if (resultado != null)
			response = true;
		return response;
	}

	@Override
	public boolean userExist(String username) {
		Users user = (Users) sessionFactory.getCurrentSession().createQuery("from Users where username =:username ")
				.setParameter("username", username).uniqueResult();

		return user != null;
	}
}
