package com.paypay.baymax.core.service.security.sau;

import com.nxn.sau.security.DTO.SecurityDTO;
import com.paypay.baymax.commons.DTO.security.SAUDTO;
import com.paypay.baymax.commons.DTO.util.MetaDTO;
import com.paypay.baymax.commons.DTO.util.ResponseDTO;
import com.paypay.baymax.commons.util.DefinicionesComunes;
import com.paypay.baymax.commons.util.FormatosFechaValidos;
import com.paypay.baymax.commons.util.GenericFormatter;
import com.paypay.baymax.core.dao.combo.IComboDAO;
import com.paypay.baymax.core.dao.security.ISAUDAO;
import com.paypay.baymax.core.dao.security.IUsersDAO;
import com.paypay.baymax.domain.security.Group_authorities;
import com.paypay.baymax.domain.security.Group_members;
import com.paypay.baymax.domain.security.Users;
import com.paypay.baymax.domain.security.sau.PasswordHistory;
import com.nxn.sau.security.ErrorCodes;
import com.nxn.sau.security.SecurityPolicies;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SAUService implements ISAUService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final ISAUDAO sauDAO;
	private final IUsersDAO userDAO;
	private final IComboDAO comboDAO;

	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Autowired
	public SAUService(ISAUDAO sauDAO, IUsersDAO userDAO, IComboDAO comboDAO) {
		this.sauDAO = sauDAO;
		this.userDAO = userDAO;
		this.comboDAO = comboDAO;
	}

	@SuppressWarnings("rawtypes")
	@Override
	@Transactional(readOnly = true)
	public ResponseDTO getUserDetailsByUsername(String username) {

		HashMap<String, Object> map = new HashMap<String, Object>();
		ResponseDTO responseDTO = new ResponseDTO(new MetaDTO(0, DefinicionesComunes.CODIGO_OK), map);

		try {

			SAUDTO sauDTO = sauDAO.getUserDetailsByUsername(username);

			List<Group_members> groupMembersList = sauDAO.getGroupsByUsername(username);

			List<Long> grupos = groupMembersList.stream().map(gm -> gm.getGroup_id().getId())
					.collect(Collectors.toList());

			List<Group_authorities> groupAuthoritiesList = sauDAO.getAuthoritiesByGroups(grupos);

			sauDTO.setRoles(
					groupAuthoritiesList.stream().map(Group_authorities::getAuthority).collect(Collectors.toList()));

			map = new HashMap<String, Object>();

			map.put("userDetails", sauDTO);

			responseDTO.getMeta().setCode(DefinicionesComunes.CODIGO_OK);
			responseDTO.getMeta().setMessage("Transacción completa");
			responseDTO.setResults(map);
		} catch (Exception e) {
			map.put("userDetails", null);
			responseDTO.getMeta().setCode("BD1000");
			responseDTO.getMeta().setMessage("Usuario no encontrado");
			responseDTO.getMeta().setType(e.getClass().getSimpleName());
			responseDTO.getMeta().setDetail(e.getMessage());
			responseDTO.setResults(map);
			log.error(responseDTO.getMeta().getMessage());
			log.error(responseDTO.getMeta().getDetail());
		}

		return responseDTO;
	}

	@Override
	@Transactional
	public ResponseDTO recordLastfailedattempt(String username) {

		HashMap<String, Object> map = new HashMap<String, Object>();
		ResponseDTO responseDTO = new ResponseDTO(new MetaDTO(0, DefinicionesComunes.CODIGO_OK), map);

		try {
			Users user = userDAO.getByUsername(username);

			int lastfailedattempt = 0;

			if (user.getFailedAttemps() == null) {
				lastfailedattempt = 1;
			} else {
				lastfailedattempt = user.getFailedAttemps() + 1;
			}

			user.setFailedAttemps(lastfailedattempt);

			user.setUpdateUsername(username);
			user.setUpdateDate(new Date());

			sauDAO.updateSAUPolicies(user);

			responseDTO.getMeta().setCode(DefinicionesComunes.CODIGO_OK);
			responseDTO.setResults(map);
		} catch (Exception e) {
			responseDTO.getMeta().setCode("BD1001");
			responseDTO.getMeta().setMessage("Error al registrar el ultimo intento fallido del usuario: " + username);
			responseDTO.getMeta().setType(e.getClass().getSimpleName());
			responseDTO.getMeta().setDetail(e.getMessage());
			responseDTO.setResults(map);
			log.error(responseDTO.getMeta().getMessage());
			log.error(responseDTO.getMeta().getDetail());
		}
		return responseDTO;
	}

	@Override
	@Transactional
	public ResponseDTO recordUserBlocked(String username) {

		HashMap<String, Object> map = new HashMap<String, Object>();
		ResponseDTO responseDTO = new ResponseDTO(new MetaDTO(0, DefinicionesComunes.CODIGO_OK), map);

		try {
			Users user = userDAO.getByUsername(username);

			user.setLockDate(new Date());
			// user.setHorabloqueo(UtileriasGenerales.getHoraEntero());
			user.setLastAccess(null);
			user.setEnabled(false);
			user.setUpdateDate(new Date());
			user.setUpdateUsername(username);

			userDAO.update(user);

			responseDTO.getMeta().setCode(DefinicionesComunes.CODIGO_OK);
			responseDTO.setResults(map);
		} catch (Exception e) {
			responseDTO.getMeta().setCode("BD3002");
			responseDTO.getMeta().setMessage("Error al bloquear el usuario: " + username);
			responseDTO.getMeta().setType(e.getClass().getSimpleName());
			responseDTO.getMeta().setDetail(e.getMessage());
			responseDTO.setResults(map);
			log.error(responseDTO.getMeta().getMessage());
			log.error(responseDTO.getMeta().getDetail());
		}

		return responseDTO;
	}

	@Override
	@Transactional
	public ResponseDTO recordLastAcces(String username) {

		HashMap<String, Object> map = new HashMap<String, Object>();
		ResponseDTO responseDTO = new ResponseDTO(new MetaDTO(0, DefinicionesComunes.CODIGO_OK), map);

		try {
			Users user = userDAO.getByUsername(username);

			user.setLastAccess(new Date());

			if (user.getFailedAttemps() == 0) {
				userDAO.mergeUsers(user);
			} else {
				user.setFailedAttemps(0);

				user.setUpdateUsername(username);
				user.setUpdateDate(new Date());

				userDAO.update(user);
			}

			responseDTO.getMeta().setCode(DefinicionesComunes.CODIGO_OK);
			responseDTO.setResults(map);

		} catch (Exception e) {
			responseDTO.getMeta().setCode("BD3003");
			responseDTO.getMeta().setMessage("Error al registrar el ultimo acceso. ");
			responseDTO.getMeta().setType(e.getClass().getSimpleName());
			responseDTO.getMeta().setDetail(e.getMessage());
			responseDTO.setResults(map);
			log.error(responseDTO.getMeta().getMessage());
			log.error(responseDTO.getMeta().getDetail());
		}

		return responseDTO;

	}

	@Override
	@Transactional
	public int changePassword(SecurityDTO DTO) {

		Users user = userDAO.getByUsername(DTO.getUsername());
		DTO = manageHistPasswords(DTO, user);

		SecurityPolicies sp = new SecurityPolicies();
		int spvalidate = sp.validatePasswordPoliciesNew(DTO, true);

		if (spvalidate != ErrorCodes.USER_OK) {
			return spvalidate;
		}

		user = manageUpdateUserPassword(user, DTO.getNewpassword());

		return ErrorCodes.USER_OK;
	}

	@Override
	@Transactional
	public int resetPassword(SecurityDTO DTO) {

		Users user = userDAO.getByUsername(DTO.getUsername());

		/**
		 * Valida token
		 */
		if (StringUtils.isNotBlank(user.getLastToken()) && StringUtils.isNotBlank(DTO.getTokenAnterior())
				&& user.getExpirationDateToken() != null && DTO.getTokenVencimiento() != null) {
			if (user.getLastToken().compareTo(DTO.getTokenAnterior()) == 0) {
				if (user.getExpirationDateToken().compareTo(DTO.getTokenVencimiento()) == 0) {
					if (new DateTime(user.getExpirationDateToken()).isAfter(DateTime.now())) {
						DTO = manageHistPasswords(DTO, user);

						SecurityPolicies sp = new SecurityPolicies();
						int spvalidate = sp.validatePasswordPoliciesNew(DTO, false);

						if (spvalidate != ErrorCodes.USER_OK) {
							return spvalidate;
						}

						user = manageUpdateUserPassword(user, DTO.getNewpassword());
					} else {
						return ErrorCodes.I_EXPIRED_JWT;
					}

				} else {
					return ErrorCodes.I_EXPIRED_JWT;
				}

			} else {
				return ErrorCodes.I_UNTRUSTED_JWT;
			}
		} else {
			return ErrorCodes.I_UNTRUSTED_JWT;
		}

		return ErrorCodes.USER_OK;
	}

	/**
	 *
	 * @param DTO
	 * @param user
	 * @return
	 */
	private SecurityDTO manageHistPasswords(SecurityDTO DTO, Users user) {

		DTO.setDtopassword(DTO.getNewpassword());
		DTO.setCurrentpassword(user.getPassword());

		List<String> passwordHistoryList = sauDAO.getPasswordHistoryListByUsername(user.getUsername()).stream()
				.map(PasswordHistory::getPassword).collect(Collectors.toList());

		if (passwordHistoryList.size() > 4)
			DTO.setPasswordhist5(passwordHistoryList.get(5));
		if (passwordHistoryList.size() > 3)
			DTO.setPasswordhist4(passwordHistoryList.get(3));
		if (passwordHistoryList.size() > 2)
			DTO.setPasswordhist3(passwordHistoryList.get(2));
		if (passwordHistoryList.size() > 1)
			DTO.setPasswordhist2(passwordHistoryList.get(1));
		if (passwordHistoryList.size() > 0)
			DTO.setPasswordhist1(passwordHistoryList.get(0));

		return DTO;
	}

	@Transactional
	@Override
	public ResponseDTO requestResetPassword(SecurityDTO DTO) {

		Users user = userDAO.getByUsername(DTO.getUsername());

		user.setLastToken(DTO.getTokenAnterior());
		user.setExpirationDateToken(DTO.getTokenVencimiento());
		user.setUpdateUsername(DTO.getUsuariomodificacion());
		user.setUpdateDate(DateTime.now().toDate());
		user.setResetRequestPassword("Reset Password Solicitado: " + DateTime.now().toString("dd/MM/YYYY HH:mm:ss"));

		userDAO.update(user);
		return new ResponseDTO(new MetaDTO(0, DefinicionesComunes.CODIGO_OK), new HashMap<>());
	}

	/**
	 * 
	 * manageUpdateUserPassword
	 * 
	 * @param user
	 * @param newPassword
	 * @return
	 */
	private Users manageUpdateUserPassword(Users user, String newPassword) {

		if (user.getLastPasswordChange() != null) {
			if (GenericFormatter.getDateWithFormat(user.getLastPasswordChange(), FormatosFechaValidos.MEDIUM_dmy2)
					.compareTo("01/01/1900") != 0) {
				PasswordHistory passwordHistory = new PasswordHistory();
				passwordHistory.setPassword(user.getPassword());
				passwordHistory.setRecordDate(DateTime.now().toDate());
				passwordHistory.setUsername(user);
				sauDAO.savePasswordHistory(passwordHistory);
			}
		}

		user.setPassword(passwordEncoder.encode(newPassword));

		user.setFailedAttemps(0);

		user.setLastPasswordChange(DateTime.now().toDate());
		user.setLastPasswordReset(DateTime.now().toDate());
		user.setUpdateUsername(user.getUsername());
		user.setUpdateDate(DateTime.now().toDate());
		user.setEnabled(true);
		user.setLockDate(null);

		userDAO.update(user);

		return user;
	}

}
