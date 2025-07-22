package com.ey.advisory.controller.gstr2;

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

import com.ey.advisory.app.data.services.anx1.Gstr2aProcessedRecordsService;
import com.ey.advisory.app.docs.dto.anx1.Gstr2aProcessedDataRecordsRespDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr2aProcessedDataRecordsReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * 
 * @author Anand3.M
 *
 */
@RestController
public class Gstr2aProcessedDataController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2aProcessedDataController.class);

	@Autowired
	@Qualifier("Gstr2aProcessedRecordsService")
	private Gstr2aProcessedRecordsService gstr2aProcessedRecordsService;

	@RequestMapping(value = "/ui/getGstr2aProcess", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr2AProcessedRecords(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		LOGGER.debug("The selected criteria for processed records is:->"
				+ requestObject.get("req"));
		// Execute the service method and get the result.
		try {
			PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR_2A_PROCESS,
					"Process_START", "Gstr2aProcessedDataController",
					"getGstr2AProcessedRecords", "");

			Gstr2aProcessedDataRecordsReqDto gstr2AProcessedDataRecordsReqDto = gson
					.fromJson(json, Gstr2aProcessedDataRecordsReqDto.class);

			PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR_2A_PROCESS,
					"FINDDetails_START", "Gstr2aProcessedDataController",
					"getGstr2AProcessedRecords", "");
			List<Gstr2aProcessedDataRecordsRespDto> respDtos = gstr2aProcessedRecordsService
					.findGstr2aProcessedRecords(
							gstr2AProcessedDataRecordsReqDto);

			PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR_2A_PROCESS,
					"FINDDetails_END", "Gstr2aProcessedDataController",
					"getGstr2AProcessedRecords", "");
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(respDtos));
			LOGGER.debug(
					"Response data for given criteria for processed data records is :->"
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
			String msg = "Unexpected error while retrieving Gstr2a processed data records";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
