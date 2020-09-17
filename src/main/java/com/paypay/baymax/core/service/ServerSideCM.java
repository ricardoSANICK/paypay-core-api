package com.paypay.baymax.core.service;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.paypay.baymax.commons.DTB.DTBModules;
import com.paypay.baymax.commons.DTO.pagination.DataTableRequest;
import com.paypay.baymax.commons.DTO.pagination.DataTableResults;
import com.paypay.baymax.commons.DTO.pagination.PaginationCriteria;
import com.paypay.baymax.commons.DTO.pagination.ResultsUtil;
import com.paypay.baymax.commons.util.DateUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class ServerSideCM<E, DTB> {

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	private final Session session;
	private final String aliasMain;
	private final String claveName;
	private final String mainTable;
	private final Class<DTB> entityClass;
	private final Class<DTB> DTBClass;
	private final Field[] fieldsDTB;

	@SuppressWarnings("unchecked")
	public ServerSideCM(E entity, DTB dtb, String aliasMain, String claveName, Session session) {
		this.entityClass = (Class<DTB>) entity.getClass();
		this.mainTable = entity.getClass().getSimpleName();
		this.DTBClass = (Class<DTB>) dtb.getClass();
		Field[] fields = DTBClass.getDeclaredFields();
		Field[] fieldsSuperClass = {};
		if (DTBClass.getSuperclass() != null)
			fieldsSuperClass = DTBClass.getSuperclass().getDeclaredFields();
		this.fieldsDTB = (Field[]) ArrayUtils.addAll(fields, fieldsSuperClass);
		this.aliasMain = aliasMain;
		this.claveName = claveName;
		this.session = session;
	}

	@Transactional(readOnly = true)
	public DataTableResults<DTB> getServerSideList(DataTableRequest<Object> dtReq) {

		long count = 0;
		List<DTB> lista = new ArrayList<DTB>();
		ResultsUtil<DTB> ru = new ResultsUtil<DTB>();

		try {
			PaginationCriteria pagination = dtReq.getPaginationRequest();
			HashMap<String, String> filtros = dtReq.getFilters();

			count = countServerSideList(pagination, filtros);

			if (count > 0) {
				lista = this.serverSideList(pagination, filtros);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ru.getDtResults(dtReq, count, lista);
	}

	public long countServerSideList(PaginationCriteria pagination, HashMap<String, String> filtros) {

		long count = 0;

		try {

			StringBuilder queryString = new StringBuilder();

			queryString.append("SELECT COUNT(distinct " + this.aliasMain + "." + this.claveName + ") ");
			queryString.append("FROM " + this.mainTable + " " + this.aliasMain);
			queryString.append(" " + this.generateLeftJoin());
			queryString.append(" " + this.generateWhereStringByFiltros(pagination, filtros));

			SQLQuery sqlQuery = session.createSQLQuery(queryString.toString());

			sqlQuery = this.setSQLParameters(sqlQuery, pagination, filtros);

			String uniqueResult = String.valueOf(sqlQuery.uniqueResult());
			if (StringUtils.isNotBlank(uniqueResult)) {
				if (StringUtils.isNumeric(uniqueResult)) {
					count = Long.parseLong(String.valueOf(sqlQuery.uniqueResult()));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return count;

	}

	@SuppressWarnings("unchecked")
	private List<DTB> serverSideList(PaginationCriteria pagination, HashMap<String, String> filtros) throws Exception {
		List<?> lista = new ArrayList<>();

		StringBuilder queryString = new StringBuilder();

		queryString.append(" SET LANGUAGE Spanish; ");
		queryString.append("SELECT * FROM ( ");
		queryString.append(" SELECT ROW_NUMBER() OVER ( " + this.getOrderByClause(pagination) + "  ) as RowNum, ");
		queryString.append(this.selectFieldsBuilder());
		queryString.append(" FROM " + this.mainTable + " " + this.aliasMain);
		queryString.append(" " + this.generateLeftJoin());
		queryString.append(" " + this.generateWhereStringByFiltros(pagination, filtros));
		queryString.append(" ) AS Rows" + this.mainTable + " ");
		queryString.append(" where RowNum >= :start");
		queryString.append(" and RowNum < :end");

		SQLQuery sqlQuery = session.createSQLQuery(queryString.toString());

		sqlQuery.setParameter("start", pagination.getPageNumber() + 1);
		sqlQuery.setParameter("end", pagination.getPageSize() + pagination.getPageNumber() + 1);
		sqlQuery = this.setSQLParameters(sqlQuery, pagination, filtros);

		lista = sqlQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

		return lista != null ? (ArrayList<DTB>) lista : new ArrayList<>();

	}

	private String generateLeftJoin() {
		String innerjoinQuery = "";

		for (Field field : fieldsDTB) {
			if (DTBModules.checkDTBRelation(field)) {
				innerjoinQuery += " left join " + DTBModules.parseEntityName(field) + " as " + this.aliasMain + "_"
						+ field.getName();
				innerjoinQuery += " on " + this.aliasMain + "." + DTBModules.getFKFieldName(field) + " = "
						+ this.aliasMain + "_" + field.getName() + "." + DTBModules.getPKFieldName(field);
			}
		}

		return innerjoinQuery + " ";
	}

	@SuppressWarnings("unchecked")
	private SQLQuery setSQLParameters(SQLQuery sqlQuery, PaginationCriteria pagination,
			HashMap<String, String> filtros) {

		if (filtros != null) {
			if (!filtros.isEmpty()) {
				for (Field field : fieldsDTB) {
					if (filtros.containsKey(field.getName())) {
						Type type = field.getType();
						org.hibernate.type.Type hType = this.getStandardBasicType(type, false);

						List<?> list = Arrays.asList(filtros.get(field.getName()).split(",")).stream()
								.map(Object::toString).collect(Collectors.toList());

						if (hType.getClass() == org.hibernate.type.DateType.class
								|| hType.getClass() == org.hibernate.type.TimestampType.class) {
							sqlQuery = this.chopDateSQLQuery(sqlQuery, pagination, (List<String>) list,
									field.getName());
						} else {
							sqlQuery.setParameterList("param_" + field.getName(), list);
						}

					}
				}
			}

		}

		return sqlQuery;
	}

	private String chopDateString(String queryString, PaginationCriteria pagination, List<String> filtroDate,
			String fieldName) {

		int ix = 0;

		for (String r : filtroDate) {
			String[] rango = DateUtils.getDateFromFilterd(r);
			if (rango.length == 2) {
				if (queryString.isEmpty()) {
					queryString = " where " + " ( (" + this.aliasMain + "." + fieldName + " BETWEEN :fa_a" + fieldName
							+ ix + " AND :fa_b" + fieldName + ix + " ) ";
				} else {
					if (ix == 0)
						queryString += " AND ( (" + this.aliasMain + "." + fieldName + " BETWEEN :fa_a" + fieldName + ix
								+ " AND :fa_b" + fieldName + ix + " ) ";
					else
						queryString += " or (" + this.aliasMain + "." + fieldName + " BETWEEN :fa_a" + fieldName + ix
								+ " AND :fa_b" + fieldName + ix + " ) ";
				}
			}
			ix++;
		}

		queryString += " )";

		return queryString;
	}

	/**
	 * 
	 * @param sqlQuery
	 * @param pagination
	 * @param filtroDate
	 * @param fieldName
	 * @return
	 */
	private SQLQuery chopDateSQLQuery(SQLQuery sqlQuery, PaginationCriteria pagination, List<String> filtroDate,
			String fieldName) {
		int ix = 0;
		for (String r : filtroDate) {
			String[] rango = DateUtils.getDateFromFilterd(r);
			if (rango.length == 2) {
				Date sp_a = DateUtils.getSp_aDate(rango[0]);
				Date sp_b = DateUtils.getSp_bDate(rango[1]);

				sqlQuery.setParameter("fa_a" + fieldName + ix, sp_a);
				sqlQuery.setParameter("fa_b" + fieldName + ix, sp_b);
			}
			ix++;
		}

		return sqlQuery;
	}

	/**
	 * 
	 * @return
	 */
	private String selectFieldsBuilder() {
		String fieldsSelect = "";

		for (Field field : fieldsDTB) {
			fieldsSelect += (fieldsSelect.length() > 1 ? ", " : " ");

			if (DTBModules.checkDTBRelation(field)) {
				Class<?> relationType = DTBModules.getDTBRelation(field);

				if (relationType != null) {
//					for(Entry<String, List<String>> j : DTBModules.includedMethods.get(DTBClass.getSimpleName()).entrySet())
//					{
//						log.info("-------------------------------");
//						log.info("key: " + j.getKey());
//						for(String relationFieldName : j.getValue()) {
//							//if(DTBModules.checkDTBRelation(relationFieldName)) {
//								//fieldsSelect += (this.aliasMain+"_"+field.getName()+"." + relationFieldName) + " as '" + field.getName()+"."+relationFieldName+"' ";
//								//fieldsSelect += (fieldsSelect.length() > 1 ? ", " : " ");
//							//} else {
//								//log.info("No relation field");
//								//fieldsSelect += (this.aliasMain+"_"+field.getName()+"." + relationFieldName) + " as '" + field.getName()+"."+relationFieldName+"' ";
//								//fieldsSelect += (fieldsSelect.length() > 1 ? ", " : " ");
//							//}
//							log.info("value: " + relationFieldName);
//						}
//						log.info("-------------------------------");
//					}

					Field[] relationFields = relationType.getDeclaredFields();
					for (Field relationField : relationFields) {
						if (DTBModules.includedMethods.containsKey(DTBClass.getSimpleName())) {
							if (DTBModules.includedMethods.get(DTBClass.getSimpleName())
									.containsKey(field.getType().getSimpleName())) {
								if (DTBModules.includedMethods.get(DTBClass.getSimpleName())
										.get(field.getType().getSimpleName()).contains(relationField.getName())) {
									fieldsSelect += (this.aliasMain + "_" + field.getName() + "."
											+ relationField.getName()) + " as '" + field.getName() + "."
											+ relationField.getName() + "' ";
									fieldsSelect += (fieldsSelect.length() > 1 ? ", " : " ");
								}
							}
						}
					}
					fieldsSelect = fieldsSelect.substring(0, fieldsSelect.length() - 2);
				}

			} else {
				fieldsSelect += (this.aliasMain + "." + field.getName()) + " as " + field.getName();
			}
		}

		return " " + fieldsSelect + " ";
	}

	/**
	 * 
	 * @param pagination
	 * @param filtros
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String generateWhereStringByFiltros(PaginationCriteria pagination, HashMap<String, String> filtros) {
		String whereString = "";

		if (filtros != null) {
			if (!filtros.isEmpty()) {
				for (Field field : fieldsDTB) {
					if (filtros.containsKey(field.getName())) {
						Type type = field.getType();
						org.hibernate.type.Type hType = this.getStandardBasicType(type, false);

						List<?> list = Arrays.asList(filtros.get(field.getName())).stream().map(Object::toString)
								.collect(Collectors.toList());

						if (hType.getClass() == org.hibernate.type.DateType.class
								|| hType.getClass() == org.hibernate.type.TimestampType.class) {
							String valueListFecha = String.valueOf(list.get(0));
							whereString = chopDateString(whereString, pagination,
									Arrays.asList(valueListFecha.split(",")), field.getName());
						} else if (DTBModules.checkDTBRelation(field)) {
							whereString += getWhereOrAND(whereString) + " " + aliasMain + "."
									+ DTBModules.getFKFieldName(field) + " in(:param_" + field.getName() + ")";
						} else {
							whereString += getWhereOrAND(whereString) + " " + aliasMain + "." + field.getName()
									+ " in(:param_" + field.getName() + ")";
						}

					}
				}
			}

			/** FILTRADOS POR EL INPUT SEARCH **/

			String searchHint = pagination.getSearchHint();

			String searchHintString = "";
			String or = "";

			if (StringUtils.isNotBlank(searchHint)) {
				searchHintString = StringUtils.isBlank(whereString) ? " WHERE ( " : " AND ( ";

				for (Field field : fieldsDTB) {
					String fieldsSelect = null;

					// Valida si el campo es de relacion
					if (DTBModules.checkDTBRelation(field)) {
						// Valida si clase principal tiene relaciones incluidas
						if (DTBModules.includedMethods.containsKey(DTBClass.getSimpleName())) {
							// Itera los campos de la relacion que están incluidos
							for (String relationFieldName : DTBModules.includedMethods.get(DTBClass.getSimpleName())
									.get(field.getType().getSimpleName())) {
								fieldsSelect = this.aliasMain + "_" + field.getName() + "." + relationFieldName;
								searchHintString += or + fieldsSelect + " LIKE '%" + searchHint + "%'  ";
							}
						}
					}
					// Cuando el campo no es de relación
					else {

						if (StringUtils.isNumeric(searchHint)) {
							if (field.getType() == Date.class) {
								searchHintString += or + " FORMAT(" + this.aliasMain + "." + field.getName()
										+ ", 'dddd, MMMM, yyyy, HH:mm:ss') LIKE '%" + searchHint + "%'  ";
								searchHintString += or + " FORMAT(" + this.aliasMain + "." + field.getName()
										+ ", 'dd/MM/yyyy HH:mm:ss') LIKE '%" + searchHint + "%'  ";
							} else if (field.getType() == String.class) {
								searchHintString += or + " " + this.aliasMain + "." + field.getName() + " LIKE '%"
										+ searchHint + "%' ";
							} else {
								searchHintString += or + " STR(" + this.aliasMain + "." + field.getName() + ") LIKE '%"
										+ searchHint + "%' ";
							}
						} else {
							if (field.getType() == Date.class) {
								searchHintString += or + " FORMAT(" + this.aliasMain + "." + field.getName()
										+ ", 'dddd, MMMM, yyyy, HH:mm:ss') LIKE '%" + searchHint + "%'  ";
								searchHintString += or + " FORMAT(" + this.aliasMain + "." + field.getName()
										+ ", 'dd/MM/yyyy HH:mm:ss') LIKE '%" + searchHint + "%'  ";
							} else if (field.getType() == String.class) {
								searchHintString += or + " " + this.aliasMain + "." + field.getName() + " LIKE '%"
										+ searchHint + "%' ";
							} else {
								searchHintString += or + this.aliasMain + "." + field.getName() + " LIKE '%"
										+ searchHint + "%'  ";
							}
						}
					}

					if (or.isEmpty()) {
						or = " or ";
					}
				}
				searchHintString += " )";
			}

			whereString += " " + searchHintString;
		}

		return whereString;
	}

	/**
	 * 
	 * @param type
	 * @return
	 */
	private org.hibernate.type.Type getStandardBasicType(Type type, boolean skipDateType) {
		org.hibernate.type.Type sbt = StandardBasicTypes.STRING;

		if (type != null) {
			if (type == String.class || (skipDateType && type == Date.class)) {
				sbt = StandardBasicTypes.STRING;
			} else if (type == Integer.class || type == int.class) {
				sbt = StandardBasicTypes.INTEGER;
			} else if (!skipDateType && type == Date.class) {
				sbt = StandardBasicTypes.TIMESTAMP;
			} else if (type == boolean.class || type == Boolean.class) {
				sbt = StandardBasicTypes.BOOLEAN;
			}
		}

		return sbt;
	}

	/**
	 * 
	 * @param pc
	 * @return
	 */
	private String getOrderByClause(PaginationCriteria pc) {
		String orderByClause = "";

		if (pc.isSortByEmpty()) {
			orderByClause = this.aliasMain + "." + this.claveName + " desc ";
		} else {
			orderByClause = pc.getOrderByClause();
			orderByClause = orderByClause.replaceAll("order by", "");
			orderByClause = orderByClause.replaceAll("ORDER BY", "");

			String orderByClauseValidate = orderByClause.trim();
			int espacio = orderByClauseValidate.indexOf(" ");
			orderByClauseValidate = orderByClauseValidate.substring(0, espacio).trim();

			Field[] fields = entityClass.getDeclaredFields();

			for (Field field : fields) {
				if (orderByClauseValidate.compareTo(field.getName()) == 0) {
					orderByClause = orderByClause.replaceAll(field.getName(), this.aliasMain + "." + field.getName());
				}
			}

		}

		if (orderByClause.toLowerCase().contains("recordDate")
				&& !orderByClause.toLowerCase().contains(this.aliasMain + "." + "recordDate")) {
			orderByClause = " ORDER BY " + this.aliasMain + "." + orderByClause.trim();
		} else {
			orderByClause = " ORDER BY " + orderByClause;
		}

		return orderByClause;
	}

	/**
	 * getWhereOrAND - Valida si devolverá WHERE ó AND dependiendo el valor del
	 * whereString
	 * 
	 * @param whereString
	 * @return WHERE | AND
	 */
	private String getWhereOrAND(String whereString) {
		return StringUtils.isBlank(whereString) ? " WHERE " : " AND ";
	}

}