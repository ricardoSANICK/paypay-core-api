package com.paypay.baymax.core.service.security.sau;

import com.nxn.sau.security.DTO.SecurityDTO;
import com.paypay.baymax.commons.DTO.util.ResponseDTO;

public interface ISAUService {
	
	ResponseDTO getUserDetailsByUsername(String username);

	ResponseDTO recordLastfailedattempt(String username);

	ResponseDTO recordUserBlocked(String username);

	ResponseDTO recordLastAcces(String username);

	int changePassword(SecurityDTO DTO);

	ResponseDTO requestResetPassword(SecurityDTO DTO);

	int resetPassword(SecurityDTO DTO);
}
