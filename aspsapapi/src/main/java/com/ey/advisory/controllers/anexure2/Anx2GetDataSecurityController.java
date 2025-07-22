package com.ey.advisory.controllers.anexure2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.services.onboarding.DataSecurityService;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.DataSecurityAttriUserRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@RestController
public class Anx2GetDataSecurityController {

	@Autowired
	@Qualifier("dataSecurityService")
	private DataSecurityService dataSecurityService;

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx2GetDataSecurityController.class);

	@PostMapping(value = { "/ui/getDataSecurityForUser",
			"/itp/getDataSecurityForUser" }, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getDataSecurityForUser() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Anx2GetDataSecurityController getDataSecurityForUser begin");
		}
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			List<DataSecurityAttriUserRespDto> getDataSecurity = dataSecurityService
					.getDataPermissionForUser("REGULAR");
			if (getDataSecurity != null && !getDataSecurity.isEmpty()) {
				JsonElement respBody = gson.toJsonTree(getDataSecurity);
				resp.add("resp", respBody);
			} else {
				resp.add("resp", new Gson()
						.toJsonTree(new APIRespDto("S", "No Record Found")));
			}

		} catch (Exception e) {
			LOGGER.error("Exception Occured:", e);
			resp.add("resp",
					new Gson().toJsonTree(new APIRespDto("E", e.getMessage())));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Anx2GetDataSecurityController getDataSecurityForUser end");
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/ui/getDataSecurityForUserDashboard", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getDataSecurityForUser1() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Anx2GetDataSecurityController getDataSecurityForUserDashboard begin");
		}
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			List<DataSecurityAttriUserRespDto> getDataSecurity = new ArrayList<>();
			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Anx2GetDataSecurityController User Name: {}",
						userName);
			}
			Map<Long, Map<String, List<Pair<Long, String>>>> attributeMap = user
					.getAttributeMap();

			Set<Long> entityIds = attributeMap.keySet();
			List<Long> listEntities = new ArrayList<>();
			if (entityIds != null && !entityIds.isEmpty()) {

				entityIds.forEach(entityId -> {
					listEntities.add(entityId);
				});
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Anx2GetDataSecurityController User Name: {}",
							userName);
				}
				getDataSecurity = dataSecurityService
						.getDataPermissionForUser(listEntities, userName, null);
			}
			if (getDataSecurity != null && !getDataSecurity.isEmpty()) {
				JsonElement respBody = gson.toJsonTree(getDataSecurity);
				resp.add("resp", respBody);
			} else {
				resp.add("resp", new Gson()
						.toJsonTree(new APIRespDto("S", "No Record Found")));
			}

		} catch (Exception e) {
			resp.add("resp",
					new Gson().toJsonTree(new APIRespDto("E", e.getMessage())));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Anx2GetDataSecurityController getDataSecurityForUser end");
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}
}
