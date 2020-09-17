package com.paypay.baymax.core.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Repository
public abstract class GenericDAOImpl<E, K extends Serializable> implements IGenericDAO<E, K> {

	@Autowired
	public SessionFactory sessionFactory;

	protected Class<? extends E> daoType;

	/**
	 * By defining this class as abstract, we prevent Spring from creating instance
	 * of this class If not defined as abstract, getClass().getGenericSuperClass()
	 * would return Object. There would be exception because Object class does not
	 * have constructor with parameters.
	 */

	public GenericDAOImpl() {
		Type t = getClass().getGenericSuperclass();
		ParameterizedType pt = (ParameterizedType) t;
		daoType = (Class) pt.getActualTypeArguments()[0];
	}

	protected Session currentSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public void add(E entity) {
		currentSession().save(entity);
	}

	@Override
	public void saveOrUpdate(E entity) {
		currentSession().saveOrUpdate(entity);
	}

	@Override
	public void update(E entity) {
		currentSession().update(entity);
	}

	@Override
	public void remove(E entity) {
		currentSession().delete(entity);
	}

	@Override
	public E find(K key) {
		return (E) currentSession().get(daoType, key);
	}

	@Override
	public List<E> findByField(String fieldName, String fieldType, Object fieldValue) {
		if (fieldValue instanceof List<?>) {
			return currentSession().createCriteria(daoType).add(Restrictions.in(fieldName, (List<?>) fieldValue))
					.list();
		} else {
			return currentSession().createCriteria(daoType).add(Restrictions.eq(fieldName, fieldValue)).list();
		}
	}

	@Override
	public List<E> findByFields(HashMap<String, Object[]> fields) {

		Criteria criteria = currentSession().createCriteria(daoType);

		for (Entry<String, Object[]> value : fields.entrySet()) {
			if (value.getValue()[0].toString().contains("java.util.list")) {
				criteria.add(Restrictions.in(value.getKey(), (List<?>) value.getValue()[1]));
			} else {
				criteria.add(Restrictions.eq(value.getKey(), value.getValue()[1]));
			}
		}

		return criteria.list();
	}

	@Override
	public List<E> getAll() {
		return currentSession().createCriteria(daoType).list();
	}

	@Override
	public List<E> getAllActive() {
		return currentSession().createCriteria(daoType).add(Restrictions.eq("status", true)).list();
	}

	@Override
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

}