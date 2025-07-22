package com.ey.advisory.controller;

import java.util.HashMap;
import java.util.Map;

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

import com.ey.advisory.app.services.search.simplified.docsummary.Annexure1ReqRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Anx1GetGstnDataSearchService;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

@RestController
public class SimplifiedSummaryScreenController {
	
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(SimplifiedSummaryScreenController.class);
	
	@Autowired
	@Qualifier("Annexure1ReqRespHandler")
	Annexure1ReqRespHandler annexure1ReqRespHandler;
	
	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;
	
	@Autowired
	@Qualifier("Anx1GetGstnDataSearchService")
	Anx1GetGstnDataSearchService gstnService;
	
	@PostMapping(value = "/ui/annexure1SummaryForDemo", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> eyAnnexure1SummaryForDemo(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			Annexure1SummaryReqDto annexure1SummaryRequest = 
					gson.fromJson(reqJson.toString(),
							Annexure1SummaryReqDto.class);
				
			
		/*	LOGGER.debug(" DataSecurity Adapter Filters Assigned to Request BEGIN");
			Annexure1SummaryReqDto setSumDataSecurity = basicCommonSecParam
					.setSumDataSecuritySearchParams(annexure1SummaryRequest);
			LOGGER.debug(" DataSecurity Adapter Filters Assigned to Request END");*/
		/*	JsonElement summaryRespBody = annexure1ReqRespHandler
					.handleAnnexure1ReqAndResp(annexure1SummaryRequest);
			*/
			JsonElement summaryRespBody = null;
			JsonElement b2bRespbody = gson.toJsonTree(summaryRespBody);
			JsonObject resp = new JsonObject();
			resp.add("resp", b2bRespbody);
			
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
					+ "Summary Documents "+ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} 
	}
	
	@PostMapping(value = "/ui/annexure1Summary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> eyAnnexure1Summary(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			Annexure1SummaryReqDto annexure1SummaryRequest = 
					gson.fromJson(reqJson.toString(),
							Annexure1SummaryReqDto.class);
			// Gstn Data From  ANX1 Get Summary API.
			
		/*	SearchResult<Annexure1SummaryDto> gstnSummary = gstnService.find(
					annexure1SummaryRequest, null, Annexure1SummaryDto.class);
			List<? extends Annexure1SummaryDto> gstnResult = gstnSummary.getResult();
			
			*/
			
			JsonElement outwardSummaryRespBody = annexure1ReqRespHandler
						.handleAnnexure1ReqAndResp(annexure1SummaryRequest);
			
			JsonElement InwardSummaryRespBody = annexure1ReqRespHandler
					.handleInwardAnnexure1ReqAndResp(annexure1SummaryRequest);
			
			JsonElement ecomSummaryRespBody = annexure1ReqRespHandler
					.handleEcommAnnexure1ReqAndResp(annexure1SummaryRequest);
					
		
			Map<String, JsonElement> combinedMap = new HashMap<>();

			combinedMap.put("outward", outwardSummaryRespBody);
			
			combinedMap.put("inward", InwardSummaryRespBody);
			combinedMap.put("ecom", ecomSummaryRespBody);
		
			JsonElement summaryRespbody = gson.toJsonTree(combinedMap);
			JsonElement respBody = gson.toJsonTree(summaryRespbody);
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
					+ "Summary Documents "+ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} 
	}

}
