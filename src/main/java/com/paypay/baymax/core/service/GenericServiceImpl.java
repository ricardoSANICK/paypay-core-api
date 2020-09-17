package com.paypay.baymax.core.service;

import com.paypay.baymax.core.dao.IGenericDAO;
import com.paypay.baymax.core.service.utils.MapperUtils;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unchecked")
@Service
public abstract class GenericServiceImpl<E, K, L>  implements IGenericService<E, K, L> {
	
	@Autowired(required=true)
    private IGenericDAO<E, K> genericDao;
	
	private ModelMapper modelMapper = new ModelMapper();
	
	private MapperUtils<E, L> mapperUtils;
	
	private Class<? extends L> dtoType;
	
	private Class<? extends E> entityType;
		
    public GenericServiceImpl(IGenericDAO<E,K> genericDao) {
        this.genericDao=genericDao;
    }
     
	@SuppressWarnings("rawtypes")
	public GenericServiceImpl() {
  	  Type t = getClass().getGenericSuperclass();
      ParameterizedType pt = (ParameterizedType) t;
      dtoType = (Class) pt.getActualTypeArguments()[2];
      entityType = (Class) pt.getActualTypeArguments()[0];
      mapperUtils = new MapperUtils<E,L>(modelMapper,dtoType,entityType);
      //mapperUtils = new MapperUtils<E,L>(modelMapper);
    }
 
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrUpdate(E entity) {
        genericDao.saveOrUpdate(entity);
    }
 
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<E> getAll() {
        return genericDao.getAll();
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<E> getAllActive() {
        return genericDao.getAllActive();
    }
 
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public E get(K id) {
        if(id != null) {
            return genericDao.find(id);
        } else {
            return null;
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public L getDTO(K id) {
      return convertToDTO(genericDao.find(id));
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<E> getByField(String fieldName, String fieldType, Object fieldValue){
    	return  genericDao.findByField(fieldName, fieldType, fieldValue);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<E> getByFields(HashMap<String, Object[]> fields){
    	return genericDao.findByFields(fields);
    }
 
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void add(E entity) {
        genericDao.add(entity);
    }
 
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void update(E entity) {
        genericDao.update(entity);
    }
 
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void remove(E entity) {
        genericDao.remove(entity);
    }
    
    @Override
    public E convertToEntity(L dto){
    	return mapperUtils.convertToEntity(dto);
    }
    
    @Override
    public L convertToDTO(E entity){
    	return mapperUtils.convertToDTO(entity);
    }
    
    @Override
    @Transactional(readOnly = true)
  	public List<L> converToDTOList(List<E> list){
		return mapperUtils.converToDTOList(list);
  	}

	public MapperUtils<E, L> getMapperUtils() {
		return mapperUtils;
	}

	@Override
	public ModelMapper getModelMapper() {
		return modelMapper;
	}

	public IGenericDAO<E, K> getGenericDao() {
		return genericDao;
	}

}
