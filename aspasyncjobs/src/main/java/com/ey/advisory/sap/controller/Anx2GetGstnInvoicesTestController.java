package com.ey.advisory.sap.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.services.jobs.anx2.Anx2InvoicesAtGstn;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 
 * This is Dummy class to test the async Job Service(Anx2 GET) 
 * service Through API.
 * 
 * @author Hemasundar.J
 *
 */
@RestController
public class Anx2GetGstnInvoicesTestController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx2GetGstnInvoicesTestController.class);

	private static final String GROUP_CODE = TestController.staticTenantId();
	private static final String LOG1 = "{} method called with arg {}";

	@Autowired
	@Qualifier("anx2B2bInvoicesAtGstnImpl")
	private Anx2InvoicesAtGstn b2b;

	@Autowired
	@Qualifier("anx2SezwpInvoicesAtGstnImpl")
	private Anx2InvoicesAtGstn sezwp;

	@Autowired
	@Qualifier("anx2SezwopInvoicesAtGstnImpl")
	private Anx2InvoicesAtGstn sezwop;

	@Autowired
	@Qualifier("anx2DeInvoicesAtGstnImpl")
	private Anx2InvoicesAtGstn de;

	@Autowired
	@Qualifier("anx2IsdcInvoicesAtGstnImpl")
	private Anx2InvoicesAtGstn isdc;
	
	@Autowired
	@Qualifier("Anx2ItcSumryInvoicesAtGstnImpl")
	private Anx2InvoicesAtGstn itc;

	@PostMapping(value = { "/Anx2GetGstnB2b" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> b2bDataFromGstn(@RequestBody String jsonReq,
			HttpServletRequest request) {
		TenantContext.setTenantId(GROUP_CODE);
		LOGGER.debug(LOG1, "b2bDataFromGstn", jsonReq);
		return b2b.findInvFromGstn(jsonReq, GROUP_CODE, APIConstants.B2B);

	}

	@PostMapping(value = { "/Anx2GetGstnDe" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> deDataFromGstn(@RequestBody String jsonReq,
			HttpServletRequest request) {
		TenantContext.setTenantId(GROUP_CODE);
		LOGGER.debug(LOG1, "deDataFromGstn", jsonReq);
		return de.findInvFromGstn(jsonReq, GROUP_CODE, APIConstants.DE);

	}

	@PostMapping(value = { "/Anx2GetGstnSezwp" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> sezwpDataFromGstn(@RequestBody String jsonReq,
			HttpServletRequest request) {
		TenantContext.setTenantId(GROUP_CODE);
		LOGGER.debug(LOG1, "sezwpDataFromGstn", jsonReq);
		return sezwp.findInvFromGstn(jsonReq, GROUP_CODE, APIConstants.SEZWP);

	}

	@PostMapping(value = { "/Anx2GetGstnSezwop" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> sezwopDataFromGstn(
			@RequestBody String jsonReq, HttpServletRequest request) {
		TenantContext.setTenantId(GROUP_CODE);
		LOGGER.debug(LOG1, "sezwopDataFromGstn", jsonReq);
		return sezwop.findInvFromGstn(jsonReq, GROUP_CODE, APIConstants.SEZWOP);

	}

	@PostMapping(value = { "/Anx2GetGstnIsdc" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> isdcDataFromGstn(@RequestBody String jsonReq,
			HttpServletRequest request) {
		TenantContext.setTenantId(GROUP_CODE);
		LOGGER.debug(LOG1, "isdcDataFromGstn", jsonReq);
		return isdc.findInvFromGstn(jsonReq, GROUP_CODE, APIConstants.ISDC);

	}
	
	@PostMapping(value = { "/Anx2GetGstnItcSumry" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> itcSumryDataFromGstn(@RequestBody String jsonReq,
			HttpServletRequest request) {
		TenantContext.setTenantId(GROUP_CODE);
		LOGGER.debug(LOG1, "itcSumryDataFromGstn", jsonReq);
		return itc.findInvFromGstn(jsonReq, GROUP_CODE, APIConstants.ITCSUM);

	}

}
