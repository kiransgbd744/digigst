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

import com.ey.advisory.app.docs.dto.erp.ApprovalStatusReqDto;
import com.ey.advisory.app.services.jobs.erp.Gstr1ApprovalReqRevIntegrationHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
public class Gstr1ApprovalReqRevIntegrationTestController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ApprovalReqRevIntegrationTestController.class);

	@Autowired
	@Qualifier("Gstr1ApprovalReqRevIntegrationHandler")
	private Gstr1ApprovalReqRevIntegrationHandler handler;

	@PostMapping(value = "/gstr1ApprovalRequestURL.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> approvalRequestURL(
			@RequestBody String jsonReq) {

		JsonObject resp = respObject("success");
		try {
			JsonObject requestObj = new JsonParser().parse(jsonReq)
					.getAsJsonObject().get("req").getAsJsonObject();
			Gson gson = GsonUtil.gsonInstanceWithExpose();
			ApprovalStatusReqDto dto = gson.fromJson(requestObj,
					ApprovalStatusReqDto.class);
			String groupcode = TestController.staticTenantId();

			/**
			 * destination name should be fetched from erp tables
			 */
			String destinationName = APIConstants.ERP_GSTR1_WORKFLOW;

			dto.setDestinationName(destinationName);
			dto.setGroupcode(groupcode);
			dto.setGstinIds(dto.getGstinIds());
			dto.setReturnPeriod(dto.getReturnPeriod());
			Integer respcodeOutward = handler.gsrt1ApprovalRequestToErp(dto);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Outward response code is {}", respcodeOutward);
			}

		} catch (Exception e) {
			LOGGER.error("Exception Occured:", e);
			resp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", e.getMessage())));
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
