/*
 * This controlled is used to test in Local only.
 */
package com.ey.advisory.app.services.jobs.erp.vendorcommunication;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;
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

@RestController
@Slf4j
public class VendorMissMatchController {
	
	@Autowired
	@Qualifier("VendorMismatchServiceImpl")
	private VendorMismatchService rv;
	
	@Autowired
	@Qualifier("VendorMismatchPushErpImpl")
	VendorMismatchPushErp vendorMismatcPush;
	
	@PostMapping(value = "/getVendorMismatchRI",
			produces = {MediaType.APPLICATION_JSON_VALUE })
public ResponseEntity<String> getProcessedRecords(
		@RequestBody String jsonString) {
	
	JsonObject requestObject = (new JsonParser()).parse(
			jsonString).getAsJsonObject();

	JsonObject json = requestObject.get("req").getAsJsonObject();
	
	Gson gson = GsonUtil.newSAPGsonInstance();
	
	RevIntegrationScenarioTriggerDto revIntegerationDto = gson.fromJson(json
			, RevIntegrationScenarioTriggerDto.class);
	
	// Destination Name is Final Point were Data is to be pushed to ERP
	
	String destinationName = revIntegerationDto.getDestinationName();
	
	String taxPeriods = "082019";
	
	List<String> taxPeriod = new ArrayList<String>();
	taxPeriod.add(taxPeriods);

	try {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Get Processed Records";
			LOGGER.debug(msg);
		}
		String groupCode = TenantContext.getTenantId();
		revIntegerationDto.setGroupcode(groupCode);
		List<VendorMismatchDto> Resp = 
				rv.getVendorMismatchRecords(revIntegerationDto);
		
		VendorMismatchRevRecordsDto respDto = new VendorMismatchRevRecordsDto();
		respDto.setItem(Resp);

		vendorMismatcPush.pushToErp(respDto, destinationName, null);
		
		
		if (LOGGER.isDebugEnabled()) {
			String msg = "EntityServiceImpl"
					+ ".getAllEntities Preparing Response Object";
			LOGGER.debug(msg);
		}
		JsonObject gstinResp = new JsonObject();
		JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(Resp);
		gstinResp.add("entities", respBody);
		resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp.add("resp", gstinResp);
		if (LOGGER.isDebugEnabled()) {
			String msg = "End EntityServiceImpl"
					+ ".getAllEntities ,before returning response";
			LOGGER.debug(msg);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	} catch (JsonParseException ex) {
		String msg = "Error while parsing the input Json";
		LOGGER.error(msg, ex);

		JsonObject resp = new JsonObject();
		resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

		return new ResponseEntity<>(resp.toString(),
				HttpStatus.INTERNAL_SERVER_ERROR);

	} catch (Exception ex) {
		String msg = " Unexpected error occured ";
		LOGGER.error(msg, ex);

		JsonObject resp = new JsonObject();
		resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

		return new ResponseEntity<>(resp.toString(),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}
 }	
}		

