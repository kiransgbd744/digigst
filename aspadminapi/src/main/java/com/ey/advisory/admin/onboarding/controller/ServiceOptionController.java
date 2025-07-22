package com.ey.advisory.admin.onboarding.controller;

import java.lang.reflect.Type;
import java.util.List;

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

import com.ey.advisory.app.serviceoption.ServiceOptionServiceImpl;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.ServiceOptionDto;
import com.ey.advisory.core.dto.ServiceOptionReqDto;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
public class ServiceOptionController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceOptionController.class);

	@Autowired
	@Qualifier("ServiceOptionServiceImpl")
	private ServiceOptionServiceImpl servOptionSImpl;

	private static final String RESP = "resp";
	private static final String HRD = "hrd";

	private static final String UPDATE_SUCCESSFULLY = "Updated Successfully";

	@PostMapping(value = "/getServiceOption", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getServiceOption(@RequestBody String jsonReq) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject reqObj = (new JsonParser()).parse(jsonReq).getAsJsonObject().get("req").getAsJsonObject();
			ServiceOptionReqDto servOptReqDto = gson.fromJson(reqObj, ServiceOptionReqDto.class);

			List<ServiceOptionDto> optDtos = servOptionSImpl.getServiceOption(servOptReqDto);
			JsonElement respBody = gson.toJsonTree(optDtos);
			resp.add(HRD, gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add(RESP, respBody);

		} catch (Exception e) {
			LOGGER.error("Exception Occured:{}", e);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/saveServiceOption", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveServiceOption(@RequestBody String jsonReq) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonArray reqObj = (new JsonParser()).parse(jsonReq).getAsJsonObject().get("req").getAsJsonArray();
			Type listType = new TypeToken<List<ServiceOptionReqDto>>() {
			}.getType();
			List<ServiceOptionReqDto> reqDtos = gson.fromJson(reqObj, listType);
			servOptionSImpl.saveServiceOption(reqDtos);
			resp.add(HRD, gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add(RESP, gson.toJsonTree(new APIRespDto(APIRespDto.getSuccessStatus(), UPDATE_SUCCESSFULLY)));
		} catch (Exception e) {
			LOGGER.error("Exception Occured:{}", e);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}
}
