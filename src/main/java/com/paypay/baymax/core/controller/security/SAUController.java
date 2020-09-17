package com.paypay.baymax.core.controller.security;

import com.google.gson.Gson;
import com.nxn.sau.security.DTO.SecurityDTO;
import com.nxn.sau.security.ErrorCodes;
import com.nxn.sau.security.util.JWTEncripter;
import com.paypay.baymax.commons.DTO.util.MetaDTO;
import com.paypay.baymax.commons.DTO.util.ResponseDTO;
import com.paypay.baymax.commons.util.DefinicionesComunes;
import com.paypay.baymax.commons.util.GsonBuild;
import com.paypay.baymax.core.service.security.sau.ISAUService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@RequestMapping(value = "/sau")
@RestController
@Scope("request")
public class SAUController {

	private final Gson gson = new GsonBuild().getGson();
	private final JWTEncripter jwt = new JWTEncripter();
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private final ISAUService sauService;

	@Autowired
	public SAUController(ISAUService sauService) {
		this.sauService = sauService;
	}

	@RequestMapping(value = "/securityPoliciesUser/{tipo}/{username:.+}/{usuariomodificacion:.+}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public String securityPoliciesUser(@PathVariable String tipo, @PathVariable String username,
			@PathVariable String usuariomodificacion, HttpServletRequest request) {
		log.debug("Obtener las politicas de seguridad asignadas al usuario seleccionado...");
		log.debug("/securityPoliciesUser/" + tipo + "/" + username + "/" + usuariomodificacion);

		HashMap<String, Object> mapa = new HashMap<String, Object>();
		String message = "Transacción completa";
		String code = DefinicionesComunes.CODIGO_OK;
		String type = null;
		String detail = null;

		ResponseDTO jsdtoResp = new ResponseDTO();

		try {
			switch (tipo.toLowerCase()) {
			case "lastfailedattempt":
				jsdtoResp = sauService.recordLastfailedattempt(username);
				break;
			case "bloquearusuario":
				jsdtoResp = sauService.recordUserBlocked(username);
				break;
			case "lastaccess":
				jsdtoResp = sauService.recordLastAcces(username);
				break;
			case "userdetails":
				jsdtoResp = sauService.getUserDetailsByUsername(username);
				break;
			}

			return jwt.toJWT(jsdtoResp, gson);

		} catch (Exception e) {

			message = "Error al intentar verificar las politicas de seguridad asignadas al usuario " + username;
			type = e.getClass().getSimpleName();
			detail = e.getMessage();
			code = "BD3004";
			log.error(message + ". Error: " + e.getMessage());
			return jwt.toJWT(new ResponseDTO(new MetaDTO(request.hashCode(), code, type, message, detail), mapa), gson);
		}

	}

	@RequestMapping(value = "/changePassword", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public String changePassword(@RequestBody String json, HttpServletRequest request) {
		log.debug("Cambiar password...");

		HashMap<String, Object> mapa = new HashMap<String, Object>();
		String message = "Transacción completa";
		String code = DefinicionesComunes.CODIGO_OK;
		String type = null;
		String detail = null;

		try {

			SecurityDTO SecDTO = jwt.fromJWT(json, gson, SecurityDTO.class);

			int spvalidate = sauService.changePassword(SecDTO);

			if (spvalidate == ErrorCodes.INVALID_PASSWORD) {
				code = ErrorCodes.DIFF_OLD_PASSWORD;
			}

			code = this.validatePasswordRules(code, spvalidate);

			return jwt.toJWT(new ResponseDTO(new MetaDTO(request.hashCode(), code), mapa), gson);

		} catch (Exception e) {
			message = "Error al cambiar el password. ";
			type = e.getClass().getSimpleName();
			detail = e.getMessage();
			code = DefinicionesComunes.CODIGO_ERROR;

			log.error(message + ": " + detail);

			return jwt.toJWT(new ResponseDTO(new MetaDTO(request.hashCode(), code, type, message, detail), mapa), gson);
		}

	}

	@RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public String resetPassword(@RequestBody String json, HttpServletRequest request) {
		log.debug("Reset password...");

		HashMap<String, Object> mapa = new HashMap<String, Object>();
		String message = "Transacción completa";
		String code = DefinicionesComunes.CODIGO_OK;
		String type = null;
		String detail = null;

		try {

			SecurityDTO SecDTO = jwt.fromJWT(json, new GsonBuild().getGsonDate(), SecurityDTO.class);

			int spvalidate = sauService.resetPassword(SecDTO);

			code = this.validatePasswordRules(code, spvalidate);

			if (spvalidate == ErrorCodes.I_EXPIRED_JWT) {
				code = ErrorCodes.EXPIRED_JWT;
			}

			if (spvalidate == ErrorCodes.I_MALFORMED_JWT) {
				code = ErrorCodes.MALFORMED_JWT;
			}

			if (spvalidate == ErrorCodes.I_UNTRUSTED_JWT) {
				code = ErrorCodes.UNTRUSTED_JWT;
			}

			return jwt.toJWT(new ResponseDTO(new MetaDTO(request.hashCode(), code), mapa), gson);

		} catch (Exception e) {
			message = "Error al cambiar el password. ";
			type = e.getClass().getSimpleName();
			detail = e.getMessage();
			code = DefinicionesComunes.CODIGO_ERROR;

			log.error(message + ": " + detail);

			return jwt.toJWT(new ResponseDTO(new MetaDTO(request.hashCode(), code, type, message, detail), mapa), gson);
		}
	}

	@RequestMapping(value = "/requestResetPassword", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public String requestResetPassword(@RequestBody String json, HttpServletRequest request) {

		HashMap<String, Object> mapa = new HashMap<String, Object>();
		String message = "Transacción completa";
		String code = DefinicionesComunes.CODIGO_OK;
		String type = null;
		String detail = null;

		try {
			SecurityDTO SecDTO = jwt.fromJWT(json, new GsonBuild().getGsonDate(), SecurityDTO.class);

			return jwt.toJWT(sauService.requestResetPassword(SecDTO), gson);
		} catch (Exception e) {
			message = "Error al solicitar el reset de password ";
			type = e.getClass().getSimpleName();
			detail = e.getMessage();
			code = DefinicionesComunes.CODIGO_ERROR;
			log.error(message + ": " + detail);
		}

		return jwt.toJWT(new ResponseDTO(new MetaDTO(request.hashCode(), code, type, message, detail), mapa), gson);
	}

	private String validatePasswordRules(String code, int spvalidate) {

		if (spvalidate == ErrorCodes.INVALID_PASSWORD_RULES_SAME_OLD) {
			code = ErrorCodes.SAME_NEW_AND_OLD_PASSWORD;
		}

		if (spvalidate == ErrorCodes.INVALID_PASSWORD_RULES_SAME_NEW) {
			code = ErrorCodes.SAME_NEW_NEW_PASSWORD;
		}

		if (spvalidate == ErrorCodes.INVALID_PASSWORD_RULES_ALFANUMERIC) {
			code = ErrorCodes.INVALID_PASSWORD_ALFANUMERIC;
		}

		if (spvalidate == ErrorCodes.INVALID_PASSWORD_RULES_MIN_LEN) {
			code = ErrorCodes.INVALID_PASSWORD_MIN_LEN;
		}

		if (spvalidate == ErrorCodes.INVALID_PASSWORD_RULES_HIST) {
			code = ErrorCodes.SAME_NEW_HIST_PASSWORD;
		}

		return code;
	}

}