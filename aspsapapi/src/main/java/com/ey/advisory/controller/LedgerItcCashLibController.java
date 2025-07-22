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

import com.ey.advisory.app.docs.dto.ledger.LedgerSummarizeBalanceDto;
import com.ey.advisory.app.services.ledger.GetLedgerBalance;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class LedgerItcCashLibController {
	
	@Autowired
	@Qualifier("getLedgerBalances")
	private GetLedgerBalance getledgerBalance;
	
	@PostMapping(value = "/ui/getCashITCLibLegderBalance", produces =
			MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getCashItcLibBalance(@RequestBody 
			String jsonReq, HttpServletRequest request) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug("getCashITCBalance method called with arg {}", jsonReq);
		List<LedgerSummarizeBalanceDto> apiResp = 
				getledgerBalance.getLedgerBalances(jsonReq, groupCode);
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(apiResp);
		resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp.add("response", respBody);
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		
	}

}
