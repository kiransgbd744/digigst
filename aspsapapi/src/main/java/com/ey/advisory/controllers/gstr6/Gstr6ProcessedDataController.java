/**
 * 
 */
package com.ey.advisory.controllers.gstr6;

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

import com.ey.advisory.app.docs.dto.gstr6.Gstr6ProcessedSummResponseDto;
import com.ey.advisory.app.docs.services.gstr6.Gstr6ProcessedDataService;
import com.ey.advisory.common.BasicGstr6SecCommonParam;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr6SummaryRequestDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * @author Laxmi.Salukuti
 *
 */
@RestController
public class Gstr6ProcessedDataController {

	@Autowired
	@Qualifier("Gstr6ProcessedDataServiceImpl")
	private Gstr6ProcessedDataService gstr6ProcessedDataService;

	@Autowired
	@Qualifier("BasicGstr6SecCommonParam")
	BasicGstr6SecCommonParam basicGstr6SecCommonParam;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6ProcessedDataController.class);

	@PostMapping(value = "/ui/getGstr6ProcessedData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr6ProcessedData(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			Gstr6SummaryRequestDto criteria = gson.fromJson(json,
					Gstr6SummaryRequestDto.class);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr6ProcessSumm Adapter Filters Setting to Request BEGIN");
			}
			Gstr6SummaryRequestDto setDataSecurity = basicGstr6SecCommonParam
					.setDataSecuritySearchParams(criteria);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr6ProcessSumm Adapter Filters Setting to Request END");
			}
			List<Gstr6ProcessedSummResponseDto> recResponse = gstr6ProcessedDataService
					.getGstr6ProcessedRec(setDataSecurity);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(recResponse);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}
