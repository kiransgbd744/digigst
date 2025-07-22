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

import com.ey.advisory.app.services.jobs.erp.EInvoiceDataStatusReqRevIntHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
public class EInvoiceDataStatusReqRevIntController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(EInvoiceDataStatusReqRevIntController.class);

	@Autowired
	@Qualifier("EInvoiceDataStatusReqRevIntHandler")
	private EInvoiceDataStatusReqRevIntHandler einvoiceDataStatusReqRevIntHandler;

	@PostMapping(value = "/getEInvoiceDataStatus", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getEInvoiceDataStatus(
			@RequestBody String jsonReq) {

		Gson gson = GsonUtil.gsonInstanceWithExpose();
		JsonObject resp = new JsonObject();
		try {
			JsonObject reqObj = new JsonParser().parse(jsonReq)
					.getAsJsonObject().get("req").getAsJsonObject();

			RevIntegrationScenarioTriggerDto dto = gson.fromJson(reqObj,
					RevIntegrationScenarioTriggerDto.class);
			String groupcode = TestController.staticTenantId();
			TenantContext.setTenantId(groupcode);
			String destinationName = APIConstants.ERP_EINVOICE_DATA_STATUS;
			dto.setDestinationName(destinationName);
			dto.setGroupcode(groupcode);
			if (groupcode != null && destinationName != null
					&& dto.getEntityId() != null && dto.getGstin() != null) {
				Integer responseCode = einvoiceDataStatusReqRevIntHandler
						.dataStatusToERP(dto);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Response Code: {}", responseCode);
				}
			}
		} catch (Exception e) {
			LOGGER.debug("Excepion Occured:",e);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}
}
