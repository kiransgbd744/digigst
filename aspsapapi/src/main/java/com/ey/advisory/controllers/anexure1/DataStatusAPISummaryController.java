package com.ey.advisory.controllers.anexure1;

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

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.docs.dto.DataStatusApiSummaryResDto;
import com.ey.advisory.app.services.search.apisummarysearch.DataStatusApiSummaryService;
import com.ey.advisory.app.services.search.datastatus.anx1.DataStatusSummaryCommonSecParam;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.DataStatusApiSummaryReqDto;
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
public class DataStatusAPISummaryController {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(DataStatusAPISummaryController.class);

	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository dataStatusRepository;

	@Autowired
	@Qualifier("DataStatusSummaryCommonSecParam")
	DataStatusSummaryCommonSecParam dataStatusSummaryCommonSecParam;

	@Autowired
	@Qualifier("DataStatusApiSummaryService")
	DataStatusApiSummaryService dataStatusApiSummaryService;

	@PostMapping(value = "/ui/getDataStatusApiSummary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> apiSummaryByReceivedDate(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstanceWithDateFmt();
		if (LOGGER.isDebugEnabled()) {

			LOGGER.debug(
					"The selected criteria for datastatus api summary is:{}",
					requestObject.get("req"));
		}
		// Execute the service method and get the result.
		try {

			DataStatusApiSummaryReqDto dataStatusApiSummaryReqDto = gson
					.fromJson(json, DataStatusApiSummaryReqDto.class);

			@SuppressWarnings("unused")
			List<DataStatusApiSummaryResDto> resp = dataStatusApiSummaryService
					.<DataStatusApiSummaryResDto>find(
							dataStatusApiSummaryReqDto);
			JsonObject resps = new JsonObject();
			if (!resp.isEmpty()) {
				JsonElement respBody = gson.toJsonTree(resp);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Response data for given criteria for processed records is :{}",
							resp);

				}
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

			} else {
				JsonElement respBody = gson.toJsonTree(
						"No data found for the Given Dates for an given entity.");
				resps.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resps.add("resp", respBody);
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
