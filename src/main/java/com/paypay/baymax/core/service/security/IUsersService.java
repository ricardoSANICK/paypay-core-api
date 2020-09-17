package com.paypay.baymax.core.service.security;

import java.util.List;

import com.paypay.baymax.commons.DTO.pagination.DataTableRequest;
import com.paypay.baymax.commons.DTO.pagination.DataTableResults;
import com.paypay.baymax.commons.DTO.security.UsersDTO;
import com.paypay.baymax.commons.DTO.security.UsersFormDTO;
import com.paypay.baymax.core.service.IGenericService;
import com.paypay.baymax.domain.security.Users;
import com.paypay.baymax.commons.DTB.security.UsersDTB;

public interface IUsersService extends IGenericService<Users, String, UsersDTO> {
	
	public UsersDTO getUserByUsername(String username);

	List<UsersFormDTO> getAllList();

	public List<UsersDTO> getList();

	public List<UsersDTO> getActiveList();

	List<?> getTreeList();

	public String addUser(UsersDTO DTO);

	public void updateUser(UsersDTO DTO);

	public void disableUser(UsersDTO DTO);

	public UsersDTO resetPasswordUser(String username, String updateUsername, String newPass);

	public boolean existUser(String user);

	public DataTableResults<UsersDTB> getServerSideList(DataTableRequest<Object> dtReq);
	
}
