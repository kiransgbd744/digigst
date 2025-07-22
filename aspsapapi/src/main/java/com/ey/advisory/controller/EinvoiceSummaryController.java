/**
 * 
 */
package com.ey.advisory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.services.search.docsummarysearch.EinvSummaryScreenReqRespHandler;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.EinvoiceProcessedReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author BalaKrishna S
 *
 */
@Slf4j
@RestController
public class EinvoiceSummaryController {

	@Autowired
	@Qualifier("EinvSummaryScreenReqRespHandler")
	EinvSummaryScreenReqRespHandler gstr1ReqRespHandler;


	@PostMapping(value = "/ui/einvSummaryScreen", produces = {
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

			EinvoiceProcessedReqDto annexure1SummaryRequest = gson
					.fromJson(reqJson.toString(), EinvoiceProcessedReqDto.class);

		

			JsonElement outwardSummaryRespBody = gson
					.toJsonTree(gstr1ReqRespHandler.handleGstr1ReqAndResp(
							annexure1SummaryRequest,null));

					
		/*	Map<String, JsonElement> combinedMap = new HashMap<>();
			// lastUpdatedDate
			combinedMap.put("outward", outwardSummaryRespBody);

			JsonElement summaryRespbody = gson.toJsonTree(combinedMap);
			JsonElement respBody = gson.toJsonTree(summaryRespbody);*/

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", outwardSummaryRespBody);

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
