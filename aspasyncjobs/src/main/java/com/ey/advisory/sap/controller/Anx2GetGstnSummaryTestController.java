/**
 * 
 */
package com.ey.advisory.sap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.anx2.Anx2GetInvoicesReqDto;
import com.ey.advisory.app.services.jobs.anx2.Anx2SummaryAtGstn;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Dibyakanta.sahoo
 *
 */
@RestController
@Slf4j
public class Anx2GetGstnSummaryTestController {

	private static final String GROUP_CODE = TestController.staticTenantId();

	@Autowired
	@Qualifier("Anx2SummaryAtGstnImpl")
	private Anx2SummaryAtGstn anx2SummaryAtGstn;

	@PostMapping(value = "/gstnAnx2Summary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAnx2GstnSumry(
			@RequestBody String jsonReq) {
		
		TenantContext.setTenantId(GROUP_CODE);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getAnx2GstnSumry method called with args {}",
					jsonReq);
		}
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject requestObject = (new JsonParser()).parse(jsonReq)
				.getAsJsonObject();
		JsonObject reqObject = requestObject.get("req").getAsJsonObject();
		Anx2GetInvoicesReqDto dto = gson.fromJson(reqObject,
				Anx2GetInvoicesReqDto.class);
		String apiResp = anx2SummaryAtGstn.getAnx2Summary(dto, GROUP_CODE);
		
		JsonObject resp1 = new JsonObject();
		JsonElement respBody = gson.toJsonTree(gson.toJson(apiResp));
		resp1.add("resp", respBody);
		return new ResponseEntity<>(resp1.toString(), HttpStatus.OK);
	}

}
