package com.ey.advisory.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.services.anx1.ITC04ProcessSummaryFetchService;
import com.ey.advisory.app.docs.dto.simplified.ITC04ProcessSummaryRespDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.ITC04RequestDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Slf4j
@RestController
public class ITC04ProcessSummaryController {

	@Autowired
	@Qualifier("ITC04ProcessSummaryFetchService")
	ITC04ProcessSummaryFetchService itcProcessSummaryFetchService;
	
	
	@RequestMapping(value = "/ui/itcProcessSummary", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getProcessedRecords(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		if(LOGGER.isDebugEnabled()) {
		LOGGER.debug("The selected criteria for processed records is:->"
				+ requestObject.get("req"));
		}
		// Execute the service method and get the result.
		try {

			ITC04RequestDto recordsReqDto = gson
					.fromJson(json, ITC04RequestDto.class);

			List<ITC04ProcessSummaryRespDto> response = itcProcessSummaryFetchService
					.response(recordsReqDto);
			JsonObject resp = new JsonObject();
			if (response.isEmpty()) {
				String msg = "No data found";
				resp.add("hdr",
						new Gson().toJsonTree(new APIRespDto("S", msg)));
			} else {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(response));
			}
			if(LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Response data for given criteria for processed records is :->"
							+ resp.toString());
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving ITC04 processed records";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	
}
