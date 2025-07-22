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

import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.company.code.map.CompanyCodeMappingReqDto;
import com.ey.advisory.company.code.map.CompanyCodeMappingRespDto;
import com.ey.advisory.company.code.map.CompanyCodeMappingServiceImpl;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
public class CompanyCodeMappingController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CompanyCodeMappingController.class);

	@Autowired
	@Qualifier("CompanyCodeMappingServiceImpl")
	private CompanyCodeMappingServiceImpl companyCodeMappingService;

	@PostMapping(value = "/getCompanyCodeMapping.do", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getCompanyCodeMapping(
			@RequestBody String jsonReq) {

		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {
			JsonObject reqObj = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject().get("req").getAsJsonObject();
			CompanyCodeMappingReqDto reqDto = gson.fromJson(reqObj,
					CompanyCodeMappingReqDto.class);
			List<CompanyCodeMappingRespDto> respDtos = companyCodeMappingService
					.getCompanyCodeMapping(reqDto);
			JsonElement bodyResp = gson.toJsonTree(respDtos);
			resp.add("resp", bodyResp);
			resp.add("hdr", gson.toJsonTree(APIRespDto.SUCCESS_STATUS));

		} catch (Exception e) {
			LOGGER.error("Exception Occured:", e);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/saveCompanyCodeMapp.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> saveCompanyCodeMapp(
			@RequestBody String reqObj) {
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		JsonObject resp = new JsonObject();
		try {
			JsonArray reqArray = (new JsonParser()).parse(reqObj)
					.getAsJsonObject().get("req").getAsJsonArray();
			Type list = new TypeToken<List<CompanyCodeMappingReqDto>>() {
			}.getType();
			List<CompanyCodeMappingReqDto> reqDtos = gson.fromJson(reqArray,
					list);
			companyCodeMappingService.saveCompanyCodeMapping(reqDtos);
			resp.add("resp", gson.toJsonTree(new APIRespDto(
					APIRespDto.getSuccessStatus(), "Updated Successfully")));
			resp.add("hrd", gson.toJsonTree(APIRespDto.SUCCESS_STATUS));
		} catch (Exception e) {
			LOGGER.error("Exception Occured: ", e);
			resp.add("resp", gson.toJsonTree(new APIRespDto(
					APIRespDto.getSuccessStatus(), e.getMessage())));
			resp.add("hrd", gson.toJsonTree(APIRespDto.ERROR_STATUS));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}
}
