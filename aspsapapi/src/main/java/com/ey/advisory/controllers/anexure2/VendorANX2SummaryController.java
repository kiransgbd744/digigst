/**
 * 
 */
package com.ey.advisory.controllers.anexure2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.VendorSummaryRespDto;
import com.ey.advisory.app.services.search.anx2.VendorANX2SummaryService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.VendorSummaryReqDto;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * @author BalaKrishna S
 *
 */
@RestController
public class VendorANX2SummaryController {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(VendorANX2SummaryController.class);
	
	@Autowired
	@Qualifier("VendorANX2SummaryService")
	VendorANX2SummaryService service;
		
	@RequestMapping(value = "/ui/getVenorANX2Sumary", method = 
			RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getVendorANX2Summary(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		// Execute the service method and get the result.
		try {
			VendorSummaryReqDto criteria = gson.fromJson(json,
					VendorSummaryReqDto.class);
			
			SearchResult<VendorSummaryRespDto> searchResult = 
					service.find(criteria, null, VendorSummaryRespDto.class);
			
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(searchResult.getResult());
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
			String msg = "Unexpected error while retriving Vendor "
					+ "ANX2 Summary Data ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


}
