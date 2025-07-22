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

import com.ey.advisory.app.services.jobs.erp.OutwardPayloadMetadataRevIntHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.PayloadDocsRevIntegrationReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 
 * @author Umesha.M
 *
 */
@RestController
public class OutwardPayloadMetadataRevIntController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(OutwardPayloadMetadataRevIntController.class);

	@Autowired
	@Qualifier("OutwardPayloadMetadataRevIntHandler")
	private OutwardPayloadMetadataRevIntHandler handler;

	/**
	 * 
	 * @param jsonReq
	 * @return
	 */
	@PostMapping(value = "/getOutwardPayload", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getOutwardPayload(
			@RequestBody String jsonReq) {

		Gson gson = GsonUtil.gsonInstanceWithExpose();
		JsonObject resp = new JsonObject();
		try {
			JsonObject reqObj = new JsonParser().parse(jsonReq)
					.getAsJsonObject().get("req").getAsJsonObject();

			PayloadDocsRevIntegrationReqDto dto = gson.fromJson(reqObj,
					PayloadDocsRevIntegrationReqDto.class);
			String groupcode = TestController.staticTenantId();
			TenantContext.setTenantId(groupcode);
			dto.setGroupcode(groupcode);
			if (groupcode != null && dto.getPayloadId() != null) {
				Integer responseCode = handler.getPayloadMetadataDetails(dto,
						"OUTWARD");
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Response Code: {}", responseCode);
				}
			}
		} catch (Exception e) {
			LOGGER.debug("Excepion Occured:{}", e.getMessage());
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	/**
	 * 
	 * @param jsonReq
	 * @return
	 */
	@PostMapping(value = "/getInwardPayload", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getInwardPayload(
			@RequestBody String jsonReq) {

		Gson gson = GsonUtil.gsonInstanceWithExpose();
		JsonObject resp = new JsonObject();
		try {
			JsonObject reqObj = new JsonParser().parse(jsonReq)
					.getAsJsonObject().get("req").getAsJsonObject();

			PayloadDocsRevIntegrationReqDto dto = gson.fromJson(reqObj,
					PayloadDocsRevIntegrationReqDto.class);
			String groupcode = TestController.staticTenantId();
			TenantContext.setTenantId(groupcode);
			dto.setGroupcode(groupcode);
			if (groupcode != null && dto.getPayloadId() != null) {
				Integer responseCode = handler.getPayloadMetadataDetails(dto,
						"INWARD");
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Response Code: {}", responseCode);
				}
			}
		} catch (Exception e) {
			LOGGER.debug("Excepion Occured:{}", e.getMessage());
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}
}
