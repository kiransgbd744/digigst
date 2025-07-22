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

import com.ey.advisory.app.services.jobs.erp.Ret1ProcessReviewSummaryHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
/**
 * 
 * @author Mahesh.Golla
 *
 */
@RestController
public class Ret1ProcessAndReviewSummaryTestController {

	private static Logger LOGGER = LoggerFactory
			.getLogger(Ret1ProcessAndReviewSummaryTestController.class);

	@Autowired
	@Qualifier("Ret1ProcessReviewSummaryHandler")
	private Ret1ProcessReviewSummaryHandler ret1ProcessReviewSummaryHandler;
	
	/*@Autowired
	@Qualifier("Ret1AProcessReviewSummaryHandler")
	private Ret1AProcessReviewSummaryHandler ret1AProcessReviewSummaryHandler;*/

	@PostMapping(value = "/ret1ProcessAndReviewSummary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> ret1ProcessAndReviewSummary(
			@RequestBody String jsonReq) {

		Gson gson = GsonUtil.gsonInstanceWithExpose();
		JsonObject resp = new JsonObject();
		try {
			JsonObject obj = new JsonParser().parse(jsonReq).getAsJsonObject()
					.get("req").getAsJsonObject();

			RevIntegrationScenarioTriggerDto reqDto = gson.fromJson(obj,
					RevIntegrationScenarioTriggerDto.class);

			// Overriding groupcode and destination for testing
			String groupcode = TestController.staticTenantId();
			TenantContext.setTenantId(groupcode);
			String destinationName = 
					APIConstants.ERP_RET1_REVIEW_AND_PROCESSED_SUMMARY;
			reqDto.setDestinationName(destinationName);
			reqDto.setGroupcode(groupcode);
			if (groupcode != null && destinationName != null
					&& reqDto.getEntityId() != null
					&& reqDto.getGstin() != null) {

				Integer respcode = 
						ret1ProcessReviewSummaryHandler.
						processReviewSummryToErp(reqDto,"RET1");
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Review Summary response code is {}",
							respcode);
				}
			}
			resp = respObject("success");
		} catch (Exception e) {
			LOGGER.error("Exception Occured:{} ", e);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
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
