package com.ey.advisory.sap.controller;

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

import com.ey.advisory.app.services.daos.prsummary.Anx2PRSummaryRevHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
public class Anx2PRSummaryRevTestController {

	@Autowired
	@Qualifier("Anx2PRSummaryRevHandler")
	private Anx2PRSummaryRevHandler handler;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx2PRSummaryRevTestController.class);

	@PostMapping(value = "/getAnx2PRSummary.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getAnx2PRSummary(
			@RequestBody String jsonReq) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {
			JsonObject reqObj = new JsonParser().parse(jsonReq)
					.getAsJsonObject().get("req").getAsJsonObject();
			RevIntegrationScenarioTriggerDto dto = gson.fromJson(reqObj,
					RevIntegrationScenarioTriggerDto.class);
			// Group Code
			String groupCode = TestController.staticTenantId();
			TenantContext.setTenantId(groupCode);

			String destName = APIConstants.ERP_ANX2_PR_SUMMARY;
			dto.setDestinationName(destName);
			dto.setGroupcode(groupCode);
			if (groupCode != null && destName != null
					&& dto.getEntityId() != null && dto.getGstin() != null) {
				Integer value = handler.getAnx2PRSummary(dto);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Succes : {}", value);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured:{}", e);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}
}
