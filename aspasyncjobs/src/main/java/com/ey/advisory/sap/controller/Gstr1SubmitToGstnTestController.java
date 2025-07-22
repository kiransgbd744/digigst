package com.ey.advisory.sap.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.entities.client.GstnSubmitEntity;
import com.ey.advisory.app.processors.handler.Gstr1SubmitToGstnJobHandler;
import com.ey.advisory.app.services.refidpolling.submit.SubmitBatchIdPollingManager;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.PollingMessage;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Slf4j
@RestController
public class Gstr1SubmitToGstnTestController {

	private static final String GROUP_CODE = TestController.staticTenantId();

	
	@Autowired
	private Gstr1SubmitToGstnJobHandler submitHandler;
	
	@Autowired
	@Qualifier("Gstr1SubmitBatchIdPollingManager")
	private SubmitBatchIdPollingManager batchManager;

	@PostMapping(value = "/gstr1SubmitToGstn",  produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public APIResponse submitToGstn(@RequestBody String jsonString) {

		APIResponse resp = submitHandler.submitGstinAndPeriod(jsonString,
				GROUP_CODE, APIConstants.SYSTEM);
		return resp;

	}
	
	@PostMapping(value = "/gstr1SubmitRefIdStatus",  produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void submitRefIdStatus(@RequestBody String jsonReq) {

		String groupcode = GROUP_CODE;

		if (jsonReq != null && groupcode != null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Pooling Gstr1 Submit ReturnStatus- Request is {} ",
						jsonReq);
			}
			Gson gson = new Gson();
			PollingMessage reqDto = gson.fromJson(jsonReq,
					PollingMessage.class);

			if (reqDto != null) {

				GstnSubmitEntity batch = new GstnSubmitEntity();
				batch.setId(reqDto.getBatchId());
				batch.setGstin(reqDto.getGstin());
				batch.setRetPeriod(reqDto.getTaxPeriod());
				batch.setRefId(reqDto.getRefId());
				batch.setReturnType(reqDto.getReturnType());

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Pooling Gstr1 ReturnStatus- batch is {} ",
							batch);
				}

				batchManager.processBatch(groupcode, batch);
			}

		}

	}

}
