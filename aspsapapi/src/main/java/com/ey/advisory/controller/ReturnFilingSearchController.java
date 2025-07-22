package com.ey.advisory.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.GstnReturnFilingStatus;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.ReturnFilingGstnResponseDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */
@RestController
@Slf4j
public class ReturnFilingSearchController {

	@Autowired
	GstnReturnFilingStatus gstnReturnFiling;

	@PostMapping(value = "/ui/getReturnFilingSearchResults", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getReturnFilingSearchResults(
			@RequestBody String jsonReq) {

		JsonObject requestObj = (new JsonParser()).parse(jsonReq)
				.getAsJsonObject();

		String gstins = requestObj.has("gstins")
				? requestObj.get("gstins").getAsJsonArray().toString() : "[]";
		Gson converter = new Gson();
		Type type = new TypeToken<List<String>>() {
		}.getType();
		List<String> gstinList = converter.fromJson(gstins, type);

		Gson gson = GsonUtil.newSAPGsonInstance();
		String returnType = requestObj.has("returnType")
				? requestObj.get("returnType").getAsJsonArray().toString()
				: "[]";
		ArrayList<String> returnTypes = converter.fromJson(returnType, type);

		String returnPeriod = requestObj.has("returnPeriod")
				? requestObj.get("returnPeriod").getAsString() : "";
		String finYear = requestObj.has("returnPeriod")
				? requestObj.get("returnPeriod").getAsString() : "";

		List<ReturnFilingGstnResponseDto> filingList = gstnReturnFiling
				.callGstnApi(gstinList, finYear, false);
		JsonObject gstinResp = new JsonObject();
		JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(filingList);
		gstinResp.add("filingStatus", respBody);
		resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp.add("resp", gstinResp);

		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

}
