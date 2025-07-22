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

import com.ey.advisory.app.services.search.docsummarysearch.Gstr2PRSummaryReqRespHandler;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr2ProcessedRecordsReqDto;
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
public class Gstr2PRSummaryController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2PRSummaryController.class);

	@Autowired
	@Qualifier("Gstr2PRSummaryReqRespHandler")
	Gstr2PRSummaryReqRespHandler reqRespHandler;
	
	@PostMapping(value = "/ui/getGstr2ReviewPrSummary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> eyGstr1Summary(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
		//	Gstr2ProcessedRecordsReqDto dto= new Gstr2ProcessedRecordsReqDto();
			
			Gson gson = GsonUtil.newSAPGsonInstance();
			
			Gstr2ProcessedRecordsReqDto gstr6SummaryRequest = gson
					.fromJson(reqJson.toString(), Gstr2ProcessedRecordsReqDto.class);
			
			JsonElement handleGstr1ReqAndResp = reqRespHandler
					.handleGstr1ReqAndResp(gstr6SummaryRequest);
			
			JsonObject resp = new JsonObject();
			resp.add("resp", handleGstr1ReqAndResp);
			
			//String resp = null;
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	catch (JsonParseException ex) {
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
