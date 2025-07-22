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

import com.ey.advisory.app.services.jobs.erp.Anx1ReviewSummaryReqRevIntegrationHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
public class Anx1ReviewSummaryRevIntegrationTestController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ReviewSummaryRevIntegrationTestController.class);

	@Autowired
	private Anx1ReviewSummaryReqRevIntegrationHandler handler;

	@PostMapping(value = "/anx1ReviewSummaryRequestToErp", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> reviewSummaryRequestToErp(
			@RequestBody String jsonString) {

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
			String destinationName = APIConstants.ERP_ANX1_REVIEW_SUMMARY;
			dto.setDestinationName(destinationName);
			dto.setGroupcode(groupcode);
			if (groupcode != null && destinationName != null
					&& dto.getEntityId() != null && dto.getGstin() != null) {
				Integer respcodeOutward = handler
						.reviewSummaryRequestToErp(dto);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Response code  is {}", respcodeOutward);
				}
			}
			JsonObject resp = respObject("success");
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			LOGGER.error("Unexpected Eror", ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson()
					.toJsonTree(new APIRespDto("E", "Unexpected Eror")));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public JsonObject respObject(Object msg) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(msg);
		resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp.add("resp", respBody);
		return resp;
	}

}
