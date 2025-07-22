package com.ey.advisory.sap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.services.jobs.erp.BCAPIPaylodRevereseFeedHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.PayloadDocsRevIntegrationReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class BCAPIPaylodRevereseFeedController {

	@Autowired
	@Qualifier("BCAPIPaylodRevereseFeedHandler")
	private BCAPIPaylodRevereseFeedHandler handler;

	@PostMapping(value = "/BCAPIPaylodRevereseFeed", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getBCAPIPayloadRevFedd(
			@RequestBody String req) {

		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {
			JsonObject reqObj = new JsonParser().parse(req).getAsJsonObject()
					.get("req").getAsJsonObject();

			PayloadDocsRevIntegrationReqDto dto = gson.fromJson(reqObj,
					PayloadDocsRevIntegrationReqDto.class);
			String groupcode = TestController.staticTenantId();
			TenantContext.setTenantId(groupcode);
			dto.setGroupcode(groupcode);
			if (groupcode != null && dto.getPayloadId() != null) {
				Integer responseCode = handler.bcapiPaylodToErp(dto);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Response Code: {}", responseCode);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured", e);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}
}
