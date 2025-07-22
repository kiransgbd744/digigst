/**
 * 
 */
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

import com.ey.advisory.app.docs.dto.erp.ApprovalStatusReqDto;
import com.ey.advisory.app.services.jobs.erp.Anx1ApprovalReqRevIntegrationHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author Hemasundar.J
 *
 */
@RestController
public class Anx1ApprovalReqRevIntegrationTestController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ApprovalReqRevIntegrationTestController.class);

	@Autowired
	private Anx1ApprovalReqRevIntegrationHandler handler;

	@PostMapping(value = "/anx1ApprovalRequestToErp", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> approvalRequestToErp(
			@RequestBody String jsonString) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("approvalRequestToErp method called");
		}
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.gsonInstanceWithExpose();
			ApprovalStatusReqDto dto = gson.fromJson(reqJson,
					ApprovalStatusReqDto.class);
			String groupcode = TestController.staticTenantId();
			/**
			 * destination name should be fetched from erp tables
			 */
			String destinationName = APIConstants.ERP_WORKFLOW;
			dto.setDestinationName(destinationName);
			dto.setGroupcode(groupcode);
			dto.setGstin(dto.getGstin());
			dto.setReturnPeriod(dto.getReturnPeriod());
			Integer respcode = handler.approvalRequestToErp(dto);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("respcode: {}", respcode);
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
