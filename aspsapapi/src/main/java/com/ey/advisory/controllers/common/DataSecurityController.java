package com.ey.advisory.controllers.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.javatuples.Quartet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.controllers.anexure1.InputValidationUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstr2.userdetails.EntityService;
import com.ey.advisory.gstr2.userdetails.GstinDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class DataSecurityController {

	@Autowired
	@Qualifier("EntityServiceImpl")
	private EntityService entityService;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstNDetailRepository;

	/**
	 * Loads the entities for the currently logged in user.
	 * 
	 * @return the Json response.
	 */
	@RequestMapping(value = "/ui/getEntitiesForUser", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEntitiesForUser() {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Getting Entity Ids for the logged in user";
			LOGGER.debug(msg);
		}

		try {
			// Get the logged in user. If the user is not available, return
			// an error.
			User user = SecurityContext.getUser();
			if (user == null) {
				throw new AppException("User not logged in.");
			}

			// Get the group code for the currently logged in user.
			String groupCode = TenantContext.getTenantId();
			if (groupCode == null) {
				throw new AppException("Unable to identify "
						+ "the Group for the logged in user. "
						+ "Group Code = [%s]", groupCode);
			}

			// Get all entities for the group code.
			List<EntityInfoEntity> entityList = entityService
					.getAllEntities(groupCode);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Loaded entities for the "
								+ "group: '%s'. No: of entities = %d",
						groupCode, entityList.size());
				LOGGER.debug(msg);
			}

			// Get the data security map for the user and extract the entity
			// Ids applicable to the user.
			/*
			 * Map<Long, List<Pair<String, String>>> entityMap =
			 * user.getEntityMap();
			 */

			Map<Long, List<Quartet<String, String, String, String>>> entityMap = user
					.getEntityMap();
			Set<Long> entityIds = (entityMap != null) ? entityMap.keySet()
					: new HashSet<>();
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Entity Ids applicable for the user = [%s] ",
						StringUtils.join(entityIds));
				LOGGER.debug(msg);
			}

			Map<Long, Long> entityIdMap = entityIds.stream().collect(
					Collectors.toMap(Function.identity(), Function.identity()));

			List<EntityInfoEntity> retList = entityList.stream()
					.filter(o -> entityIdMap.containsKey(o.getId()))
					.collect(Collectors.toList());

			if (LOGGER.isDebugEnabled()) {
				String msg = "Filtered all EntityInfo objects "
						+ "belonging to the user.";
				LOGGER.debug(msg);
			}

			Gson gson = GsonUtil.newSAPGsonInstance();

			JsonObject gstinResp = new JsonObject();
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(retList);
			gstinResp.add("entities", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);

			if (LOGGER.isDebugEnabled()) {
				String msg = "Obtained all Entity Ids for the logged in "
						+ "user and created the response JSON";
				LOGGER.debug(msg);
			}

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			return InputValidationUtil.createJsonErrResponse(ex);
		}
	}

	@PostMapping(value = "/ui/getAllRegularGstins", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAllRegularGstins(
			@RequestBody String jsonParam) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		LOGGER.debug("inside getAllRegularGstins");
		try {

			JsonObject requestObject = (new JsonParser()).parse(jsonParam)
					.getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			Long entityId = reqObject.get("entityId") != null
					? reqObject.get("entityId").getAsLong() : null;
			if (entityId == null) {
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree("EntityId is  mandatory"));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

			}
			List<String> gstins = gstNDetailRepository
					.findgstinByEntityIdWithRegTypeForGstr1(entityId);

			List<GstinDto> vendorGstinList = gstins.stream()
					.map(e -> convertToDto(e))
					.collect(Collectors.toCollection(ArrayList::new));

			LOGGER.debug("Number of Reg gstins fetched {} ",
					vendorGstinList.size());
			JsonObject gstinBody = new JsonObject();
			JsonElement respBody = gson.toJsonTree(vendorGstinList);
			gstinBody.add("gstins", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Unexpected error while getting Reg gstins";
			LOGGER.error(msg, ex);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		}
	}

	private GstinDto convertToDto(String e) {
		GstinDto dto = new GstinDto();
		dto.setGstin(e);
		return dto;
	}
}
