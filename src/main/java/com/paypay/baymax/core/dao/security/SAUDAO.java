package com.paypay.baymax.core.dao.security;

import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.paypay.baymax.commons.DTO.security.SAUDTO;
import com.paypay.baymax.domain.security.Group_authorities;
import com.paypay.baymax.domain.security.Group_members;
import com.paypay.baymax.domain.security.Users;
import com.paypay.baymax.domain.security.sau.PasswordHistory;
import com.paypay.baymax.domain.security.sau.SAUPolicies;

import java.util.Date;
import java.util.List;

@Repository
public class SAUDAO implements ISAUDAO {

	private final SessionFactory sessionFactory;
	private final ModelMapper modelMapper;

	@Autowired
	public SAUDAO(SessionFactory sessionFactory, ModelMapper modelMapper) {
		this.sessionFactory = sessionFactory;
		this.modelMapper = modelMapper;
	}

	public void addToModelMapper() {
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
	}

	/**
	 * @param sauPolicies
	 * @return
	 */
	@Override
	public SAUPolicies updateSAUPolicies(SAUPolicies sauPolicies) {
		sessionFactory.getCurrentSession().update(sauPolicies);
		return sauPolicies;
	}

	/**
	 * @param passwordHistory
	 * @return
	 */
	@Override
	public PasswordHistory updatePasswordHistory(PasswordHistory passwordHistory) {
		sessionFactory.getCurrentSession().update(passwordHistory);
		return passwordHistory;
	}

	/**
	 * @param passwordHistory
	 * @return
	 */
	@Override
	public PasswordHistory savePasswordHistory(PasswordHistory passwordHistory) {
		sessionFactory.getCurrentSession().save(passwordHistory);
		return passwordHistory;
	}

	/**
	 * @param username
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PasswordHistory> getPasswordHistoryListByUsername(String username) {
		return sessionFactory.getCurrentSession()
				.createQuery("from " + PasswordHistory.class.getSimpleName()
						+ " ph where ph.username.username = :username  order by ph.recordDate desc ")
				.setParameter("username", username).setMaxResults(5).list();
	}

	/**
	 * @param username
	 * @return
	 */
	@Override
	public SAUDTO getUserDetailsByUsername(String username) {
		addToModelMapper();
		Users users = (Users) sessionFactory.getCurrentSession()
				.createQuery("from " + Users.class.getSimpleName() + " u where u.username = :username ")
				.setParameter("username", username).uniqueResult();

		SAUDTO saudto = null;

		if (users != null) {
			saudto = modelMapper.map(users, SAUDTO.class);
			saudto.setFirstName(users.getFirstName());
			saudto.setLastName(users.getLastName());
		}

		return saudto;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Group_members> getGroupsByUsername(String username) {
		return sessionFactory.getCurrentSession()
				.createQuery("from " + Group_members.class.getSimpleName() + " g where g.username = :username")
				.setParameter("username", username).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Group_authorities> getAuthoritiesByGroup(long groupId) {
		return sessionFactory.getCurrentSession()
				.createQuery("from " + Group_authorities.class.getSimpleName() + " g where g.group_id.id = :groupId")
				.setParameter("groupId", groupId).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Group_authorities> getAuthoritiesByGroups(List<Long> groupIdList) {
		return sessionFactory.getCurrentSession()
				.createQuery(
						"from " + Group_authorities.class.getSimpleName() + " g where g.group_id.id in(:groupIdList)")
				.setParameterList("groupIdList", groupIdList).list();
	}

}
