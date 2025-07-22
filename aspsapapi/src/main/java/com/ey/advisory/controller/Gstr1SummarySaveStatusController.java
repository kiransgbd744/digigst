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

import com.ey.advisory.app.docs.dto.gstr1.Gstr1SummarySaveStatusRespDto;
import com.ey.advisory.app.services.daos.get2a.GetGstr1SummarySaveStatusService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1SummarySaveStatusReqDto;
import com.fasterxml.jackson.core.JsonParseException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author Sasidhar
 *
 * 
 */

@RestController
public class Gstr1SummarySaveStatusController {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1SummarySaveStatusController.class);

	@Autowired
	@Qualifier("GetGstr1SummarySaveStatusService")
	private GetGstr1SummarySaveStatusService gstr1SummarySaveStatusService;

	public static void main(String[] args) {

		String msg = "Unexpected error while retriving Detail Status ";
		JsonObject resp = new JsonObject();
		resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		System.out.println(resp);

	}

	@PostMapping(value = "/ui/gstr1SummarySaveStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr1SummarySaveStatus(
			@RequestBody String jsonString) throws JsonParseException {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject errorResp = new JsonObject();
		// Execute the service method and get the result.
		try {
			Gstr1SummarySaveStatusReqDto criteria = gson.fromJson(json,
					Gstr1SummarySaveStatusReqDto.class);

			List<Gstr1SummarySaveStatusRespDto> summaryList = gstr1SummarySaveStatusService
					.findByCriteria(criteria);
			JsonObject resps = new JsonObject();
			if (!summaryList.isEmpty()) {
				JsonElement respBody = gson.toJsonTree(summaryList);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			} else {
				return createGstinNodataSuccessResp();
			}
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json payload";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (RuntimeException ex) {
			errorResp.add("hdr", new Gson()
					.toJsonTree(new APIRespDto("E", ex.getMessage())));
			return new ResponseEntity<>(errorResp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Detail Status ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public ResponseEntity<String> createGstinNodataSuccessResp() {
		JsonObject resp = new JsonObject();
		resp.add("hdr", new Gson().toJsonTree(
				new APIRespDto("S", " No Data for the selected gstins. ")));
		return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
	}
}
