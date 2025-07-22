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

import com.ey.advisory.app.data.services.anx1.DataStatusFileSummaryByNewFetchService;
import com.ey.advisory.app.docs.dto.DataStatusFileSummaryFinalRespDto;
import com.ey.advisory.app.docs.dto.MessageDto;
import com.ey.advisory.app.docs.dto.anx1.DataStatusFilesummaryReqDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * @author V.Mule
 *
 */
@RestController
public class DataStatusFileSummaryController {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(DataStatusFileSummaryController.class);
	@Autowired
	@Qualifier("DataStatusFileSummaryByNewFetchService")
	DataStatusFileSummaryByNewFetchService dataStatusFileSummaryByNewFetchService;

	@PostMapping(value = "/ui/getNewDataStatusFileSummary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getNewDataStatusFileSummary(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstanceWithDateFmt();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"The selected criteria for datastatus file summary is:{}",
					requestObject.get("req"));
		}

		// Execute the service method and get the result.
		try {
			DataStatusFilesummaryReqDto dataStatusApiSummaryReqDto = gson
					.fromJson(json, DataStatusFilesummaryReqDto.class);

			@SuppressWarnings("unused")
			List<DataStatusFileSummaryFinalRespDto> resp = dataStatusFileSummaryByNewFetchService
					.<DataStatusFileSummaryFinalRespDto>findDataByFileStatus(
							dataStatusApiSummaryReqDto);
			JsonObject resps = new JsonObject();
			if (!resp.isEmpty()) {
				JsonElement respBody = gson.toJsonTree(resp);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Response data for given criteria for File summary");
				}
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

			} else {
				MessageDto msgDto = new MessageDto();
				msgDto.setStatus("E");
				msgDto.setMessage("No data found for the Given Selected file.");
				JsonElement respBody = gson.toJsonTree(msgDto);
				resps.add("hdr", gson.toJsonTree(respBody));
				resps.add("resp", gson.toJsonTree(resp));
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			}
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fecthing Out/InwardSummary "
					+ "Data. ";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
