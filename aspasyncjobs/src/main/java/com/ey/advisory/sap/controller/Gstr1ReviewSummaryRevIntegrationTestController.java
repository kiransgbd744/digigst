package com.ey.advisory.sap.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.services.jobs.erp.Gstr1ReviewSummaryReqRevIntegrationHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
public class Gstr1ReviewSummaryRevIntegrationTestController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1ReviewSummaryRevIntegrationTestController.class);

	@Autowired
	private Gstr1ReviewSummaryReqRevIntegrationHandler handler;

	@PostMapping(value = "/gstr1ReviewSummaryRequestToErp", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> reviewSummaryRequestToErp(
			@RequestBody String jsonString) {
		JsonObject resp = new JsonObject();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("reviewSummaryRequestToErp method called");
		}
		try {

			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.gsonInstanceWithExpose();
			RevIntegrationScenarioTriggerDto dto = gson.fromJson(reqJson,
					RevIntegrationScenarioTriggerDto.class);
			// Overriding groupcode and destination for testing
			String groupcode = TestController.staticTenantId();
			TenantContext.setTenantId(groupcode);
			String destinationName = APIConstants.ERP_GSTR1_REVIEW_AND_PROCESSED_SUMMARY;
			dto.setDestinationName(destinationName);
			dto.setGroupcode(groupcode);
			if (groupcode != null && destinationName != null
					&& dto.getEntityId() != null && dto.getGstin() != null) {
				Integer respcode = handler.reviewSummaryRequestToErp(dto);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Review Summary response code is {}",
							respcode);
				}
			}
			resp = respObject("success");
		} catch (Exception ex) {
			LOGGER.error("Unexpected Eror:", ex);
			resp.add("hdr", new Gson()
					.toJsonTree(new APIRespDto("E", ex.getMessage())));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	private JsonObject respObject(Object msg) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(msg);
		resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp.add("resp", respBody);
		return resp;
	}

}
