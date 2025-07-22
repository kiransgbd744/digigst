package com.ey.advisory.controller;

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

import com.ey.advisory.app.docs.dto.GetDataSummaryReqDto;
import com.ey.advisory.app.docs.dto.GetDataSummaryResDto;
import com.ey.advisory.app.services.search.getdatasummarysearch.GetDataSummarySearchService;
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
 * @author Anand3.M
 *
 */

@RestController
public class NRHomeController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(NRHomeController.class);
	
	@Autowired
	@Qualifier("getDataSummarySearchService")
	private GetDataSummarySearchService getDataSummarySearchService;
	
	@PostMapping(value = "/ui/getDataSummary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getDataSummaryByReceivedDate(
			                             @RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		}
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			GetDataSummaryReqDto getDataSumRequest = gson.fromJson(reqJson.toString(),
					GetDataSummaryReqDto.class);
			@SuppressWarnings("unused")
			List<GetDataSummaryResDto> resp = getDataSummarySearchService
					                    .<GetDataSummaryResDto>find(getDataSumRequest);
			JsonObject resps = new JsonObject();
			if(!resp.isEmpty()) {
				JsonElement respBody = gson.toJsonTree(resp);
				resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			} else {
				JsonElement respBody = gson.toJsonTree("No data found for the Given Date.");
				resps.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			}
			
		}

		catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fecthing NR "
					+ "Summary Documents ";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} 
	}
}