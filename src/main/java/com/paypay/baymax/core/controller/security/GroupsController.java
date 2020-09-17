package com.paypay.baymax.core.controller.security;

import com.nxn.sau.security.util.JWTEncripter;
import com.paypay.baymax.commons.DTO.security.GroupsDTO;
import com.paypay.baymax.commons.DTO.util.MetaDTO;
import com.paypay.baymax.commons.DTO.util.ResponseDTO;
import com.paypay.baymax.commons.meta.EndPoints;
import com.paypay.baymax.commons.meta.ErrorCodes;
import com.paypay.baymax.commons.util.DefinicionesComunes;
import com.paypay.baymax.commons.util.GsonBuild;
import com.paypay.baymax.commons.util.Modulos;
import com.paypay.baymax.commons.util.UtileriasGenerales;
import com.paypay.baymax.core.service.security.IGroupAuthoritiesService;
import com.paypay.baymax.core.service.security.IGroupMembersService;
import com.paypay.baymax.core.service.security.IGroupsService;
import com.paypay.baymax.domain.security.Group_authorities;
import com.paypay.baymax.domain.security.Group_members;
import com.paypay.baymax.domain.security.Groups;

import org.hibernate.exception.ConstraintViolationException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RequestMapping(value = EndPoints.GROUPS)
@RestController
@Scope("request")
public class GroupsController {

	private final String modulo = Modulos.ENTIDADES.get(Groups.class.getSimpleName());
	public final Logger log = LoggerFactory.getLogger(this.getClass());
	private JWTEncripter jwt = new JWTEncripter(new GsonBuild().getGson());
	private final IGroupsService groupsService;
	private final IGroupAuthoritiesService gaService;
	private final IGroupMembersService gmService;

	@Autowired
	public GroupsController(IGroupsService groupsService, IGroupAuthoritiesService gaService,
			IGroupMembersService gmService) {
		this.groupsService = groupsService;
		this.gaService = gaService;
		this.gmService = gmService;
	}

	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public String get(@PathVariable("id") String id, HttpServletRequest request) {
		log.debug("Obteniendo " + this.modulo + " por ID...");

		HashMap<String, Object> mapa = new HashMap<String, Object>();
		String message = "Transacción completa";
		String code = DefinicionesComunes.CODIGO_OK;
		String type = null;
		String detail = null;

		try {
			mapa.put("groups", groupsService.convertToDTO(groupsService.get(Long.parseLong(id))));
		} catch (Exception e) {
			code = "BD";
			message = ErrorCodes.ERROR_GET + " de " + this.modulo;
			type = e.getClass().getSimpleName();
			detail = e.getMessage();
			log.error(message + ": " + e.getMessage());
		}

		return jwt.toJWT(new ResponseDTO(new MetaDTO(request.hashCode(), code, type, message, detail), mapa));
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public String getAll(HttpServletRequest request) {

		HashMap<String, Object> mapa = new HashMap<String, Object>();
		String message = "Transacción completa";
		String code = DefinicionesComunes.CODIGO_OK;
		String type = null;
		String detail = null;

		try {
			mapa.put("groups", groupsService.getList());
		} catch (Exception e) {
			code = "BD";
			message = ErrorCodes.ERROR_GET + " de " + this.modulo;
			type = e.getClass().getSimpleName();
			detail = e.getMessage();
			log.error(message + ": " + e.getMessage());
		}
		return jwt.toJWT(new ResponseDTO(new MetaDTO(request.hashCode(), code, type, message, detail), mapa),
				new GsonBuild().getGsonDate());
	}

	@PostMapping(value = "/save")
	@ResponseStatus(HttpStatus.OK)
	public String save(@RequestBody String json, HttpServletRequest request) {
		log.debug("Registrando " + this.modulo + " en el sistema...");

		HashMap<String, Object> mapa = new HashMap<String, Object>();
		String message = "Transacción completa";
		String code = DefinicionesComunes.CODIGO_OK;
		String type = null;
		String detail = null;

		try {
			GroupsDTO dto = jwt.fromJWT(json, GroupsDTO.class);
			Groups BD = groupsService.convertToEntity(dto);

			BD.setRecordDate(DateTime.now().toDate());

			BD = groupsService.saveGroup(BD);

			mapa.put("groups", groupsService.convertToDTO(BD));

		} catch (ConstraintViolationException e) {
			code = UtileriasGenerales.getRestrictionName(e);
			code = code.equals("ERROR") ? "BD" : code;
			message = ErrorCodes.ERROR_SAVE + " de " + this.modulo;
			type = e.getClass().getSimpleName();
			detail = e.getMessage();
			log.error(message + ": " + e.getMessage());
		} catch (DataIntegrityViolationException e) {
			code = UtileriasGenerales.getRestrictionName(e);
			code = code.equals("ERROR") ? "BD" : code;
			message = ErrorCodes.ERROR_SAVE + " de " + this.modulo;
			type = e.getClass().getSimpleName();
			detail = e.getMessage();
			log.error(message + ": " + e.getMessage());
		} catch (Exception e) {
			code = "BD";
			message = ErrorCodes.ERROR_SAVE + " de " + this.modulo;
			type = e.getClass().getSimpleName();
			detail = e.getMessage();
			log.error(message + ": " + e.getMessage());
		}

		return jwt.toJWT(new ResponseDTO(new MetaDTO(request.hashCode(), code, type, message, detail), mapa));
	}

	@PostMapping(value = "/update")
	@ResponseStatus(HttpStatus.OK)
	public String update(@RequestBody String json, HttpServletRequest request) {
		log.debug("Actualizando " + this.modulo + " del sistema...");

		HashMap<String, Object> mapa = new HashMap<String, Object>();
		String message = "Transacción completa";
		String code = DefinicionesComunes.CODIGO_OK;
		String type = null;
		String detail = null;

		try {
			GroupsDTO dto = jwt.fromJWT(json, GroupsDTO.class);

			Groups BD = groupsService.get(dto.getId());
			Groups BDNew = groupsService.convertToEntity(dto);

			BDNew.setAssignedAuthorities(BD.getAssignedAuthorities());
			BDNew.setRecordUsername(BD.getRecordUsername());
			BDNew.setRecordDate(BD.getRecordDate());
			BDNew.setUpdateDate(DateTime.now().toDate());

			groupsService.update(BDNew);

			mapa.put("groups", groupsService.convertToDTO(BDNew));
		} catch (ConstraintViolationException e) {
			code = "UK";
			message = ErrorCodes.ERROR_UPDATE + " de " + this.modulo;
			type = e.getClass().getSimpleName();
			detail = e.getMessage();
			log.error(message + ": " + e.getMessage());
		} catch (DataIntegrityViolationException e) {
			code = "DT";
			message = ErrorCodes.ERROR_UPDATE + " de " + this.modulo;
			type = e.getClass().getSimpleName();
			detail = e.getMessage();
			log.error(message + ": " + e.getMessage());
		} catch (Exception e) {
			code = "BD";
			message = ErrorCodes.ERROR_UPDATE + " de " + this.modulo;
			type = e.getClass().getSimpleName();
			detail = e.getMessage();
			log.error(message + ": " + e.getMessage());
		}

		return jwt.toJWT(new ResponseDTO(new MetaDTO(request.hashCode(), code, type, message, detail), mapa));

	}

	@GetMapping(value = "/delete/{id}")
	@ResponseStatus(HttpStatus.OK)
	public String delete(@PathVariable("id") String id, HttpServletRequest request) {
		log.debug("Dando de baja " + this.modulo + " del sistema...");

		HashMap<String, Object> mapa = new HashMap<String, Object>();
		String message = "Transacción completa";
		String code = DefinicionesComunes.CODIGO_OK;
		String type = null;
		String detail = null;

		try {
			Groups group = groupsService.get(Long.parseLong(id));

			List<Group_authorities> permisos = new ArrayList<Group_authorities>();
			permisos = gaService.getGroupAuthoritiesListByGrupo(group.getId());

			for (Group_authorities permiso : permisos) {
				gaService.remove(permiso);
			}

			List<Group_members> miembros = new ArrayList<Group_members>();
			miembros = gmService.getGroupMembersListByGrupo(group.getId());

			for (Group_members miembro : miembros) {
				gmService.remove(miembro);
			}

			groupsService.remove(group);
		} catch (Exception e) {
			code = "BD";
			message = ErrorCodes.ERROR_DELETE + " de " + this.modulo;
			type = e.getClass().getSimpleName();
			detail = e.getMessage();
			log.error(message + ": " + e.getMessage());
		}

		return jwt.toJWT(new ResponseDTO(new MetaDTO(request.hashCode(), code, type, message, detail), mapa));
	}

}
