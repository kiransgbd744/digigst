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

import com.ey.advisory.app.services.jobs.erp.Gstr6ProcessAndReviewSummaryRevHandler;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
public class Gstr6AProcAndRevSumRevTestController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6AProcAndRevSumRevTestController.class);

	@Autowired
	@Qualifier("Gstr6ProcessAndReviewSummaryRevHandler")
	private Gstr6ProcessAndReviewSummaryRevHandler handler;

	@PostMapping(name = "/getGstr6AProcAndRevSummary", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getGstr6AProcAndRevSummary(
			@RequestBody String req) {
		JsonObject json = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();

		try {
			JsonObject reqObject = new JsonParser().parse(req).getAsJsonObject()
					.get("req").getAsJsonObject();
			RevIntegrationScenarioTriggerDto reqDto = gson.fromJson(reqObject,
					RevIntegrationScenarioTriggerDto.class);
			String destName = APIConstants.ERP_GSTR6A_PROC_REVIEW_SUM;

			String groupCode = TestController.staticTenantId();
			TenantContext.setTenantId(groupCode);

			reqDto.setDestinationName(destName);
			reqDto.setGroupcode(groupCode);
			if (reqDto.getDestinationName() != null
					&& reqDto.getEntityId() != null
					&& reqDto.getGroupcode() != null) {
				Integer respCode = handler.getProcessAndReviewSev(reqDto);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Response Code:{}", respCode);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occred:", e);
		}
		return new ResponseEntity<>(json.toString(), HttpStatus.OK);
	}
}