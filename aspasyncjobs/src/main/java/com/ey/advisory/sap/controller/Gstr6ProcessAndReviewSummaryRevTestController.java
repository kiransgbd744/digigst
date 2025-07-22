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
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
public class Gstr6ProcessAndReviewSummaryRevTestController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6ProcessAndReviewSummaryRevTestController.class);

	@Autowired
	@Qualifier("Gstr6ProcessAndReviewSummaryRevHandler")
	private Gstr6ProcessAndReviewSummaryRevHandler handler;

	@PostMapping(value = "/gstr6RevProcSumReqToErp.do", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getReviewSummaryReqToERP(
			@RequestBody String req) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {
			JsonObject reqObj = new JsonParser().parse(req).getAsJsonObject()
					.get("req").getAsJsonObject();
			RevIntegrationScenarioTriggerDto reqDto = gson.fromJson(reqObj,
					RevIntegrationScenarioTriggerDto.class);

			String groupcode = TestController.staticTenantId();
			TenantContext.setTenantId(groupcode);
			String destinationName = APIConstants.ERP_GSTR6_PROC_REVIEW_SUM;
			
			reqDto.setDestinationName(destinationName);
			reqDto.setGroupcode(groupcode);
			if (groupcode != null && destinationName != null
					&& reqDto.getEntityId() != null
					&& reqDto.getGstin() != null) {
				Integer respcode = handler.getProcessAndReviewSev(reqDto);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Review Summary response code is {}",
							respcode);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exceptions Occured:", e);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

	}
}
