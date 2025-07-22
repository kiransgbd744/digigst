/**
 * 
 */
package com.ey.advisory.controllers.anexure2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.anx2.initiaterecon.InitiateReconReportRequestStatusDto;
import com.ey.advisory.app.anx2.initiaterecon.InitiateReconReportRequestStatusService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@RestController
public class Anx2ReconRequestStatusController {
	
	@Autowired
	@Qualifier("InitiateReconReportRequestStatusServiceImpl")
	private InitiateReconReportRequestStatusService reportStatusService;
	
	@PostMapping(value = "/ui/getAnx2ReportRequestStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getReportRequestStatus(
			@RequestBody String jsonString) {

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin Anx2ReconRequestStatusController"
						+ ".getReportRequestStatus ,Parsing Input request";
				LOGGER.debug(msg);
				}
			  User user = SecurityContext.getUser();
			  String userName = user.getUserPrincipalName(); 
				 

			Gson gson = GsonUtil.newSAPGsonInstance();
					
			List<InitiateReconReportRequestStatusDto> status = 
					reportStatusService.getReportRequestStatus(userName);
			if (LOGGER.isDebugEnabled()) {
				String msg = "InitiateReconReportRequestStatusServiceImpl"
						+ ".getReportRequestStatus Preparing Response Object";
				LOGGER.debug(msg);
				}
			JsonObject resp = new JsonObject();
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(status); 
			gstinResp.add("requestDetails", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End Anx2ReconRequestStatusController"
					+ ".getReportRequestStatus, before returning response";
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
			String msg = " Unexpected error occured";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
			