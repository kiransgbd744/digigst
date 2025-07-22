package com.ey.advisory.controller.gstr2b;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.gstr2b.summary.Gstr2BSummaryDto;
import com.ey.advisory.app.gstr2b.summary.Gstr2BSummaryService;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author hema.gm
 *
 */

@Slf4j
@RestController
public class Gstr2bSummaryController {

	@Autowired
	@Qualifier("Gstr2BSummaryServiceImpl")
	private Gstr2BSummaryService service;

	@PostMapping(value = "/ui/getGstr2BSummary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr2BSummaryResponse(
			@RequestBody String jsonString) {

		JsonArray gstins = new JsonArray();
		List<String> gstnsList = null;
		Gson googleJson = new Gson();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject errorResp = new JsonObject();

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Begin Gstr2bSummaryController"
						+ ".getGstr2BSummaryResponse() method, Parsing Input "
							+ "request %s ", jsonString);
				LOGGER.debug(msg);
			}

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonObject reqJson = requestObject.get("req").getAsJsonObject();
			
			Long entityId = reqJson.has("entityId")
					? reqJson.get("entityId").getAsLong() : 0L;

			String fromTaxPeriod = reqJson.has("fromTaxPeriod")
					? reqJson.get("fromTaxPeriod").getAsString() : null;

			String toTaxPeriod = reqJson.has("toTaxPeriod")
					? reqJson.get("toTaxPeriod").getAsString() : null;

			if ((reqJson.has("gstin"))
					&& (reqJson.getAsJsonArray("gstin").size() > 0)) {
				gstins = reqJson.getAsJsonArray("gstin");
				Type listType = new TypeToken<ArrayList<String>>() {
				}.getType();
				gstnsList = googleJson.fromJson(gstins, listType);
			}						
			
			if (gstnsList == null || gstnsList.isEmpty()) {
				try {
					List<Long> entityIds = new ArrayList<>();
					entityIds.add(entityId);
					Map<String, String> outwardSecurityAttributeMap = 
							DataSecurityAttributeUtil
							.getOutwardSecurityAttributeMap();
					Map<String, List<String>> dataSecAttrs = 
							DataSecurityAttributeUtil
							.dataSecurityAttrMapForQuery(entityIds,
									outwardSecurityAttributeMap);
					gstnsList = dataSecAttrs.get(OnboardingConstant.GSTIN);

				} catch (Exception ex) {
					String msg = String.format(
							"Error while fetching Gstins from entityId ", ex);
					LOGGER.error(msg);
					throw new AppException(msg);
				}
			}
			List<Gstr2BSummaryDto> resp = service
					.getGstr2bSummary(gstnsList, toTaxPeriod, fromTaxPeriod);
			
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
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

		} catch (AppException ex) {
			String msg = ex.getMessage();
			LOGGER.error(ex.getMessage(), ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}
}
