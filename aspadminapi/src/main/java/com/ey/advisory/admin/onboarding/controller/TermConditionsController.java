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

import com.ey.advisory.admin.services.onboarding.TermCondtionsServiceImpl;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.TermCondRespDto;
import com.ey.advisory.core.dto.TermConditionsReqDto;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
public class TermConditionsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TermConditionsController.class);
	@Autowired
	@Qualifier("TermCondtionsServiceImpl")
	private TermCondtionsServiceImpl termCondtServImpl;

	@PostMapping(value = "/getTermAndCond.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getTermAndCond(@RequestBody String reqJson) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {
			JsonObject reqObj = (new JsonParser()).parse(reqJson)
					.getAsJsonObject().get("req").getAsJsonObject();
			TermConditionsReqDto reqDto = gson.fromJson(reqObj,
					TermConditionsReqDto.class);
			List<TermCondRespDto> termCondRespDtos = termCondtServImpl
					.getTermCondi(reqDto);
			JsonElement body = gson.toJsonTree(termCondRespDtos);
			resp.add("resp", body);
			resp.add("hdr", gson.toJsonTree(APIRespDto.SUCCESS_STATUS));
		} catch (Exception e) {
			LOGGER.error("Exception Occured ", e);
			resp.add("hdr", gson.toJsonTree(APIRespDto.ERROR_STATUS));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/saveTermAndCond.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> saveTermAndCond(@RequestBody String reqJson) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {
			JsonArray jsonArray = (new JsonParser()).parse(reqJson)
					.getAsJsonObject().get("req").getAsJsonArray();
			Type listType = new TypeToken<List<TermConditionsReqDto>>() {
			}.getType();
			List<TermConditionsReqDto> reqDtos = gson.fromJson(jsonArray,
					listType);
			termCondtServImpl.saveTermCondi(reqDtos);
			resp.add("resp", gson.toJsonTree(new APIRespDto(
					APIRespDto.getSuccessStatus(), "Updated Successfully")));
			resp.add("hdr", gson.toJsonTree(APIRespDto.SUCCESS_STATUS));
		} catch (Exception e) {
			LOGGER.error("Exception Occured: ", e);
			resp.add("resp", gson.toJsonTree(new APIRespDto(
					APIRespDto.getSuccessStatus(), e.getMessage())));
			resp.add("hdr", gson.toJsonTree(APIRespDto.ERROR_STATUS));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/deleteTermAndCond.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> deleteTermAndCond(
			@RequestBody String jsonReq) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {
			JsonObject reqObj = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject().get("req").getAsJsonObject();
			TermConditionsReqDto reqDto = gson.fromJson(reqObj,
					TermConditionsReqDto.class);
			termCondtServImpl.deleteTermCondtions(reqDto);

			resp.add("resp", gson.toJsonTree(new APIRespDto(
					APIRespDto.getSuccessStatus(), "Deleted Successfully")));
			resp.add("hdr", gson.toJsonTree(APIRespDto.SUCCESS_STATUS));
		} catch (Exception e) {
			LOGGER.error("Exception Occured: ", e);
			resp.add("resp",
					gson.toJsonTree(new APIRespDto(APIRespDto.getErrorStatus(),
							"Failed to Delete the Value")));
			resp.add("hdr", gson.toJsonTree(APIRespDto.ERROR_STATUS));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}
}
