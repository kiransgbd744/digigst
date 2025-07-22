/**
 * 
 */
package com.ey.advisory.controller.gstr1.sales.register;

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
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconRequestIdsDto;
import com.ey.advisory.service.gstr1.sales.register.Gstr1SalesRegisterRequestStatusDto;
import com.ey.advisory.service.gstr1.sales.register.Gstr1SalesRegisterRequestStatusService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */

@Slf4j
@RestController
public class SalesRegisterRequestStatusController {

	@Autowired
	@Qualifier("Gstr1SalesRegisterRequestStatusServiceImpl")
	private Gstr1SalesRegisterRequestStatusService reportStatusService;
	
	@PostMapping(value = "/ui/salesRegisterRequestIds", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEWB3WayRequestIds(
			@RequestBody String jsonString) {

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin SalesRegisterRequestStatusController"
						+ ".salesRegisterRequestIds ,Parsing Input request";
				LOGGER.debug(msg);
			}
			String userName = null;
			User user = SecurityContext.getUser();

			if (user != null) {
				userName = user.getUserPrincipalName();
			} else {
				userName = "SYSTEM";
			}
			Long entityId = null;

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();
			entityId = requestObject.get("entityId").getAsLong();

			List<Gstr2InitiateReconRequestIdsDto> requestIds = reportStatusService
					.getRequestIds(userName, entityId);
			
			if (LOGGER.isDebugEnabled()) {
				String msg = "Gstr1SalesRegisterRequestStatusServiceImpl"
						+ ".salesRequestIds Preparing Response Object";
				LOGGER.debug(msg);
			}
			JsonObject resp = new JsonObject();
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(requestIds);
			gstinResp.add("requestDetails", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End 8SalesRegisterRequestStatusController"
						+ ".salesRegisterRequestIds, before returning response";
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

	@PostMapping(value = "/ui/salesRegisterRequestStatusFilter", produces = {
	MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEWB3WayReportRequestStatusFilter(
			@RequestBody String jsonString) {
	
		try {
			
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Inside SalesRegisterRequestStatusController.salesRegisterRequestStatusFilter() "
								+ "method : %s {} ", jsonString);
				LOGGER.debug(msg);
			}
			
			String userName = (SecurityContext.getUser() != null
					&& SecurityContext.getUser().getUserPrincipalName() != null)
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM";
			
			JsonObject request = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			
			Gson gson = GsonUtil.newSAPGsonInstance();
			
			JsonObject json = request.get("req").getAsJsonObject();
			
			Gstr2InitiateReconReqDto reqDto = gson.fromJson(json,
					Gstr2InitiateReconReqDto.class);
			
			List<Gstr1SalesRegisterRequestStatusDto> recResponse = reportStatusService
					.getReportRequestData(reqDto,  userName);

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
