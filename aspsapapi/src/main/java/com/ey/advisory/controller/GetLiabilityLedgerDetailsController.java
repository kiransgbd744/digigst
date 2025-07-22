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

import com.ey.advisory.app.docs.dto.ledger.LiabDetailsRespDto;
import com.ey.advisory.app.services.ledger
		.GetLiabilityLedgerDetailsForReturnLiability;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class GetLiabilityLedgerDetailsController {
	
	private static final String GROUP_CODE = GetCashLedgerDetailsController
			.staticTenantId();
	
	@Autowired
	@Qualifier("getLiabilityLedgerDetailsForReturnLiabilityImpl")
	private GetLiabilityLedgerDetailsForReturnLiability getTax;
	
	@PostMapping(value = "/ui/getLiabilityLedgerDetails", produces = 
			MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getLiabilityLedgerDetails(@RequestBody 
			String jsonReq, HttpServletRequest request) {
		LOGGER.debug("getLiabilityLedgerDetails method called with arg {}",
				jsonReq);
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp1 = new JsonObject();
		try {
			List<LiabDetailsRespDto> allDetailsDto = getTax.findTax(jsonReq);
			JsonElement respBody = gson.toJsonTree(allDetailsDto);
			resp1.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp1.add("resp", respBody);
			return new ResponseEntity<>(resp1.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Exception while Fetching Liability Ledger Details";
			LOGGER.error(msg, ex);
			resp1.add("hdr", new Gson()
					.toJsonTree(new APIRespDto("E", ex.getMessage())));
			return new ResponseEntity<>(resp1.toString(), HttpStatus.OK);
		}
	}

	public static String staticTenantId() {
		// return "ern00002";
		// return "shi00005";
		return "y8nvcqp4f9";
	}
}
