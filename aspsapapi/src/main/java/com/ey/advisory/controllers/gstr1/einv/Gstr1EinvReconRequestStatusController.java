package com.ey.advisory.controllers.gstr1.einv;

import java.lang.reflect.Type;
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

import com.ey.advisory.app.gstr1.einv.Gstr1EinvInitiateReconReportRequestStatusDto;
import com.ey.advisory.app.gstr1.einv.Gstr1EinvInitiateReconReptReqStatusService;
import com.ey.advisory.app.gstr1.einv.Gstr1EinvoiceAndReconStatusDto;
import com.ey.advisory.app.gstr1.einv.Gstr1PrVsSubmReconReportRequestStatusDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Rajesh N K
 *
 */
@Slf4j
@RestController
public class Gstr1EinvReconRequestStatusController {

	@Autowired
	@Qualifier("Gstr1EinvInitiateReconReptReqStatusServiceImpl")
	private Gstr1EinvInitiateReconReptReqStatusService reportStatus;

	@PostMapping(value = "/ui/getGstr1EinvReportRequestStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getInitiateReconReportRequestStatus(
			@RequestBody String jsonString) {

		try {

			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin Gstr1EinvReconRequestStatusController"
						+ ".getInitiateReconReportRequestStatus ,Parsing Input request";
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
			
			List<Gstr1EinvInitiateReconReportRequestStatusDto> status = reportStatus
					.getReportRequestStatus(reqDto, userName);

			JsonObject resp = new JsonObject();
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(status);
			gstinResp.add("requestDetails", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End Gstr1EinvReconRequestStatusController"
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

	@PostMapping(value = "/ui/getGstr1EinvStatusList", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEinvoiceAndReconStatus(
			@RequestBody String jsonString) {

		try {

			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin Gstr1EinvReconRequestStatusController"
						+ ".getEinovoiceAndReconStatus ,Parsing Input request";
				LOGGER.debug(msg);
			}

			JsonObject request = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			Gson gson = GsonUtil.newSAPGsonInstance();
			
			JsonArray gstnsList = new JsonArray();
			
			String taxPeriod = null;

			if (request.has("gstins")) {			
				gstnsList = request.getAsJsonArray("gstins");
				
			}
			
			Gson googleJson = new Gson();

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();
			ArrayList<String> gstinlist = googleJson.fromJson(gstnsList, listType);
			
			if (request.has("taxPeriod")) {
				 taxPeriod = request.get("taxPeriod").getAsString();
			}
		
			List<Gstr1EinvoiceAndReconStatusDto> status = reportStatus
					.getReportRequestStatus(gstinlist, taxPeriod);

			if (LOGGER.isDebugEnabled()) {
				String msg = "Gstr1EinvInitiateReconReptReqStatusServiceImpl"
						+ ".getReportRequestStatus Preparing Response Object";
				LOGGER.debug(msg);
			}
			JsonObject resp = new JsonObject();
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(status);
			gstinResp.add("Gstr1EinvStatus", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End Gstr1EinvReconRequestStatusController"
						+ ".getEinvoiceAndReconStatus, before returning response";
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
	
	
	@PostMapping(value = "/ui/getGstr1PrVsSubmReportStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr1PrVsSubmReportStatus(
			@RequestBody String jsonString){

		try {

			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin getGstr1PrVsSubmReportStatusController"
						+ ".getInitiateReconReportRequestStatus ,Parsing Input request";
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
			
			List<Gstr1PrVsSubmReconReportRequestStatusDto> status = reportStatus
					.getPrSubReportRequestStatus(reqDto, userName);

			JsonObject resp = new JsonObject();
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(status);
			gstinResp.add("requestDetails", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End Gstr1EinvReconRequestStatusController"
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
