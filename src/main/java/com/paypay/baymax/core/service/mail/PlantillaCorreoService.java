package com.paypay.baymax.core.service.mail;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paypay.baymax.commons.DTO.mail.PlantillaCorreoDTO;
import com.paypay.baymax.commons.DTO.pagination.DataTableRequest;
import com.paypay.baymax.commons.DTO.pagination.DataTableResults;
import com.paypay.baymax.core.service.GenericServiceImpl;
import com.paypay.baymax.core.service.IServerSideService;
import com.paypay.baymax.core.service.ServerSideCM;
import com.paypay.baymax.domain.mail.TPlantillaCorreo;
import com.paypay.baymax.commons.DTB.mail.PlantillaCorreoDTB;

@Service
public class PlantillaCorreoService extends GenericServiceImpl<TPlantillaCorreo, String, PlantillaCorreoDTO>
		implements IPlantillaCorreoService, IServerSideService<PlantillaCorreoDTB> {

	@Transactional(readOnly = true)
	public DataTableResults<PlantillaCorreoDTB> getServerSideList(DataTableRequest<Object> dtReq) {
		ServerSideCM<TPlantillaCorreo, PlantillaCorreoDTB> ssCM;
		ssCM = new ServerSideCM<>(new TPlantillaCorreo(), new PlantillaCorreoDTB(), "c", "clave",
				super.getGenericDao().getSessionFactory().getCurrentSession());

		return ssCM.getServerSideList(dtReq);
	}

}
