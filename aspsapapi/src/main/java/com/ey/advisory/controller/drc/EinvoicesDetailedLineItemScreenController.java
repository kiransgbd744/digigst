/**
 * 
 */
package com.ey.advisory.controller.drc;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.inward.einvoice.EinvoiceDetailedLineItemReportDto;
import com.ey.advisory.app.inward.einvoice.EinvoicesDetailedLineItemScreenService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Ravindra V S
 *
 */

@Slf4j
@RestController
public class EinvoicesDetailedLineItemScreenController {

	
	@Autowired
	@Qualifier("EinvoicesDetailedLineItemScreenServiceImpl")
	private EinvoicesDetailedLineItemScreenService invService;

	@PostMapping(value = "/ui/getInwardIrnDetailedData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getInwardIrnDetailedData(
			@RequestBody String jsonString) throws IOException {

		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();
		
		JsonObject jsonObj = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		String irnNum = json.get("irnNum").getAsString();
		String msg1 = String
				.format("Inside EinvoicesDetailedLineItemScreenController.getInwardIrnDetailedData() "
						+ "method : %s {} ", jsonString);
		LOGGER.debug(msg1);
		try {

			List<EinvoiceDetailedLineItemReportDto> recResponse = invService
					.findTableData(irnNum);

			JsonElement respBody = gson.toJsonTree(recResponse);
			jsonObj.add("hdr",
					gson.toJsonTree(APIRespDto.createSuccessResp()));
			jsonObj.add("resp", respBody);
			
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed",
					"Unexpected occured in Controller"
							+ e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Error Occured in Controller";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}

}