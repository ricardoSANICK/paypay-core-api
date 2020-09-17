package com.paypay.baymax.core.service;

import org.modelmapper.ModelMapper;
import java.util.HashMap;
import java.util.List;

public interface IGenericService<E, K, L> {

	public void saveOrUpdate(E entity);

	public List<E> getAll();

	public List<E> getAllActive();

	public E get(K id);

	public L getDTO(K id);

	public List<E> getByField(String fieldName, String fieldType, Object fieldValue);

	public List<E> getByFields(HashMap<String, Object[]> fields);

	public void add(E entity);

	public void update(E entity);

	public void remove(E entity);

	public E convertToEntity(L dto);

	public L convertToDTO(E entity);

	public List<L> converToDTOList(List<E> list);

	public ModelMapper getModelMapper();

}
