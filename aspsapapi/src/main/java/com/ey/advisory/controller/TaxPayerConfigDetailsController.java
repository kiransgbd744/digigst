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

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.common.TaxPayerConfigDetailsService;
import com.ey.advisory.app.docs.dto.TaxPayerConfigDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.controllers.anexure1.InputValidationUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/*
 * This Clas is for Getting all Configuration Details that are stored
 * in gstin config table that provide details on same 
 * 
 */

@Slf4j
@RestController
public class TaxPayerConfigDetailsController {

	@Autowired
	@Qualifier("TaxPayerConfigDetailsServiceImpl")
	TaxPayerConfigDetailsService taxPayerConfigService;

	@PostMapping(value = "/ui/getTaxPayerConfigDetail", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getTaxPayerConfigDetails(
			@RequestBody String reqJson) {
		JsonObject requestObject = (new JsonParser()).parse(reqJson)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			//String userName = json.get("user").getAsString();
			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();
			boolean einvApplicable = false;
			if (json.has("einvApplicability")) {
				einvApplicable = json.get("einvApplicability").getAsBoolean();
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("getting TaxPayer Config Details :");
			}

			/*
			 * Calling service for getting all Config Info and Forming Json
			 * response for UI
			 */
			List<TaxPayerConfigDto> taxPayerConfig = taxPayerConfigService
					.getTaxPayerConfigDetails(einvApplicable);
			JsonObject resp = new JsonObject();
			JsonObject detResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(taxPayerConfig);
			detResp.add("det", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", detResp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			return InputValidationUtil.createJsonErrResponse(ex);
		}

	}

}
