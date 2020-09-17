package com.paypay.baymax.core.dao;

import org.hibernate.SessionFactory;

import java.util.HashMap;
import java.util.List;

public interface IGenericDAO<E,K> {
	
	 public void add(E entity);
	 
	 public void saveOrUpdate(E entity);
	 
	 public void update(E entity);
	
	 public void remove(E entity);
		 
	 public E find(K key);
	 
	 public List<E> findByField(String fieldName, String fieldType, Object fieldValue);
	 
	 public List<E> findByFields(HashMap<String, Object[]> fields);
	 
	 public List<E> getAll() ;
	 
	 public List<E> getAllActive();
	 
	 public SessionFactory getSessionFactory();
	 
}