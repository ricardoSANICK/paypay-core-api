package com.paypay.baymax.core.service.security;

import java.util.List;

import com.paypay.baymax.commons.DTO.pagination.DataTableRequest;
import com.paypay.baymax.commons.DTO.security.GroupsDTO;
import com.paypay.baymax.core.service.IGenericService;
import com.paypay.baymax.domain.security.Groups;

public interface IGroupsService extends IGenericService<Groups, Long, GroupsDTO> {

	Groups saveGroup(Groups group);

	List<GroupsDTO> getList();

	Object getServerSideList(DataTableRequest<Object> dtReq);

	List<Groups> getGroupByListID(List<Integer> listID);

}
