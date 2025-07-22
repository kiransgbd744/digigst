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

import com.ey.advisory.admin.services.onboarding.TemplateSelectionServiceImpl;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.BankDetailsReqDto;
import com.ey.advisory.core.dto.BankDetailsRespDto;
import com.ey.advisory.core.dto.TemplateSelectionReqDto;
import com.ey.advisory.core.dto.TemplateSelectionRespDto;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
public class TemplateSelectionController {

	@Autowired
	@Qualifier("TemplateSelectionServiceImpl")
	private TemplateSelectionServiceImpl tempSelServImpl;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TemplateSelectionController.class);

	@PostMapping(value = "/getTemplateSelection.do", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getTemplateSelection(
			@RequestBody String reqJson) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {
			JsonObject reqObj = (new JsonParser()).parse(reqJson)
					.getAsJsonObject().get("req").getAsJsonObject();
			TemplateSelectionReqDto tempSelcReqDto = gson.fromJson(reqObj,
					TemplateSelectionReqDto.class);
			List<TemplateSelectionRespDto> tempSeleRespDtos = tempSelServImpl
					.getTemplateSelection(tempSelcReqDto);
			JsonElement jsonBody = gson.toJsonTree(tempSeleRespDtos);
			resp.add("resp", jsonBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		} catch (Exception e) {
			LOGGER.error("Exception Occured: ", e);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/saveTemplateSelection.do", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveTemplateSelection(
			@RequestBody String jsonReq) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {

			JsonObject reqObj = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject().get("req").getAsJsonObject();
			TemplateSelectionReqDto reqDto = gson.fromJson(reqObj,
					TemplateSelectionReqDto.class);
			tempSelServImpl.saveTemplateSelection(reqDto);
			resp.add("resp", gson.toJsonTree(new APIRespDto(
					APIRespDto.getSuccessStatus(), "Updated Successfully")));
		} catch (Exception e) {
			LOGGER.error("Exception Occured ", e);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/getbankDetails.do", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getBanckDetails(@RequestBody String jsonReq) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {
			JsonObject reqObj = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject().get("req").getAsJsonObject();
			BankDetailsReqDto reqDto = gson.fromJson(reqObj,
					BankDetailsReqDto.class);
			List<BankDetailsRespDto> bankDetRespDtos = tempSelServImpl
					.getBankDetails(reqDto);
			JsonElement jsonBody = gson.toJsonTree(bankDetRespDtos);
			resp.add("resp", jsonBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		} catch (Exception e) {
			LOGGER.error("Exception Occured ", e);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/saveBankDetails.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> saveBankDetails(@RequestBody String jsonReq) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {
			JsonArray reqObj = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject().get("req").getAsJsonArray();
			Type listType = new TypeToken<List<BankDetailsReqDto>>() {
			}.getType();
			List<BankDetailsReqDto> reqDtos = gson.fromJson(reqObj, listType);
			tempSelServImpl.saveBankDetails(reqDtos);
			resp.add("resp", gson.toJsonTree(new APIRespDto(
					APIRespDto.getSuccessStatus(), "Updated Successfully")));
			resp.add("hdr", gson.toJsonTree(APIRespDto.getSuccessStatus()));
		} catch (Exception e) {
			LOGGER.error("Exception Occured: ", e);
			resp.add("resp",
					gson.toJsonTree(new APIRespDto(APIRespDto.getErrorStatus(),
							e.getMessage())));
			resp.add("hdr", gson.toJsonTree(APIRespDto.getErrorStatus()));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}
}
