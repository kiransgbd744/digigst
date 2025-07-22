package com.ey.advisory.sap.controller;

import java.lang.reflect.Type;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.services.jobs.ret.RetInvoicesAtGstn;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.RetGetInvoicesReqDto;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * This is Dummy class to test the async Job Service(Ret1 GET) service Through
 * API.
 * 
 * @author Anand3.M
 *
 */
@RestController
@Slf4j
public class RetGetGstnInvoicesTestController {

	private static final String GROUP_CODE = TestController.staticTenantId();
	private static final String LOG1 = "{} method called with arg {}";

	@Autowired
	@Qualifier("RetGetInvoicesAtGstnImpl")
	private RetInvoicesAtGstn retInvoicesAtGstn;

	@PostMapping(value = { "/retGetGstn" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> retDataFromGstn(
			@RequestBody String jsonReq, HttpServletRequest request) {
		TenantContext.setTenantId(GROUP_CODE);
		LOGGER.debug(LOG1, "RetGetGstn", jsonReq);
		JsonObject requestObject = (new JsonParser()).parse(jsonReq)
				.getAsJsonObject();
		JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();

		Gson gson = GsonUtil.newSAPGsonInstance();
		Type listType = new TypeToken<List<RetGetInvoicesReqDto>>() {
		}.getType();
		List<RetGetInvoicesReqDto> dtos = gson.fromJson(asJsonArray, listType);

		return retInvoicesAtGstn.findInvFromGstn(dtos.get(0), GROUP_CODE);

	}

}
