package com.ey.advisory.controller;

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

import com.ey.advisory.app.docs.dto.simplified.Ret1SummaryReqDto;
import com.ey.advisory.app.services.search.simplified.docsummary.Ret1VerticalReqResponse;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * 
 * @author Balakrishna.S
 *
 */
@RestController
public class Ret1AspVerticalController {

	@Autowired
	@Qualifier("Ret1VerticalReqResponse")
	Ret1VerticalReqResponse reqResp;
	// ASP Vertical Sections 3C3/3C4/3D1/3D2/3D3/3D4/4A10/4A11/4B2/4B3/4E1/4E2

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Ret1IntLateFeeDetailController.class);

	@PostMapping(value = "/ui/ret1AspVertical", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> ey1AspVerticalSummary(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			Ret1SummaryReqDto ret1SummaryRequest = gson
					.fromJson(reqJson.toString(), Ret1SummaryReqDto.class);

			JsonElement retReqRespDetails = reqResp
					.retReqRespDetails(ret1SummaryRequest);
			JsonElement respBody = gson.toJsonTree(retReqRespDetails);
			JsonObject resp = new JsonObject();
			resp.add("resp", respBody);

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fecthing Annexure1 "
					+ "Summary Documents " + ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//  RET1A ASP Vertical Sections 3D1/3D2/3D3/3D4/
	@PostMapping(value = "/ui/ret1aAspVertical", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> ey1aAspVerticalSummary(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			Ret1SummaryReqDto ret1SummaryRequest = gson
					.fromJson(reqJson.toString(), Ret1SummaryReqDto.class);

			JsonElement retReqRespDetails = reqResp
					.ret1AReqRespDetails(ret1SummaryRequest);
			JsonElement respBody = gson.toJsonTree(retReqRespDetails);
			JsonObject resp = new JsonObject();
			resp.add("resp", respBody);

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fecthing Annexure1 "
					+ "Summary Documents " + ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
