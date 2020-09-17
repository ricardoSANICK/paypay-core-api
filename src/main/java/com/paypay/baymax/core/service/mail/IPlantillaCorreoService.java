package com.paypay.baymax.core.service.mail;

import com.paypay.baymax.commons.DTO.mail.PlantillaCorreoDTO;
import com.paypay.baymax.commons.DTO.pagination.DataTableRequest;
import com.paypay.baymax.core.service.IGenericService;
import com.paypay.baymax.domain.mail.TPlantillaCorreo;

public interface IPlantillaCorreoService extends IGenericService<TPlantillaCorreo, String, PlantillaCorreoDTO> {

	Object getServerSideList(DataTableRequest<Object> dtReq);

}
