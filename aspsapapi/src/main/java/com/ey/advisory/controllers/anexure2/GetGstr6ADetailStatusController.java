package com.ey.advisory.controllers.anexure2;

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

import com.ey.advisory.app.docs.dto.GetAnx2DetailStatusReqDto;
import com.ey.advisory.app.docs.dto.GetGstr6DetailStatusMainDetailsRespDto;
import com.ey.advisory.app.services.daos.get2a.GetGstr6ADetailStatusService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.fasterxml.jackson.core.JsonParseException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
public class GetGstr6ADetailStatusController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GetGstr6ADetailStatusController.class);

	@Autowired
	@Qualifier("GetGstr6ADetailStatusService")
	private GetGstr6ADetailStatusService getGstr6ADetailStatusService;

	@PostMapping(value = "/ui/getGstr6aDetailStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAnx2DetailStatus(
			@RequestBody String jsonString) throws JsonParseException {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject errorResp = new JsonObject();
		// Execute the service method and get the result.
		try {
			GetAnx2DetailStatusReqDto criteria = gson.fromJson(json,
					GetAnx2DetailStatusReqDto.class);

			GetGstr6DetailStatusMainDetailsRespDto summaryList = getGstr6ADetailStatusService
					.findByCriteria(criteria);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(summaryList);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

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

}
