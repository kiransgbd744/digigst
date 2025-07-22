package com.ey.advisory.controller.gstr2;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.services.anx1.Gstr2bVsGstr3bPrSummaryFetchService;
import com.ey.advisory.app.docs.dto.Gstr2aVsGstr3bProcessSummaryFinalRespDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;
import com.google.gson.Gson;
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
public class Gstr2bVsGstr3bProcessedSummaryController {

	@Autowired
	@Qualifier("Gstr2bVsGstr3bPrSummaryFetchService")
	Gstr2bVsGstr3bPrSummaryFetchService gstr2bVsGstr3bPrSummaryFetchService;

	@RequestMapping(value = "/ui/gstr2bVsGstr3bProcessSummary", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr2aVsGstr3bProcessSummary(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		LOGGER.debug("The selected criteria for processed records is:->"
				+ requestObject.get("req"));
		// Execute the service method and get the result.
		try {

			Gstr1VsGstr3bProcessSummaryReqDto recordsReqDto = gson
					.fromJson(json, Gstr1VsGstr3bProcessSummaryReqDto.class);

			List<Gstr2aVsGstr3bProcessSummaryFinalRespDto> response = gstr2bVsGstr3bPrSummaryFetchService
					.fetchResponse(recordsReqDto);
			
			List<Gstr2aVsGstr3bProcessSummaryFinalRespDto> sortedResponse = response.stream()
				    .sorted(Comparator.comparing(Gstr2aVsGstr3bProcessSummaryFinalRespDto::getGstin))
				    .collect(Collectors.toList());

			
			JsonObject resp = new JsonObject();
			if (sortedResponse.isEmpty()) {
				String msg = "No data found";
				resp.add("hdr",
						new Gson().toJsonTree(new APIRespDto("S", msg)));
			} else {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(sortedResponse));
			}

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
			String msg = "Unexpected error while retriving Gstr2bvs3b processed records";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}