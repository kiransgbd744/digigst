/**
 * 
 */
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

import com.ey.advisory.app.services.jobs.anx1.Anx1SummaryAtGstn;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.Anx1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author Laxmi.Salukuti
 *
 */
@RestController
public class Anx1GetGstnSummaryTestController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1GetGstnSummaryTestController.class);

	private static final String GROUP_CODE = TestController.staticTenantId();

	@Autowired
	@Qualifier("Anx1SummaryAtGstnImpl")
	private Anx1SummaryAtGstn anx1SummaryAtGstn;

	@PostMapping(value = "/gstnAnx1Summary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAnx1GstnSumry(
			@RequestBody String jsonReq) {
		
		TenantContext.setTenantId(GROUP_CODE);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getAnx1GstnSumry method called with args {}",
					jsonReq);
		}
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject requestObject = (new JsonParser()).parse(jsonReq)
				.getAsJsonObject();
		JsonObject reqObject = requestObject.get("req").getAsJsonObject();
		Anx1GetInvoicesReqDto dto = gson.fromJson(reqObject,
				Anx1GetInvoicesReqDto.class);
		String apiResp = anx1SummaryAtGstn.getAnx1Summary(dto, GROUP_CODE);
		
		JsonObject resp1 = new JsonObject();
		JsonElement respBody = gson.toJsonTree(gson.toJson(apiResp));
		resp1.add("resp", respBody);
		return new ResponseEntity<>(resp1.toString(), HttpStatus.OK);
	}

}
