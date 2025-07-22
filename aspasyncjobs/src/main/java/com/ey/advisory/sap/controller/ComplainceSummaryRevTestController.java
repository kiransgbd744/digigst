package com.ey.advisory.sap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.services.jobs.erp.ComplainceHistoryRevIntHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ComplainceSummaryRevTestController {

	@Autowired
	@Qualifier("ComplainceHistoryRevIntHandler")
	private ComplainceHistoryRevIntHandler handler;

	@PostMapping(value = "/complainceSummaryRev.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> ComplainceSummaryRev(
			@RequestBody String req) {

		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {
			JsonObject reqObj = new JsonParser().parse(req).getAsJsonObject()
					.get("req").getAsJsonObject();
			RevIntegrationScenarioTriggerDto dto = gson.fromJson(reqObj,
					RevIntegrationScenarioTriggerDto.class);
			// Overriding groupcode and destination for testing
			String groupcode = TestController.staticTenantId();
			TenantContext.setTenantId(groupcode);
			String destinationName = APIConstants.ERP_COMPLAINCE_HISTORY_SUMMARY_SUM;
			dto.setDestinationName(destinationName);
			dto.setGroupcode(groupcode);
			if (groupcode != null && destinationName != null
					&& dto.getEntityId() != null && dto.getGstin() != null) {
				Integer respcode = handler.complainceHistoryReqToErp(dto);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Review Summary response code is {}",
							respcode);
				}
			}
			resp = respObject("success");
		} catch (Exception e) {
			LOGGER.error("Exception Occured:{}", e);
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
