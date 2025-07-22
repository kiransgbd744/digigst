/**
 * 
 */
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

import com.ey.advisory.app.docs.dto.Gstr2BLinkingDto;
import com.ey.advisory.app.services.docs.gstr2b.Gstr2bLinkingResponseDto;
import com.ey.advisory.app.services.docs.gstr2b.Gstr2bLinkingService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * @author Shashikant.Shukla
 *
 */
@RestController
public class Gstr2BLinkingController {

	@Autowired
	@Qualifier("Gstr2bLinkingServiceImpl")
	private Gstr2bLinkingService gstrbLinkingService;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2BLinkingController.class);

	@PostMapping(value = "/ui/getGstr2bLinkingData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr6ProcessedData(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			Gstr2BLinkingDto criteria = gson.fromJson(json,
					Gstr2BLinkingDto.class);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2BLinkingController Adapter Filters Setting to Request BEGIN");
			}
			List<Gstr2bLinkingResponseDto> recResponse = gstrbLinkingService
					.getGstr2bLinkingRecords(criteria);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2BLinkingController Adapter Filters Setting to Request END");
			}

			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(recResponse);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
