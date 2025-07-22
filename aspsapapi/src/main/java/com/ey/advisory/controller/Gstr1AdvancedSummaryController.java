package com.ey.advisory.controller;

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

import com.ey.advisory.app.docs.dto.Gstr1VerticalAtRespDto;
import com.ey.advisory.app.services.gstr1fileupload.Gstr1AtTxpdUpdateService;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1AdvancedReqResponse;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

/**
 * 
 * @author SriBhavya
 *
 */
@RestController
public class Gstr1AdvancedSummaryController {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1AdvancedSummaryController.class);

	@Autowired
	@Qualifier("Gstr1AdvancedReqResponse")
	Gstr1AdvancedReqResponse reqResp;

	@Autowired
	@Qualifier("Gstr1AtTxpdUpdateService")
	private Gstr1AtTxpdUpdateService gstr1AtUpdateService;

	@PostMapping(value = "/ui/gstr1AdvancedData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> advancedSummary(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			Annexure1SummaryReqDto ret1SummaryRequest = gson
					.fromJson(reqJson.toString(), Annexure1SummaryReqDto.class);

			JsonElement retReqRespDetails = reqResp
					.gstr1ReqRespDetails(ret1SummaryRequest);
			JsonElement respBody = gson.toJsonTree(retReqRespDetails);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
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

	/**
	 * this method definds for Popup Screen SEction Advance Received and Advance
	 * adjustment Edit & Save Vertical Record
	 */
	@PostMapping(value = "/ui/gstr1AtTxpdVerticalUpdate", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> atTxpdVerticalUpdateSummary(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonArray jsonObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().get("req").getAsJsonArray();
			Type listType = new TypeToken<List<Gstr1VerticalAtRespDto>>() {
			}.getType();
			List<Gstr1VerticalAtRespDto> updateUserInputs = gson
					.fromJson(jsonObject, listType);
			JsonObject resp = gstr1AtUpdateService
					.updateVerticalData(updateUserInputs);

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fecthing Gstr1 At or Txpd "
					+ "Summary Documents " + ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
