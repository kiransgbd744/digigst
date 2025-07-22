package com.ey.advisory.controller.dashboard.recon.details;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.dashboard.recon.details.DashboardReconDetailsDto;
import com.ey.advisory.app.dashboard.recon.details.DashboardReconDetailsService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.controllers.anexure1.InputValidationUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */
@Slf4j
@RestController
public class DashboardReconDetailsController {
	
	@Autowired
	@Qualifier("DashboardReconDetailsServiceImpl")
	private DashboardReconDetailsService detService;
		
	@PostMapping(value="/ui/getDashboardReconDetails" ,
						produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<String> getDashboardReconDetails(@RequestBody
			String jsonString){
		
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin DashboardReconDetailsController"
						+ ".getDashboardReconDetails, Parsing Input request";
				LOGGER.debug(msg);
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();
			Long entityId = requestObject.get("entityId").getAsLong();
			String taxPeriod = requestObject.get("taxPeriod").getAsString();
			
			
			DashboardReconDetailsDto summary = detService
					.getDashboardReconDetails(entityId, taxPeriod);
		
			if (LOGGER.isDebugEnabled()) {
				String msg = "Invoked DashboardReconDetailServiceImpl"
					+ ".getDashboardReconDetails Preparing Response Object";
				LOGGER.debug(msg);
			}
			
			JsonObject resp = new JsonObject();
			JsonObject detResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(summary);
			detResp.add("det", respBody);

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", detResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End DashboardReconDetailsController"
					+ ".getDashboardReconDetails, before returning response";
				LOGGER.debug(msg);
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {

			return InputValidationUtil.createJsonErrResponse(ex);
		}
	}

}

