package com.paypay.baymax.core.controller.security;

import com.nxn.sau.security.util.JWTEncripter;
import com.paypay.baymax.commons.DTO.mail.PlantillaCorreoDTO;
import com.paypay.baymax.commons.DTO.pagination.DataTableRequest;
import com.paypay.baymax.commons.DTO.pagination.ResultsUtil;
import com.paypay.baymax.commons.DTO.security.PerfilUsuarioDTO;
import com.paypay.baymax.commons.DTO.security.UsersDTO;
import com.paypay.baymax.commons.DTO.security.UsersFormDTO;
import com.paypay.baymax.commons.DTO.util.MetaDTO;
import com.paypay.baymax.commons.DTO.util.ResponseDTO;
import com.paypay.baymax.commons.meta.EndPoints;
import com.paypay.baymax.commons.meta.ErrorCodes;
import com.paypay.baymax.commons.util.DefinicionesComunes;
import com.paypay.baymax.commons.util.GsonBuild;
import com.paypay.baymax.commons.util.Modulos;
import com.paypay.baymax.commons.util.UtileriasGenerales;
import com.paypay.baymax.commons.util.mail.CorreosUtils;
import com.paypay.baymax.core.service.mail.ICorreoService;
import com.paypay.baymax.core.service.mail.IPlantillaCorreoService;
import com.paypay.baymax.core.service.security.IGroupMembersService;
import com.paypay.baymax.core.service.security.IGroupsService;
import com.paypay.baymax.core.service.security.IUsersService;
import com.paypay.baymax.core.util.MailCoreUtils;
import com.paypay.baymax.domain.security.Group_members;
import com.paypay.baymax.domain.security.Users;

import org.apache.commons.collections4.CollectionUtils;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping(value = EndPoints.USERS)
@RestController
@Scope("request")
public class UsersController {

	private final String modulo = Modulos.ENTIDADES.get(Users.class.getSimpleName());
	public final Logger log = LoggerFactory.getLogger(this.getClass());
	private JWTEncripter jwt = new JWTEncripter(new GsonBuild().getGson());
	private final IUsersService usersService;
	private final IPlantillaCorreoService plantillaCorreoService;
	private final ICorreoService correoService;
	private final IGroupMembersService groupMemberService;
	private final IGroupsService groupService;

	@Autowired
	public UsersController(IUsersService usersService, IPlantillaCorreoService plantillaCorreoService,
			ICorreoService correoService, IGroupMembersService groupMemberService, IGroupsService groupService) {
		this.usersService = usersService;
		this.plantillaCorreoService = plantillaCorreoService;
		this.correoService = correoService;
		this.groupMemberService = groupMemberService;
		this.groupService = groupService;
	}

	@PostMapping(value = "/get")
	@ResponseStatus(HttpStatus.OK)
	public String getUserDTO(@RequestBody String json, HttpServletRequest request) {
		log.debug("Obteniendo " + this.modulo + " por ID...");

		HashMap<String, Object> mapa = new HashMap<String, Object>();
		String message = "Transacción completa";
		String code = DefinicionesComunes.CODIGO_OK;
		String type = null;
		String detail = null;

		try {
			UsersDTO usersDTO = usersService
					.getUserByUsername(jwt.fromJWT(json, new GsonBuild().getGsonDate(), UsersDTO.class).getUsername());
			UsersFormDTO usersFormDTO = usersDTO != null
					? usersService.getModelMapper().map(usersDTO, UsersFormDTO.class)
					: new UsersFormDTO();
			try {
				List<Group_members> gmList = groupMemberService.getGMByUsername(usersFormDTO.getUsername());
				List<Integer> gmIdList = gmList.stream().map(gm -> gm.getId()).collect(Collectors.toList());
				usersFormDTO.setGroups(groupService.getGroupByListID(gmIdList).stream().map(g -> g.getGroup_name())
						.collect(Collectors.joining(", ")));
			} catch (Exception e) {
				log.info(e.getMessage());
			}
			mapa.put("users", usersFormDTO);
			message = usersFormDTO == null ? (this.modulo + " no encontrado") : message;
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

	// ..........................................................................................................................
	// //

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public String getAll(HttpServletRequest request) {

		HashMap<String, Object> mapa = new HashMap<String, Object>();
		String message = "Transacción completa";
		String code = DefinicionesComunes.CODIGO_OK;
		String type = null;
		String detail = null;

		try {
			mapa.put("users", usersService.getList());
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

	@RequestMapping(value = "/getServerSideList", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public String getServerSideList(@RequestBody String json, HttpServletRequest request) {
		log.debug("Obteniendo Server Side List de " + this.modulo);

		HashMap<String, Object> mapa = new HashMap<String, Object>();
		String message = "Transacción completa";
		String code = DefinicionesComunes.CODIGO_OK;
		String type = null;
		String detail = null;

		try {
			@SuppressWarnings("unchecked")
			DataTableRequest<Object> dtReq = jwt.fromJWT(json, DataTableRequest.class);
			mapa.put("dtr", usersService.getServerSideList(dtReq));
		} catch (Exception e) {
			code = "BD";
			message = ErrorCodes.ERROR_LIST + " de " + this.modulo;
			type = e.getClass().getSimpleName();
			detail = e.getMessage();
			log.error(message + ": " + e.getMessage());
			mapa.put("dtr", ResultsUtil.getDtResultsForException());
		}

		return jwt.toJWT(new ResponseDTO(new MetaDTO(request.hashCode(), code, type, message, detail), mapa));
	}

	@RequestMapping(value = "/treeList", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public String getTreeList(@RequestBody String json, HttpServletRequest request) {
		log.debug("Obteniendo Tree List de " + this.modulo);

		HashMap<String, Object> mapa = new HashMap<String, Object>();
		String message = "Transacción completa";
		String code = DefinicionesComunes.CODIGO_OK;
		String type = null;
		String detail = null;

		try {
			mapa.put("treeList", usersService.getTreeList());
		} catch (Exception e) {
			code = "BD";
			message = ErrorCodes.ERROR_LIST + " de " + this.modulo;
			type = e.getClass().getSimpleName();
			detail = e.getMessage();
			log.error(message + ": " + e.getMessage());
			mapa.put("dtr", ResultsUtil.getDtResultsForException());
		}

		return jwt.toJWT(new ResponseDTO(new MetaDTO(request.hashCode(), code, type, message, detail), mapa));
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

			UsersDTO dto = jwt.fromJWT(json, UsersDTO.class);
			String newPass = usersService.addUser(dto);
			mapa.put("users", usersService.getModelMapper().map(dto, UsersFormDTO.class));

			code = this.sendMailNuevoUSuario(dto, newPass) ? DefinicionesComunes.CODIGO_OK : "ML";

		} catch (ConstraintViolationException e) {
			code = UtileriasGenerales.getRestrictionName(e);
			code = code.equals("ERROR") ? "BD" : code;
			code = code.contains("PK") ? "PK" : code;
			message = ErrorCodes.ERROR_SAVE + " de " + this.modulo;
			type = e.getClass().getSimpleName();
			detail = e.getMessage();
			log.error(message + ": " + e.getMessage());
		} catch (DataIntegrityViolationException e) {
			code = UtileriasGenerales.getRestrictionName(e);
			code = code.equals("ERROR") ? "BD" : code;
			code = code.contains("PK") ? "PK" : code;
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
			UsersDTO dto = jwt.fromJWT(json, UsersDTO.class);

			Users BD = usersService.get(dto.getUsername());

			BD.setCellphone(dto.getCellphone());
			BD.setTelephone(dto.getTelephone());
			BD.setEmail(dto.getEmail());
			BD.setEnabled(dto.getEnabled());
			BD.setLocked(dto.getEnabled());
			BD.setAvatar(dto.getAvatar());

			BD.setFirstName(dto.getFirstName());
			BD.setLastName(dto.getLastName());

			BD.setUpdateUsername(dto.getUpdateUsername());
			BD.setUpdateDate(DateTime.now().toDate());

//    		if(dto.getEnabled())
//        	{
//    			BD.setFechabloqueo(null);
//    			BD.setHorabloqueo(null);
//    			BD.setNumintentosfallidos(0);
//        	}
//        	else
//        	{
//        		BD.setFechabloqueo(new Date());
//        		BD.setHorabloqueo(UtileriasGenerales.getHoraEntero());
//        	}

			List<Group_members> listGM = groupMemberService.getGMByUsername(BD.getUsername());

			if (CollectionUtils.isNotEmpty(listGM)) {
				try {
					BD.setGroups(listGM.stream().map(g -> g.getGroup_id().getGroup_name())
							.collect(Collectors.joining(", ")));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			usersService.update(BD);

			mapa.put("users", usersService.getModelMapper().map(BD, UsersFormDTO.class));
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

	@PostMapping(value = "/updateStatus")
	@ResponseStatus(HttpStatus.OK)
	public String updateStatus(@RequestBody String json, HttpServletRequest request) {
		log.debug("Actualizando " + this.modulo + " del sistema...");

		HashMap<String, Object> mapa = new HashMap<String, Object>();
		String message = "Transacción completa";
		String code = DefinicionesComunes.CODIGO_OK;
		String type = null;
		String detail = null;

		try {
			UsersDTO dto = jwt.fromJWT(json, UsersDTO.class);

			Users BD = usersService.get(dto.getUsername());

			BD.setEnabled(dto.getEnabled());
			BD.setLocked(dto.getLocked());

			BD.setUpdateUsername(dto.getUpdateUsername());
			BD.setUpdateDate(DateTime.now().toDate());

			BD.setLockDate(dto.getEnabled() ? null : new Date());
			BD.setFailedAttemps(dto.getEnabled() ? 0 : BD.getFailedAttemps());

			usersService.update(BD);

			mapa.put("users", usersService.getModelMapper().map(BD, UsersFormDTO.class));
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

	@PostMapping(value = "/updatePerfil")
	@ResponseStatus(HttpStatus.OK)
	public String updatePerfil(@RequestBody String json, HttpServletRequest request) {
		log.debug("Actualizando perfil de " + this.modulo + " del sistema...");

		HashMap<String, Object> mapa = new HashMap<String, Object>();
		String message = "Transacción completa";
		String code = DefinicionesComunes.CODIGO_OK;
		String type = null;
		String detail = null;

		try {
			PerfilUsuarioDTO dto = jwt.fromJWT(json, PerfilUsuarioDTO.class);

			Users BD = usersService.get(dto.getUsername());

			BD.setAvatar(dto.getAvatar());

			BD.setUpdateUsername(dto.getUpdateUsername());
			BD.setUpdateDate(DateTime.now().toDate());

			usersService.update(BD);

			mapa.put("users", usersService.getModelMapper().map(BD, UsersFormDTO.class));
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

	@PostMapping(value = "/disable")
	@ResponseStatus(HttpStatus.OK)
	public String disable(@RequestBody String json, HttpServletRequest request) {
		log.debug("Deshabilitando" + this.modulo + " del sistema...");

		HashMap<String, Object> mapa = new HashMap<String, Object>();
		String message = "Transacción completa";
		String code = DefinicionesComunes.CODIGO_OK;
		String type = null;
		String detail = null;

		try {
			UsersDTO dto = jwt.fromJWT(json, UsersDTO.class);
			usersService.disableUser(dto);

			mapa.put("users", dto);
		} catch (Exception e) {
			code = "BD";
			message = ErrorCodes.ERROR_DISABLE + " de " + this.modulo;
			type = e.getClass().getSimpleName();
			detail = e.getMessage();
			log.error(message + ": " + e.getMessage());
		}

		return jwt.toJWT(new ResponseDTO(new MetaDTO(request.hashCode(), code, type, message, detail), mapa));
	}

	public boolean sendMailNuevoUSuario(UsersDTO user, String newPass) throws Exception {
		HashMap<String, String> mapValues = new HashMap<>();

		mapValues.put("\\$\\{USUARIO}", user.getUsername());
		mapValues.put("\\$\\{NOMBRECOMPLETO}", user.getFirstName());
		mapValues.put("\\$\\{PASSWORD}", newPass);

		try {
			PlantillaCorreoDTO plantilla = plantillaCorreoService
					.convertToDTO(plantillaCorreoService.get("NUEVOUSUARIO"));

			if (plantilla != null) {
				CorreosUtils correoUtils = new CorreosUtils();

				plantilla.setCc("");
				plantilla.setCco("");
				plantilla.setMultiListPerfilesDestinatarios("");
				plantilla.setDestinatariosOpcionales(user.getEmail());
				plantilla.setMapValues(mapValues);
				plantilla = correoUtils.procesarPlantilla(plantilla, null);
				correoService.add(MailCoreUtils.procesarCorreo(correoService, plantilla));
				return true;
			}
		} catch (Exception e) {
			return false;
		}

		return false;

	}
}