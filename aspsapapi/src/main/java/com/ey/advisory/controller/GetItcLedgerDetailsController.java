package com.ey.advisory.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.services.ledger.GetITCLedgerDetails;
import com.ey.advisory.app.services.ledger.ItcDetailsRespDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class GetItcLedgerDetailsController {
	
	@Autowired
	@Qualifier("getITCLedgerDetailsImpl")
	private GetITCLedgerDetails getItc;
	
	private static final String GROUP_CODE = GetCashLedgerDetailsController
			.staticTenantId();
	
	@PostMapping(value = "/ui/getITCLedgerDetails", produces = 
			MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getITCLedgerDetails(@RequestBody 
			String jsonReq, HttpServletRequest request) {
		JsonObject resp1 = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		TenantContext.setTenantId(GROUP_CODE);
		LOGGER.debug("getITCLedgerDetails method called with arg {}", jsonReq);
		List<ItcDetailsRespDto> apiResp = getItc.findITC(jsonReq, GROUP_CODE);
		JsonElement respBody = gson.toJsonTree(apiResp);
		resp1.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp1.add("resp", respBody);
		return new ResponseEntity<>(resp1.toString(), HttpStatus.OK);
	}
	
	
	public static String staticTenantId() {
		// return "ern00002";
		// return "shi00005";
		return "y8nvcqp4f9";
	}
}
