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

import com.ey.advisory.app.services.ledger.GetCashITCBalance;
import com.ey.advisory.app.services.ledger.GetCashLedgerDetails;
import com.ey.advisory.app.services.ledger.GetITCLedgerDetails;
import com.ey.advisory.app.services.ledger.GetLiabilityLedgerDetailsForReturnLiability;
import com.ey.advisory.app.services.ledger.GetOtherthanReturnLedger;
import com.ey.advisory.app.services.ledger.GetRetLiabBalance;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 
 * This is Dummy class to test the async Job Service(Ledger) 
 * service Through API.
 * 
 * @author Hemasundar.J
 *
 */
@RestController
public class LedgerTestController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(LedgerTestController.class);
	private static final String GROUP_CODE = TestController.staticTenantId();
	
	@Autowired
	@Qualifier("getCashITCBalanceImpl")
	private GetCashITCBalance getBalance;
	
	@Autowired
	@Qualifier("getCashLedgerDetailsImpl")
	private GetCashLedgerDetails getCash;
	
	@Autowired
	@Qualifier("getITCLedgerDetailsImpl")
	private GetITCLedgerDetails getItc;
	
	@Autowired
	@Qualifier("getLiabilityLedgerDetailsForReturnLiabilityImpl")
	private GetLiabilityLedgerDetailsForReturnLiability getTax;
	
	@Autowired
	@Qualifier("getOtherthanReturnLedgerImpl")
	private GetOtherthanReturnLedger getNrtn;
	
	@Autowired
	@Qualifier("getRetLiabBalanceImpl")
	private GetRetLiabBalance getTaxPayable;

	@PostMapping(value = "/getCashITCBalance", produces = 
			MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getCashITCBalance(@RequestBody String jsonReq,
			HttpServletRequest request) {
		TenantContext.setTenantId(GROUP_CODE);
		LOGGER.debug("getCashITCBalance method called with arg {}", jsonReq);
		String apiResp = getBalance.findBalance(jsonReq, GROUP_CODE);
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp1 = new JsonObject();
		JsonElement reponseBody = gson.toJsonTree(apiResp);
		resp1.add("resp", reponseBody);
		return new ResponseEntity<>(resp1.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/getOtherthanReturnLedger", produces = 
			MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getOtherthanReturnLedger(@RequestBody String jsonReq,
			HttpServletRequest request) {
		TenantContext.setTenantId(GROUP_CODE);
		LOGGER.debug("getOtherthanReturnLedger method called with arg {}", jsonReq);
		String apiResp = getNrtn.findNrtn(jsonReq, GROUP_CODE);
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp1 = new JsonObject();
		JsonElement reponseBody = gson.toJsonTree(apiResp);
		resp1.add("resp", reponseBody);
		return new ResponseEntity<>(resp1.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/getRetLiabBalance", produces = 
			MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getRetLiabBalance(@RequestBody String jsonReq,
			HttpServletRequest request) {
		TenantContext.setTenantId(GROUP_CODE);
		LOGGER.debug("getOtherthanReturnLedger method called with arg {}", jsonReq);
		String apiResp = getTaxPayable.findTaxPayable(jsonReq, GROUP_CODE);
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp1 = new JsonObject();
		JsonElement reponseBody = gson.toJsonTree(apiResp);
		resp1.add("resp", reponseBody);
		return new ResponseEntity<>(resp1.toString(), HttpStatus.OK);
	}

}
