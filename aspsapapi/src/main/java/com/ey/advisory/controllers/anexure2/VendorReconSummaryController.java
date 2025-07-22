package com.ey.advisory.controllers.anexure2;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.anx2.vendorsummary.VendorReconSummaryReqDto;
import com.ey.advisory.app.anx2.vendorsummary.VendorReconSummaryResponseDto;
import com.ey.advisory.app.anx2.vendorsummary.VendorReconSummaryService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstr2.userdetails.EntityService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Nikhil.Duseja
 *
 */

@Slf4j
@RestController
public class VendorReconSummaryController {
	
	@Autowired 
	@Qualifier("VendorReconSummaryServiceImpl")
	VendorReconSummaryService service;
	
	@Autowired
	private EntityService entityService; 
	
	@PostMapping(value = "/ui/getAnx2VendorReconSummary", produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getvendorReconSummaryDet(@RequestBody
			String jsonReq) {
		
		JsonObject requestObject = (new JsonParser()).parse(jsonReq)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Gson googleJson = new Gson();
		JsonArray gstins = new JsonArray(); 
		List<String> gstnsList = null; 
		
		// Execute the service method and get the result.
		try {
			
			VendorReconSummaryReqDto criteria = gson.fromJson(json,
					VendorReconSummaryReqDto.class);
			
			
			//getting info of GSTIN and setting for a particular entity
			if ((json.has("gstins"))
					&& (json.getAsJsonArray("gstins").size() > 0)) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"As gstins are provided in request, not Invoking"
									+ "service to get gstins for entity");
				}
				gstins = json.getAsJsonArray("gstins");
				Type listType = new TypeToken<ArrayList<String>>() {
				}.getType();
				gstnsList = googleJson.fromJson(gstins, listType);
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Invoking service to get gstins for entity");
				}
				gstnsList = entityService
						.getGSTINsForEntity(criteria.getEntityId());
				gstnsList = gstnsList.stream().distinct()
						.collect(Collectors.toList());
				criteria.setGstins(gstnsList);
			}
			
			List<VendorReconSummaryResponseDto> searchResult = 
					service.getReconSummaryDetails(criteria);

			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(searchResult);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
	  }  catch (JsonParseException ex) {
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
