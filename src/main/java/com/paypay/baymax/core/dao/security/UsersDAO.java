package com.paypay.baymax.core.dao.security;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.paypay.baymax.core.dao.GenericDAOImpl;
import com.paypay.baymax.domain.security.Group_members;
import com.paypay.baymax.domain.security.Users;
import com.paypay.baymax.domain.security.sau.SAUPolicies;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UsersDAO extends GenericDAOImpl<Users, String> implements IUsersDAO {

	@Autowired
	private SessionFactory sessionFactory;

	static Logger log = LoggerFactory.getLogger(UsersDAO.class);

	// Obtener un usuario del sistema.

	@Override
	public Users getByUsername(String username) {
		return (Users) sessionFactory.getCurrentSession().createQuery("from Users as u where u.username = :username ")
				.setParameter("username", username).uniqueResult();
	}

	@Override
	public List<?> getTreeList() {
		String queryString = "SELECT u.username, u.firstName, u.lastName, u.telephone, u.cellphone, u.email, u.enabled, u.avatar, gm.group_id, g.group_name FROM users u "
				+ "INNER JOIN group_members gm ON u.username = gm.username "
				+ "INNER JOIN groups g ON gm.group_id = g.id ";

		return sessionFactory.getCurrentSession().createSQLQuery(queryString)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	// Obtener la lista de usuarios activos.

	@Override
	@SuppressWarnings("unchecked")
	public List<Users> getList() {
		return sessionFactory.getCurrentSession()
				.createQuery("from Users as u order by u.enabled desc, u.lastName desc, u.firstName desc").list();
	}

	// Obtener la lista de usuarios activos.

	@Override
	@SuppressWarnings("unchecked")
	public List<Users> getActiveList() {
		String queryString = " from Users as u where u.enabled = :enabled";
		Query consulta = sessionFactory.getCurrentSession().createQuery(queryString);
		consulta.setParameter("enabled", true);
		return consulta.list();
	}

	// Obtener la lista de administradores activos.

	@Override
	@SuppressWarnings("unchecked")
	public List<Users> getAdminListActivos() {
		List<String> uNames = new ArrayList<>();
		String queryString = "from GroupMembers m " + " left join fetch m.groups g " + " WHERE g.group_name = :gName ";

		Query consulta = sessionFactory.getCurrentSession().createQuery(queryString);
		consulta.setParameter("gName", "ADMINISTRADOR");
		List<Group_members> miembros = consulta.list();
		for (Group_members aux : miembros) {
			uNames.add(aux.getUsername());
		}

		if (uNames != null && !uNames.isEmpty()) {
			queryString = "from Users as u where " + " u.username IN (:uNames) and "
					+ " u.recibirNotificaciones = :notificaciones and " + " u.enabled = :enabled ";
			consulta = sessionFactory.getCurrentSession().createQuery(queryString);
			consulta.setParameterList("uNames", uNames);
			consulta.setParameter("notificaciones", false);
			consulta.setParameter("enabled", true);
			return consulta.list();
		} else {
			return new ArrayList<Users>();
		}
	}

	// Actualizar un usuario sin generar auditoria del registro a los campos con la
	// anotación @NotAudited

	@Override
	public void mergeUsers(Users user) {
		sessionFactory.getCurrentSession().merge(user);
	}

	// Trunca la tabla de usuario del sistema.

	@Override
	public void truncateUsers() {
		Session session = sessionFactory.getCurrentSession();
		String queryString = "truncate table users";
		SQLQuery consulta = session.createSQLQuery(queryString);
		consulta.executeUpdate();
		session.flush();
	}

	// Consulta la existencia de un usuario del sistema.

	@Override
	public boolean existUser(String username) {
		boolean response = false;
		String queryString = "from Users user where user.username = :username";
		Query consulta = sessionFactory.getCurrentSession().createQuery(queryString);
		consulta.setParameter("username", username);
		Object resultado = consulta.uniqueResult();
		if (resultado != null)
			response = true;
		return response;
	}

	@Override
	public SAUPolicies getSAUPoliciesByUsername(String username) {
		String queryString = "from " + SAUPolicies.class.getSimpleName() + " where FK_username = :username ";
		return (SAUPolicies) sessionFactory.getCurrentSession().createQuery(queryString)
				.setParameter("username", username).uniqueResult();
	}
}