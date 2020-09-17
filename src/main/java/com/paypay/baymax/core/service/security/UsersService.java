package com.paypay.baymax.core.service.security;

import com.nxn.sau.security.util.PasswordGenerator;
import com.paypay.baymax.commons.DTO.pagination.DataTableRequest;
import com.paypay.baymax.commons.DTO.pagination.DataTableResults;
import com.paypay.baymax.commons.DTO.security.UsersDTO;
import com.paypay.baymax.commons.DTO.security.UsersFormDTO;
import com.paypay.baymax.commons.util.UtileriasGenerales;
import com.paypay.baymax.core.dao.security.IGroupMembersDAO;
import com.paypay.baymax.core.dao.security.IUsersDAO;
import com.paypay.baymax.core.service.GenericServiceImpl;
import com.paypay.baymax.core.service.IServerSideService;
import com.paypay.baymax.core.service.ServerSideCM;
import com.paypay.baymax.domain.security.Group_members;
import com.paypay.baymax.domain.security.Users;
import com.paypay.baymax.commons.DTB.security.UsersDTB;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsersService extends GenericServiceImpl<Users, String, UsersDTO>
		implements IUsersService, IServerSideService<UsersDTB> {

	@Autowired
	private IUsersDAO dao;
	@Autowired
	private IGroupMembersDAO gmDao;

	@Override
	@Transactional
	public UsersDTO getUserByUsername(String username) {
		return convertToDTO(dao.getByUsername(username));
	}

	@Override
	public List<UsersFormDTO> getAllList() {
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UsersDTO> getList() {
		return converToDTOList(dao.getList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<UsersDTO> getActiveList() {
		return converToDTOList(dao.getActiveList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<?> getTreeList() {
		return dao.getTreeList();
	}

	@Override
	@Transactional
	public String addUser(UsersDTO DTO) {
		Users BD = new Users();

		if (StringUtils.isBlank(DTO.getAvatar())) {
			DTO.setAvatar("/api/imageResources/user/ninguna/png");
		}

		BD = convertToEntity(DTO);

		BD.setLastPasswordChange(DateTime.now().toDate());
		BD.setFailedAttemps(0);

		BD.setRecordDate(new Date());
		BD.setRecordUsername(DTO.getRecordUsername());

		String newPass = PasswordGenerator.getRandomPassword(8);
		String encriptedPass = UtileriasGenerales.encryptPassword(newPass);
		BD.setPassword(encriptedPass);

		dao.add(BD);

		return newPass;
	}

	@Override
	@Transactional
	public void updateUser(UsersDTO DTO) {
		Users BD = dao.getByUsername(DTO.getUsername());

		BD = convertToEntity(DTO);

//    	if(BD.getEnabled())
//    	{
//    		BD.setFechabloqueo(null);
//    		BD.setHorabloqueo(null);
//    		BD.setNumintentosfallidos(0);
//    	}
//    	else
//    	{
//    		BD.setFechabloqueo(new Date());
//    		BD.setHorabloqueo(UtileriasGenerales.getHoraEntero());
//    	}

		List<Group_members> listGM = gmDao.getGMByUsername(DTO.getUsername());

		if (CollectionUtils.isNotEmpty(listGM)) {
			try {
				BD.setGroups(
						listGM.stream().map(g -> g.getGroup_id().getGroup_name()).collect(Collectors.joining(", ")));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		BD.setUpdateUsername(DTO.getUpdateUsername());

		BD.setUpdateDate(new Date(new Date().getTime()));

		dao.update(BD);
	}

	@Override
	@Transactional
	public void disableUser(UsersDTO DTO) {
		Users BD = dao.getByUsername(DTO.getUsername());

		BD.setEnabled(false);
		BD.setLocked(false);

//		BD.setFechabloqueo(new Date());
//		BD.setHorabloqueo(UtileriasGenerales.getHoraEntero());

		BD.setUpdateDate(new Date());

		BD.setUpdateUsername(DTO.getUpdateUsername());

		dao.update(BD);
	}

	@Override
	@Transactional
	public UsersDTO resetPasswordUser(String username, String updateUsername, String newPass) {
		Users user = dao.getByUsername(username);

//    	user.setNumintentosfallidos(0);
//    	user.setFechabloqueo(null);
//    	user.setHorabloqueo(null);

//    	if(user.getLastpasswordchange() != null){
//        	if( GenericFormatter.getDateWithFormat(user.getLastpasswordchange(), FormatosFechaValidos.MEDIUM_dmy2).compareTo("01/01/1900") != 0){
//        		user.setLastpasswordchange(new DateTime(1900, 1, 1, 1, 1).toDate());
//        		user.setPasswordhist5(user.getPasswordhist4());
//    			user.setPasswordhist4(user.getPasswordhist3());
//    			user.setPasswordhist3(user.getPasswordhist2());
//    			user.setPasswordhist2(user.getPasswordhist1());
//    			user.setPasswordhist1(user.getPassword());
//        	}
//    	} else {
//    		user.setLastpasswordchange(new DateTime(1900, 1, 1, 1, 1).toDate());
//    	}
//
//    	user.setLastaccess(new Date());
//    	user.setLastpasswordreset(new Date());
//    	user.setLasthourpasswordreset(UtileriasGenerales.getHoraEntero());
		user.setEnabled(true);
		user.setPassword(UtileriasGenerales.encryptPassword(newPass));

		user.setUpdateUsername(updateUsername);
		user.setUpdateDate(new Date());

		dao.update(user);

		return convertToDTO(user);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existUser(String user) {
		return dao.existUser(user);
	}

	@Transactional(readOnly = true)
	public DataTableResults<UsersDTB> getServerSideList(DataTableRequest<Object> dtReq) {
		ServerSideCM<Users, UsersDTB> ssCM;
		ssCM = new ServerSideCM<>(new Users(), new UsersDTB(), "u", "username",
				super.getGenericDao().getSessionFactory().getCurrentSession());

		return ssCM.getServerSideList(dtReq);
	}
}
