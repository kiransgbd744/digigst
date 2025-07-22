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

import com.ey.advisory.app.services.jobs.erp.Anx1AProcessAndReviewSummaryRevIntegHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
public class Anx1AProcessAndReviewSummaryRevIntegrationTestController {
	private static Logger LOGGER = LoggerFactory.getLogger(
			Anx1AProcessAndReviewSummaryRevIntegrationTestController.class);

	@Autowired
	@Qualifier("Anx1AProcessAndReviewSummaryRevIntegHandler")
	private Anx1AProcessAndReviewSummaryRevIntegHandler handler;

	@PostMapping(value = "/anx1AProcessAndReviewSummary", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> anx1AProcessAndReviewSummary(
			@RequestBody String jsonReq) {

		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {
			JsonObject reqObj = new JsonParser().parse(jsonReq)
					.getAsJsonObject().get("req").getAsJsonObject();
			RevIntegrationScenarioTriggerDto dto = gson.fromJson(reqObj,
					RevIntegrationScenarioTriggerDto.class);
			String groupCode = TestController.staticTenantId();
			String destinationName = APIConstants.ERP_ANX1A_PROC_REVIEW_SUM;
			dto.setDestinationName(destinationName);
			dto.setGroupcode(groupCode);
			if (groupCode != null &&  dto.getGstin() != null && dto.getDestinationName() != null
					&& dto.getEntityId() != null) {
				Integer success = handler.processReviewSummryToErp(dto);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Success: {}", success);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured:{} ", e);
			resp.add("hdr", new Gson()
					.toJsonTree(new APIRespDto("E", "Unexpected Eror")));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		resp = respObject("success");
		return new ResponseEntity(resp.toString(), HttpStatus.OK);
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
