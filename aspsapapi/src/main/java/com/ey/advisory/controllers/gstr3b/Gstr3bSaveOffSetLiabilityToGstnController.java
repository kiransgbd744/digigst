package com.ey.advisory.controllers.gstr3b;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.repositories.client.Gstr3BSaveLiabilitySetOffStatusRepository;
import com.ey.advisory.app.gstr3b.Gstr3BSaveLiabilitySetOffServiceImpl;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@RestController
public class Gstr3bSaveOffSetLiabilityToGstnController {

	@Autowired
	@Qualifier("Gstr3BSaveLiabilitySetOffStatusRepository")
	private Gstr3BSaveLiabilitySetOffStatusRepository satusRepository;
	
	@Autowired
	@Qualifier("Gstr3BSaveLiabilitySetOffServiceImpl")
	private Gstr3BSaveLiabilitySetOffServiceImpl service;


	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@PostMapping(value = "/ui/saveOffSetLiabilityToGstin", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveOffSetLiabilityToGstin(
			@RequestBody String jsonString) {
		LOGGER.debug("Inside Gstr3bSaveOffSetLiabilityToGstnController"
				+ ".saveOffSetLiabilityToGstin() method");
		String groupCode = TenantContext.getTenantId();
		LOGGER.info("groupCode {} is set", groupCode);
		try {
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			String taxPeriod = requestObject.get("taxPeriod").getAsString();
			String gstin = requestObject.get("gstin").getAsString();
			String respMsg = saveToGstn(gstin, taxPeriod);

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(respMsg));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Unexpected error while Fetching  Latest status";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String saveToGstn(String gstin, String retPeriod) {

			String respBody = null;
			String groupCode = TenantContext.getTenantId();
			String authStatus = authTokenService.getAuthTokenStatusForGstin(gstin);
			if (authStatus.equals("A")) {
				Long saveInprogressCnt = satusRepository
						.findByGstinAndTaxPeriodAndStatus(gstin, retPeriod,
								APIConstants.SAVE_INITIATED);
				if (saveInprogressCnt > 0) {
					respBody = "Save is already Inprogress";
				} 
					 else {
						 respBody = service.saveLiablityToGstn(gstin, retPeriod,
								 groupCode);
					}
				}
					else {
				respBody = "Auth Token is Inactive, Please Activate";
				} return respBody;

}}