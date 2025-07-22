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
import com.ey.advisory.app.services.jobs.erp.Ret1ApprovalReqRevIntegrationHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
public class Ret1ApprovalReqRevIntegrationTestController {

	@Autowired
	@Qualifier("Ret1ApprovalReqRevIntegrationHandler")
	private Ret1ApprovalReqRevIntegrationHandler handler;
	private static Logger LOGGER = LoggerFactory
			.getLogger(Ret1ApprovalReqRevIntegrationTestController.class);

	@PostMapping(value = "/getRet1Approval.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getRet1Approval(@RequestBody String jsonReq) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {
			JsonObject reqObj = new JsonParser().parse(jsonReq)
					.getAsJsonObject().get("req").getAsJsonObject();
			ApprovalStatusReqDto reqDto = gson.fromJson(reqObj,
					ApprovalStatusReqDto.class);
			String groupCode = TestController.staticTenantId();

			// Destination name for Ret1
			String destinationName = APIConstants.ERP_RET1_APPROVAL;
			reqDto.setDestinationName(destinationName);
			reqDto.setGroupcode(groupCode);

			Integer statusApproval = handler.ret1ApprovalToErp(reqDto);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Status Approval: {}", statusApproval);
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured:{}", e);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

}
