package com.ey.advisory.sap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.services.jobs.gstr1.Gstr1SummaryAtGstn;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * This is Dummy class to test the async Job Service(Gstr1 GET) 
 * service Through API.
 * 
 * @author Hemasundar.J
 *
 */
@Slf4j
@RestController
public class Gstr1GetGstnSummaryTestController {

	private static final String GROUP_CODE = TestController.staticTenantId();
	
	@Autowired
	@Qualifier("gstr1SummaryAtGstnImpl")
	private Gstr1SummaryAtGstn gstr1SummaryAtGstn;

	@PostMapping(value = "/gstnGstr1Summary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGst1GstnSumry(
			@RequestBody String jsonReq) {
		TenantContext.setTenantId(GROUP_CODE);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getGst1GstnSumry method called with args {}",
					jsonReq);
		}
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject requestObject = (new JsonParser()).parse(jsonReq)
				.getAsJsonObject();
		JsonObject reqObject = requestObject.get("req").getAsJsonObject();
		Gstr1GetInvoicesReqDto dto = gson.fromJson(reqObject,
				Gstr1GetInvoicesReqDto.class);
		String apiResp = gstr1SummaryAtGstn.getGstr1Summary(dto, GROUP_CODE);

		JsonObject resp1 = new JsonObject();
		JsonElement respBody = gson.toJsonTree(apiResp);
		resp1.add("resp", respBody);
		return new ResponseEntity<>(resp1.toString(), HttpStatus.OK);
	}

}
