package com.paypay.baymax.core.service.utils;

import org.joda.time.DateTime;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapperUtils<E, L> {

	private ModelMapper modelMapper;

	protected Class<? extends L> dtoType;

	protected Class<? extends E> entityType;

    public MapperUtils(ModelMapper modelMapper,Class<? extends L> dtoType, Class<? extends E> entityType) {
	//public MapperUtils(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
		this.dtoType = dtoType;
		this.entityType = entityType;
	}

	public E convertToEntity(L dto) {

		Converter<String, Date> toDate = new Converter<String, Date>() {
			public Date convert(MappingContext<String, Date> context) {

				String source = context.getSource();
				if (source != null) {
					if (source.isEmpty()) {
						source = null;
					}
				}

				return source == null ? null : new DateTime(source).toDate();
			}
		};
		modelMapper.addConverter(toDate);
		return modelMapper.map(dto, entityType);
	}

	public L convertToDTO(E entity) {
		return entity != null ? modelMapper.map(entity, dtoType) : null;
	}

	public List<L> converToDTOList(List<E> list) {
		List<L> listDTO = new ArrayList<L>();
		for (E col : list) {
			listDTO.add(convertToDTO(col));
		}
		return listDTO;
	}

}
