package com.ey.advisory.controller.inward.einvoice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.inward.einvoice.InwardEinvoiceGstinLevelReqDto;
import com.ey.advisory.app.inward.einvoice.InwardEinvoiceGstinLevelResponseDto;
import com.ey.advisory.app.inward.einvoice.InwardEinvoiceGstinLevelService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */

@Slf4j
@RestController
public class InwardEInvoiceGstinLevelController {

	@Autowired
	@Qualifier("InwardEinvoiceGstinLevelServiceImpl")
	private InwardEinvoiceGstinLevelService service;

	@RequestMapping(value = "/ui/inwardEinvoiceGstinLevelData", method 
			= RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> ewb3WaySummaryInitiateRecon(
			@RequestBody String jsonString) {

		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();

		JsonObject jsonObj = new JsonObject();

		JsonObject reqJson = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {

			InwardEinvoiceGstinLevelReqDto criteria = gson.fromJson(reqJson,
					InwardEinvoiceGstinLevelReqDto.class);

			// Executing the service method and getting the result.

				InwardEinvoiceGstinLevelResponseDto recResponse = 
						service.getGstinLevelData(criteria);

				JsonElement respBody = gson.toJsonTree(recResponse);
				jsonObj.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				jsonObj.add("resp", respBody);
				
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
			
		} catch (JsonParseException ex) {
			String msg = "InwardEInvoiceGstinLevelController- "
					+ "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "InwardEInvoiceGstinLevelController "
					+ "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
