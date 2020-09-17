package com.paypay.baymax.core.util;

import org.joda.time.DateTime;

import com.paypay.baymax.commons.DTO.mail.CorreoDTO;
import com.paypay.baymax.commons.DTO.mail.PlantillaCorreoDTO;
import com.paypay.baymax.core.service.mail.ICorreoService;
import com.paypay.baymax.domain.mail.TCorreo;

public class MailCoreUtils {

	public static TCorreo procesarCorreo(ICorreoService correoService, PlantillaCorreoDTO plantilla) {
		
		CorreoDTO correoDTO = new CorreoDTO();
		correoDTO.setAsunto(plantilla.getAsunto());
		correoDTO.setCuerpo(plantilla.getCuerpo());
		correoDTO.setDe(plantilla.getRemitente());
		correoDTO.setEstatus("pendiente");
		correoDTO.setOrigen("paypay");
		correoDTO.setPara(plantilla.getDestinatariosOpcionales());
		TCorreo BD = correoService.convertToEntity(correoDTO);
		BD.setFechaalta(DateTime.now().toDate());
		return BD;
	}

}
