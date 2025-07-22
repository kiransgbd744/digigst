package com.ey.advisory.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.KeyValuePair;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.ewb.app.api.APIError;
import com.ey.advisory.ewb.app.api.APIExecutor;
import com.ey.advisory.ewb.app.api.APIIdentifiers;
import com.ey.advisory.ewb.app.api.APIParams;
import com.ey.advisory.ewb.app.api.APIReqParamConstants;
import com.ey.advisory.ewb.app.api.APIResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@RestController
@Component
public class GenerateEwayBillController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GenerateEwayBillController.class);

	@Autowired
	@Qualifier("DefaultEWBAPIExecutor")
	private APIExecutor ewbApiExecutor;

	@PostMapping(value = "/api/generateEwayBill1", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> generateEwayBill(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject jsonObj = new JsonObject();
		try {
			KeyValuePair<String, String> apiParam = new KeyValuePair<>(
					APIReqParamConstants.GSTIN, "29ABYPR4788F1ZJ");
			APIParams apiParams = new APIParams(APIProviderEnum.EWB,
					APIIdentifiers.GENERATE_EWB, apiParam);
			APIResponse response = ewbApiExecutor.execute(apiParams,
					jsonString);
			if (response.isSuccess()) {
				String jsonResp = response.getResponse();
				jsonObj.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				jsonObj.add("resp", gson.toJsonTree(jsonResp));
				if (LOGGER.isDebugEnabled()) {
					String msg = "EwayBill Generated Successfully."
							+ " Here the Details {}";
					LOGGER.debug(msg, jsonResp);
				}
			} else {
				List<APIError> errList = response.getErrors();
				jsonObj.add("hdr",
						gson.toJsonTree(APIRespDto.creatErrorResp()));
				jsonObj.add("resp", gson.toJsonTree(errList));
				if (LOGGER.isDebugEnabled()) {
					String msg = "EwayBill Generated Failed. Here the Errors {}";
					LOGGER.debug(msg, errList);
				}
			}

			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception while Generating EwayBill ", ex);
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			jsonObj.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		} finally {
			TenantContext.clearTenant();
		}
	}

}
