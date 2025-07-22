package com.ey.advisory.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.services.anx1.Gstr8ReviewSummaryFetchService;
import com.ey.advisory.app.data.views.client.Gstr8ReviewSummaryRespDto;
import com.ey.advisory.app.docs.dto.gstr1.Gstr8ReviewSummaryReqDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */

@Slf4j
@RestController
public class Gstr8ReviewSummaryController {

	@Autowired
	@Qualifier("Gstr8ReviewSummaryFetchService")
	Gstr8ReviewSummaryFetchService gstr8ReviewSummaryFetchService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@RequestMapping(value = "/ui/getGstr8ReviewSummary", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getProcessedRecords(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		LOGGER.debug("The selected criteria for processed records is:->"
				+ requestObject.get("req"));
		// Execute the service method and get the result.
		try {

			Gstr8ReviewSummaryReqDto reqDto = gson.fromJson(json,
					Gstr8ReviewSummaryReqDto.class);

			List<Gstr8ReviewSummaryRespDto> response = gstr8ReviewSummaryFetchService
					.getReviewSummary(reqDto);

			JsonElement responseListBody = gson.toJsonTree(response);
			Map<String, JsonElement> combinedMap = new HashMap<>();
			// lastUpdatedDate
			combinedMap.put("response", responseListBody);

			JsonElement summaryRespbody = gson.toJsonTree(combinedMap);
			JsonElement respBody = gson.toJsonTree(summaryRespbody);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			LOGGER.debug(
					"Response data for given criteria for processed records is :->"
							+ resp.toString());
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Gstr1 processed records";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}
