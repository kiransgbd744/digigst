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

import com.ey.advisory.app.data.daos.client.TDSUpdateActionDaoImpl;
import com.ey.advisory.app.data.services.anx1.TDSSummaryScreenFetchService;
import com.ey.advisory.app.docs.dto.simplified.TDSSummaryRespDto;
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
public class TDSSummaryController {

	@Autowired
	@Qualifier("TDSSummaryScreenFetchService")
	TDSSummaryScreenFetchService tdsSummaryFetchService;
	
	@Autowired
	@Qualifier("TDSUpdateActionDaoImpl")
	TDSUpdateActionDaoImpl tdsUpdateFetchService;
	
	
	@RequestMapping(value = "/ui/tdsSummaryRecords", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gettdsSummaryRecords(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		LOGGER.debug("The selected criteria for Summary SCreen records is:->"
				+ requestObject.get("req"));
		// Execute the service method and get the result.
		try {

			ITC04RequestDto recordsReqDto = gson
					.fromJson(json, ITC04RequestDto.class);

			List<TDSSummaryRespDto> response = tdsSummaryFetchService
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

			LOGGER.debug(
					"Response data for given criteria for Summary Screen records is :->"
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
			String msg = "Unexpected error while retriving TDS Summary records";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// TDS ACTION 
	
	@RequestMapping(value = "/ui/tdsUpdateAction", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> updateSummaryAction(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug("The selected criteria for Summary SCreen records is:->"
				+ requestObject.get("req"));
		}
		// Execute the service method and get the result.
		try {

			ITC04RequestDto recordsReqDto = gson
					.fromJson(json, ITC04RequestDto.class);

			tdsUpdateFetchService.fetchTdsSummaryRecords(recordsReqDto);
			JsonObject resp = new JsonObject();
			/*if (response < 0) {
				String msg = "No Update Happend ";
				resp.add("hdr",
						new Gson().toJsonTree(new APIRespDto("S", msg)));
			}*/ //else {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
			//	resp.add("resp", gson.toJsonTree(response));
			//}

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving TDS Summary records";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
}
