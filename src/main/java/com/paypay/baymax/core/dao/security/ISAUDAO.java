package com.paypay.baymax.core.dao.security;

import java.util.List;

import com.paypay.baymax.commons.DTO.security.SAUDTO;
import com.paypay.baymax.domain.security.Group_authorities;
import com.paypay.baymax.domain.security.Group_members;
import com.paypay.baymax.domain.security.sau.PasswordHistory;
import com.paypay.baymax.domain.security.sau.SAUPolicies;

public interface ISAUDAO {

	
	SAUPolicies updateSAUPolicies(SAUPolicies sauPolicies);

	
	PasswordHistory updatePasswordHistory(PasswordHistory passwordHistory);

	PasswordHistory savePasswordHistory(PasswordHistory passwordHistory);

	List<PasswordHistory> getPasswordHistoryListByUsername(String username);

	SAUDTO getUserDetailsByUsername(String username);

	List<Group_members> getGroupsByUsername(String username);

	List<Group_authorities> getAuthoritiesByGroup(long groupId);

	List<Group_authorities> getAuthoritiesByGroups(List<Long> groupIdList);
	
}	

