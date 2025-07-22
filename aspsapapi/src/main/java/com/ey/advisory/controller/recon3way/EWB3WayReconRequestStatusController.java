/**
 * 
 */
package com.ey.advisory.controller.recon3way;

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
import com.ey.advisory.app.asprecon.gstr2.initiaterecon.Gstr2InitiateReconEmailDto;
import com.ey.advisory.app.asprecon.gstr2.initiaterecon.Gstr2InitiateReconUsernameDto;
import com.ey.advisory.app.recon3way.EWB3WayInitiateReconReportRequestStatusService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconRequestIdsDto;
import com.ey.advisory.gstr2.initiaterecon.EWB3WayInitiateReconReportRequestStatusDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@RestController
public class EWB3WayReconRequestStatusController {

	@Autowired
	@Qualifier("EWB3WayReportRequestStatusServiceImpl")
	private EWB3WayInitiateReconReportRequestStatusService reportStatusService;
	
	@PostMapping(value = "/ui/getEWB3WayRequestIds", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEWB3WayRequestIds(
			@RequestBody String jsonString) {

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin EWB3WayReconRequestStatusController"
						+ ".getEWB3WayRequestIds ,Parsing Input request";
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
				String msg = "EWB3WayReportRequestStatusServiceImpl"
						+ ".getEWB3WayRequestIds Preparing Response Object";
				LOGGER.debug(msg);
			}
			JsonObject resp = new JsonObject();
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(requestIds);
			gstinResp.add("requestDetails", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End EWB3WayReconRequestStatusController"
						+ ".getEWB3WayRequestIds, before returning response";
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

	@PostMapping(value = "/ui/getEWB3WayUserNames", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEWB3WayUserNames(
			@RequestBody String jsonString) {

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin EWB3WayReconRequestStatusController"
						+ ".getEWB3WayUserNames ,Parsing Input request";
				LOGGER.debug(msg);
			}

			Long entityId = null;
			String userName = null;
			User user = SecurityContext.getUser();

			if (user != null) {
				userName = user.getUserPrincipalName();
			} else {
				userName = "SYSTEM";
			}

			JsonObject requestObject = (new JsonParser()).parse(jsonString).getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();

			entityId = requestObject.get("entityId").getAsLong();


			List<Gstr2InitiateReconUsernameDto> status = reportStatusService
					.getEWB3WayUserNames(entityId, userName);
			if (LOGGER.isDebugEnabled()) {
				String msg = "EWB3WayReportRequestStatusServiceImpl"
						+ ".getEWB3WayUserNames Preparing Response Object";
				LOGGER.debug(msg);
			}
			JsonObject resp = new JsonObject();
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(status);
			gstinResp.add("requestDetails", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End EWB3WayReconRequestStatusController"
						+ ".getEWB3WayUserNames, before returning response";
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

	@PostMapping(value = "/ui/getEWB3WayEmailIds", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEWB3WayEmailIds(
			@RequestBody String jsonString) {

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin EWB3WayReconRequestStatusController"
						+ ".getEWB3WayEmailIds ,Parsing Input request";
				LOGGER.debug(msg);
			}
		
			Long entityId = null;
			String userName = null;
			User user = SecurityContext.getUser();

			if (user != null) {
				userName = user.getUserPrincipalName();
			} else {
				userName = "SYSTEM";
			}

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();

			if (requestObject.has("entityId")) {
				entityId = requestObject.get("entityId").getAsLong();

			}

			List<Gstr2InitiateReconEmailDto> status = reportStatusService
					.getgstr2EmailIds(entityId, userName);
			if (LOGGER.isDebugEnabled()) {
				String msg = "Gstr2InitiateReconReportRequestStatusServiceImpl"
						+ ".getgstr2EmailIds Preparing Response Object";
				LOGGER.debug(msg);
			}
			JsonObject resp = new JsonObject();
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(status);
			gstinResp.add("requestDetails", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End Gstr2ReconRequestStatusController"
						+ ".getgstr2EmailIds, before returning response";
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

	@PostMapping(value = "/ui/getEWB3WayReportRequestStatusFilter", produces = {
	MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEWB3WayReportRequestStatusFilter(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject json = requestObject.get("req").getAsJsonObject();

		String msg = String
				.format("Inside EWB3WayReconRequestStatusController.getEWB3WayReportRequestStatusNew() "
						+ "method : %s {} ", jsonString);
		LOGGER.debug(msg);

		try {
			Gstr2InitiateReconReqDto reqDto = gson.fromJson(json,
					Gstr2InitiateReconReqDto.class);
			
			String userName = null;
			User user = SecurityContext.getUser();

			if (user != null) {
				userName = user.getUserPrincipalName();
			} else {
				userName = "SYSTEM";
			}

			List<EWB3WayInitiateReconReportRequestStatusDto> recResponse = reportStatusService
					.getReportRequestData(reqDto,  userName);

			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(recResponse);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
