/**
 * 
 */
package com.ey.advisory.sap.controller;

import java.lang.reflect.Type;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.services.jobs.anx1.Anx1InvoicesAtGstn;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Anx1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import jakarta.servlet.http.HttpServletRequest;

/**
 * This is Dummy class to test the async Job Service(Anx1 GET) 
 * service Through API.
 * 
 * @author Hemasundar.J
 *
 */
@RestController
public class Anx1GetGstnInvoicesTestController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1GetGstnInvoicesTestController.class);

	private static final String GROUP_CODE = TestController.staticTenantId();
	private static final String LOG1 = "{} method called with arg {}";

	@Autowired
	@Qualifier("anx1B2bInvoicesAtGstnImpl")
	private Anx1InvoicesAtGstn getB2bService;
	
	@Autowired
	@Qualifier("anx1B2baInvoicesAtGstnImpl")
	private Anx1InvoicesAtGstn getB2baService;

	@Autowired
	@Qualifier("anx1B2cInvoicesAtGstnImpl")
	private Anx1InvoicesAtGstn getB2cService;

	@Autowired
	@Qualifier("anx1RevInvoicesAtGstnImpl")
	private Anx1InvoicesAtGstn getRevService;

	@Autowired
	@Qualifier("anx1EcomInvoicesAtGstnImpl")
	private Anx1InvoicesAtGstn getEcomservice;

	@Autowired
	@Qualifier("anx1ExpwopInvoicesAtGstnImpl")
	private Anx1InvoicesAtGstn getExpwopService;

	@Autowired
	@Qualifier("Anx1ExpwpInvoicesAtGstnImpl")
	private Anx1InvoicesAtGstn getExpwpService;
	
	@Autowired
	@Qualifier("Anx1MisInvoicesAtGstnImpl")
	private Anx1InvoicesAtGstn getMisService;
	
	@Autowired
	@Qualifier("Anx1ImpgSezInvoicesAtGstnImpl")
	private Anx1InvoicesAtGstn getImpgSezService;
	
	@Autowired
	@Qualifier("Anx1ImpgInvoicesAtGstnImpl")
	private Anx1InvoicesAtGstn getImpgService;
	
	@Autowired
	@Qualifier("Anx1ImpsInvoicesAtGstnImpl")
	private Anx1InvoicesAtGstn getImpsService;
	
	@Autowired
	@Qualifier("Anx1SezwopInvoicesAtGstnImpl")
	private Anx1InvoicesAtGstn getSezwopService;
	
	@Autowired
	@Qualifier("Anx1SezwopaInvoicesAtGstnImpl")
	private Anx1InvoicesAtGstn getSezwopaService;
	
	@Autowired
	@Qualifier("Anx1SezwpInvoicesAtGstnImpl")
	private Anx1InvoicesAtGstn getSezwpService;
	
	@Autowired
	@Qualifier("Anx1SezwpaInvoicesAtGstnImpl")
	private Anx1InvoicesAtGstn getSezwpaService;
	
	@Autowired
	@Qualifier("Anx1DeInvoicesAtGstnImpl")
	private Anx1InvoicesAtGstn getDeService;
	
	@Autowired
	@Qualifier("Anx1DeaInvoicesAtGstnImpl")
	private Anx1InvoicesAtGstn getDeaService;

	@PostMapping(value = { "/Anx1GetGstnB2c" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> b2cDataFromGstn(@RequestBody String jsonReq,
			HttpServletRequest request) {
		Anx1GetInvoicesReqDto reuestDto = getReuestDtos(jsonReq);
		TenantContext.setTenantId(GROUP_CODE);
		LOGGER.debug(LOG1, "b2cDataFromGstn", jsonReq);
		return getB2cService.findInvFromGstn(reuestDto, GROUP_CODE, APIConstants.B2C);
	}
	
	@PostMapping(value = { "/Anx1GetGstnB2b" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> b2bDataFromGstn(@RequestBody String jsonReq,
			HttpServletRequest request) {
		Anx1GetInvoicesReqDto reuestDto = getReuestDtos(jsonReq);
		TenantContext.setTenantId(GROUP_CODE);
		LOGGER.debug(LOG1, "b2bDataFromGstn", jsonReq);
		return getB2bService.findInvFromGstn(reuestDto, GROUP_CODE, APIConstants.B2B);
	}
	
	
	@PostMapping(value = { "/Anx1GetGstnB2ba" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> b2baDataFromGstn(@RequestBody String jsonReq,
			HttpServletRequest request) {
		Anx1GetInvoicesReqDto reuestDto = getReuestDtos(jsonReq);
		TenantContext.setTenantId(GROUP_CODE);
		LOGGER.debug(LOG1, "b2baDataFromGstn", jsonReq);
		return getB2baService.findInvFromGstn(reuestDto, GROUP_CODE, APIConstants.B2BA);
	}

	@PostMapping(value = { "/Anx1GetGstnExpwp" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> expwpDataFromGstn(
			@RequestBody String jsonReq, HttpServletRequest request) {
		Anx1GetInvoicesReqDto reuestDto = getReuestDtos(jsonReq);
		TenantContext.setTenantId(GROUP_CODE);
		LOGGER.debug(LOG1, "expwpDataFromGstn", jsonReq);
		return getExpwpService.findInvFromGstn(reuestDto, GROUP_CODE, APIConstants.EXPWP);
	}
	
	@PostMapping(value = { "/Anx1GetGstnExpwop" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> expwopDataFromGstn(
			@RequestBody String jsonReq, HttpServletRequest request) {
		Anx1GetInvoicesReqDto reuestDto = getReuestDtos(jsonReq);
		TenantContext.setTenantId(GROUP_CODE);
		LOGGER.debug(LOG1, "expwopDataFromGstn", jsonReq);
		return getExpwopService.findInvFromGstn(reuestDto, GROUP_CODE, APIConstants.EXPWOP);
	}

	@PostMapping(value = { "/Anx1GetGstnSezwp" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> sezwpDataFromGstn(
			@RequestBody String jsonReq, HttpServletRequest request) {
		Anx1GetInvoicesReqDto reuestDto = getReuestDtos(jsonReq);
		TenantContext.setTenantId(GROUP_CODE);
		LOGGER.debug(LOG1, "sezwpDataFromGstn", jsonReq);
		return getSezwpService.findInvFromGstn(reuestDto, GROUP_CODE, APIConstants.SEZWP);
	}
	
	@PostMapping(value = { "/Anx1GetGstnSezwpa" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> sezwpaDataFromGstn(
			@RequestBody String jsonReq, HttpServletRequest request) {
		Anx1GetInvoicesReqDto reuestDto = getReuestDtos(jsonReq);
		TenantContext.setTenantId(GROUP_CODE);
		LOGGER.debug(LOG1, "sezwpaDataFromGstn", jsonReq);
		return getSezwpaService.findInvFromGstn(reuestDto, GROUP_CODE, APIConstants.SEZWPA);
	}
	
	@PostMapping(value = { "/Anx1GetGstnSezwop" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> sezwopDataFromGstn(
			@RequestBody String jsonReq, HttpServletRequest request) {
		Anx1GetInvoicesReqDto reuestDto = getReuestDtos(jsonReq);
		TenantContext.setTenantId(GROUP_CODE);
		LOGGER.debug(LOG1, "sezwopDataFromGstn", jsonReq);
		return getSezwopService.findInvFromGstn(reuestDto, GROUP_CODE, APIConstants.SEZWOP);
	}
	
	@PostMapping(value = { "/Anx1GetGstnSezwopa" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> sezwopaDataFromGstn(
			@RequestBody String jsonReq, HttpServletRequest request) {
		Anx1GetInvoicesReqDto reuestDto = getReuestDtos(jsonReq);
		TenantContext.setTenantId(GROUP_CODE);
		LOGGER.debug(LOG1, "sezwopaDataFromGstn", jsonReq);
		return getSezwopaService.findInvFromGstn(reuestDto, GROUP_CODE, APIConstants.SEZWOPA);
	}

	@PostMapping(value = { "/Anx1GetGstnDe" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> deDataFromGstn(
			@RequestBody String jsonReq, HttpServletRequest request) {
		Anx1GetInvoicesReqDto reuestDto = getReuestDtos(jsonReq);
		TenantContext.setTenantId(GROUP_CODE);
		LOGGER.debug(LOG1, "deDataFromGstn", jsonReq);
		return getDeService.findInvFromGstn(reuestDto, GROUP_CODE, APIConstants.DE);
	}
	
	@PostMapping(value = { "/Anx1GetGstnDea" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> deaDataFromGstn(
			@RequestBody String jsonReq, HttpServletRequest request) {
		Anx1GetInvoicesReqDto reuestDto = getReuestDtos(jsonReq);
		TenantContext.setTenantId(GROUP_CODE);
		LOGGER.debug(LOG1, "deaDataFromGstn", jsonReq);
		return getDeaService.findInvFromGstn(reuestDto, GROUP_CODE, APIConstants.DEA);
	}
	
	@PostMapping(value = { "/Anx1GetGstnRev" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> revDataFromGstn(@RequestBody String jsonReq,
			HttpServletRequest request) {
		Anx1GetInvoicesReqDto reuestDto = getReuestDtos(jsonReq);
		TenantContext.setTenantId(GROUP_CODE);
		LOGGER.debug(LOG1, "revDataFromGstn", jsonReq);
		return getRevService.findInvFromGstn(reuestDto, GROUP_CODE, APIConstants.REV);
	}
	
	@PostMapping(value = { "/Anx1GetGstnImps" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> impsDataFromGstn(
			@RequestBody String jsonReq, HttpServletRequest request) {
		Anx1GetInvoicesReqDto reuestDto = getReuestDtos(jsonReq);
		TenantContext.setTenantId(GROUP_CODE);
		LOGGER.debug(LOG1, "impsDataFromGstn", jsonReq);
		return getImpsService.findInvFromGstn(reuestDto, GROUP_CODE, APIConstants.IMPS);
	}

	@PostMapping(value = { "/Anx1GetGstnImpg" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> impgDataFromGstn(
			@RequestBody String jsonReq, HttpServletRequest request) {
		Anx1GetInvoicesReqDto reuestDto = getReuestDtos(jsonReq);
		TenantContext.setTenantId(GROUP_CODE);
		LOGGER.debug(LOG1, "impgDataFromGstn", jsonReq);
		return getImpgService.findInvFromGstn(reuestDto, GROUP_CODE, APIConstants.IMPG);
	}
	
	@PostMapping(value = { "/Anx1GetGstnImpgSez" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> impgSezDataFromGstn
	                 (@RequestBody String jsonReq,HttpServletRequest request) {
		Anx1GetInvoicesReqDto reuestDto = getReuestDtos(jsonReq);
		TenantContext.setTenantId(GROUP_CODE);
		LOGGER.debug(LOG1, "impgSezDataFromGstn", jsonReq);
		return getImpgSezService.findInvFromGstn(reuestDto, GROUP_CODE,
				                                 APIConstants.IMPGSEZ);
	}
	
	@PostMapping(value = { "/Anx1GetGstnMis" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> misDataFromGstn(@RequestBody String jsonReq,
			HttpServletRequest request) {
		
		Anx1GetInvoicesReqDto reuestDto = getReuestDtos(jsonReq);
		TenantContext.setTenantId(GROUP_CODE);
		LOGGER.debug(LOG1, "misDataFromGstn", jsonReq);
		return getMisService.findInvFromGstn(reuestDto, GROUP_CODE, APIConstants.MIS);
	}
	
	@PostMapping(value = { "/Anx1GetGstnEcom" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> ecomDataFromGstn(@RequestBody String jsonReq,
			HttpServletRequest request) {
		
		Anx1GetInvoicesReqDto reuestDto = getReuestDtos(jsonReq);
		TenantContext.setTenantId(GROUP_CODE);
		LOGGER.debug(LOG1, "ecomDataFromGstn", jsonReq);
		return getEcomservice.findInvFromGstn(reuestDto, GROUP_CODE, APIConstants.ECOM);
	}	
	
	public Anx1GetInvoicesReqDto getReuestDtos(String jsonReq) {
		Gson gson = GsonUtil.newSAPGsonInstance();

		JsonObject requestObject = (new JsonParser()).parse(jsonReq)
				.getAsJsonObject();
		JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();
		Type listType = new TypeToken<List<Anx1GetInvoicesReqDto>>() {
		}.getType();
		List<Anx1GetInvoicesReqDto> dtos = gson.fromJson(asJsonArray, listType);
		// Just returning one object for testing
		return dtos.get(0);
	}
	
}
