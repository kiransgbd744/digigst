package com.ey.advisory.controller.gstr7;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.services.onboarding.DataSecurityService;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.DataSecurityAttriUserRespDto;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
public class Gstr7GetDataSecurityController {

	@Autowired
	@Qualifier("dataSecurityService")
	private DataSecurityService dataSecurityService;

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr7GetDataSecurityController.class);

	@PostMapping(value = { "/ui/getGstr7DataSecForUser"}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getGstr7DataSecForUser(
			@RequestBody String reqJson) {
		LOGGER.debug(
				"Gstr7GetDataSecurityController getGstr7DataSecForUser begin");
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject reqObj = JsonParser.parseString(reqJson)
					.getAsJsonObject().get("req").getAsJsonObject();
			RevIntegrationScenarioTriggerDto dto = gson.fromJson(reqObj,
					RevIntegrationScenarioTriggerDto.class);
			List<DataSecurityAttriUserRespDto> getDataSecurity = new ArrayList<>();
			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr7GetDataSecurityController User Name: {}",
						userName);
			}
			Long entityId = dto.getEntityId();
			List<Long> listEntities = new ArrayList<>();
			if (entityId != null) {
				listEntities.add(entityId);
			}
			getDataSecurity = dataSecurityService
					.getDataPermissionForUser(listEntities, userName, "TDS");

			if (getDataSecurity != null && !getDataSecurity.isEmpty()) {
				JsonElement respBody = gson.toJsonTree(getDataSecurity);
				resp.add("resp", respBody);
			} else {
				resp.add("resp", new Gson()
						.toJsonTree(new APIRespDto("S", "No Record Found")));
			}

		} catch (

		Exception e) {
			resp.add("resp",
					new Gson().toJsonTree(new APIRespDto("E", e.getMessage())));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr6GetDataSecurityController getGstr6DataSecForUser end");
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}
}
