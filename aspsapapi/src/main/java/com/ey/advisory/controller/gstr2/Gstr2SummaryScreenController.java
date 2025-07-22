package com.ey.advisory.controller.gstr2;

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

import com.ey.advisory.app.docs.dto.gstr2.Gstr2SummaryDto;
import com.ey.advisory.app.docs.dto.gstr2.Gstr2SummaryReqDto;
import com.ey.advisory.app.services.docs.gstr2.Gstr2DocSummarySearchService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

@RestController
public class Gstr2SummaryScreenController {
	
	@Autowired
	@Qualifier("Gstr2DocSummarySearchService")
	private Gstr2DocSummarySearchService docSummarySearchService ;
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2SummaryScreenController.class);
	
	@PostMapping(value = "/ui/gstr2Summary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> eyGstr2Summary(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			Gstr2SummaryReqDto gstr1SummaryRequest = 
					gson.fromJson(reqJson.toString(),
					Gstr2SummaryReqDto.class);
			
		/*	SearchResult<Gstr2SummaryDto> gstr1SummaryResp = docSummarySearchService
					.find(gstr1SummaryRequest, null, Gstr2SummaryDto.class);*/
		
			Gstr2SummaryDto gstr1SummaryResp = docSummarySearchService
					.find(gstr1SummaryRequest);
		/*	JsonElement summaryRespBody = gstr1ReqRespHandler
					.handleGstr1ReqAndResp(gstr1SummaryResp);
*/			
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(gstr1SummaryResp));
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
			String msg = "Unexpected error while fecthing Gstr2 "
					+ "Summary Documents "+ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} 
		
	}



}
