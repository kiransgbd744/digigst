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

import com.ey.advisory.app.docs.dto.Gstr1SummaryReqDto;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryReqRespHandler;
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
 * @author Mahesh.Golla
 *
 */

@RestController
public class Gstr1SummaryScreenController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1SummaryScreenController.class);

	@Autowired
	@Qualifier("Gstr1ReqRespHandler")
	Gstr1SummaryReqRespHandler gstr1ReqRespHandler;
	
	@PostMapping(value = "/ui/gstr1Summary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> eyGstr1Summary(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			Gstr1SummaryReqDto gstr1SummaryRequest = 
					gson.fromJson(reqJson.toString(),
					Gstr1SummaryReqDto.class);
			
			JsonElement summaryRespBody = gstr1ReqRespHandler
					.handleGstr1ReqAndResp(gstr1SummaryRequest);
			
			JsonObject resp = new JsonObject();
			resp.add("resp", summaryRespBody);
			
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
			String msg = "Unexpected error while fecthing Gstr1 "
					+ "Summary Documents "+ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} 
		
	}
	
	
}