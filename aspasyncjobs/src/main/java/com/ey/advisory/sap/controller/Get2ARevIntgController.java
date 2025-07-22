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

import com.ey.advisory.app.docs.dto.erp.Get2AConsolidatedRevIntgDto;
import com.ey.advisory.app.services.jobs.erp.Get2ARevIntgHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
public class Get2ARevIntgController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Get2ARevIntgController.class);

	@Autowired
	@Qualifier("Get2ARevIntgHandler")
	private Get2ARevIntgHandler get2ARevIntgHandler;
	
	@PostMapping(value = "/get2ARevInt", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getEInvoiceDataStatus(
			@RequestBody String jsonReq) {
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		JsonObject resp = new JsonObject();
		try {
			JsonObject reqObj = new JsonParser().parse(jsonReq)
					.getAsJsonObject().get("req").getAsJsonObject();

			Get2AConsolidatedRevIntgDto dto = gson.fromJson(reqObj,
					Get2AConsolidatedRevIntgDto.class);
			String groupcode = TestController.staticTenantId();
			TenantContext.setTenantId(groupcode);
			String destinationName = APIConstants.ERP_GET2A_CONSOLIDATED;
			dto.setDestinationName(destinationName);
			dto.setGroupcode(groupcode);
			if (groupcode != null && destinationName != null
					&& dto.getEntityId() != null && dto.getGstin() != null) {
				Integer responseCode = get2ARevIntgHandler.get2AToErpPush(dto);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Response Code:{}", responseCode);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Excepion Occured:", e);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}
}
