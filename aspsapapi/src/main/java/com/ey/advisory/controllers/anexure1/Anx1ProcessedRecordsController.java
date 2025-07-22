package com.ey.advisory.controllers.anexure1;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.services.anx1.Anx1ProcessedRecordsFetchService;
import com.ey.advisory.app.docs.dto.anx1.Anx1ProcessedRecordsFinalRespDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Anx1ProcessedRecordsReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * @author V.Mule
 *
 */
@RestController
public class Anx1ProcessedRecordsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ProcessedRecordsController.class);

	@Autowired
	@Qualifier("Anx1ProcessedRecordsFetchService")
	Anx1ProcessedRecordsFetchService anx1ProcessedRecordsFetchService;

	@RequestMapping(value = "/ui/getProcessedRecords", method = RequestMethod.POST, produces = {
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

			Anx1ProcessedRecordsReqDto processedRecordsReqDto = gson
					.fromJson(json, Anx1ProcessedRecordsReqDto.class);

			List<Anx1ProcessedRecordsFinalRespDto> respDtos = anx1ProcessedRecordsFetchService
					.find(processedRecordsReqDto, "ANX1");

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(respDtos));

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
			String msg = "Unexpected error while retriving Anx1 processed records";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/ui/getAnx1aProcessedRecords", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAnx1aProcessedRecords(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		LOGGER.debug("The selected criteria for processed records is:->"
				+ requestObject.get("req"));
		// Execute the service method and get the result.
		try {

			Anx1ProcessedRecordsReqDto processedRecordsReqDto = gson
					.fromJson(json, Anx1ProcessedRecordsReqDto.class);

			List<Anx1ProcessedRecordsFinalRespDto> respDtos = anx1ProcessedRecordsFetchService
					.find(processedRecordsReqDto, "ANX1A");

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(respDtos));

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
			String msg = "Unexpected error while retriving Anx1 processed records";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
