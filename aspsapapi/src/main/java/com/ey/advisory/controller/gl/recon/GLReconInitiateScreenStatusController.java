package com.ey.advisory.controller.gl.recon;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.anx2.initiaterecon.Gstr2InitiateReconReportRequestStatusDto;
import com.ey.advisory.app.glrecon.GLReconInitiateReconReportRequestStatusService;
import com.ey.advisory.app.glrecon.GlViewSummaryResponseDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconRequestIdsDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;



@Slf4j
@RestController
public class GLReconInitiateScreenStatusController {

	
	@Autowired
	@Qualifier("GlReconInitiateReconReportRequestStatusServiceImpl")
	private GLReconInitiateReconReportRequestStatusService reportStatusService;


	@PostMapping(value = "/ui/getGlReconRequestIds", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getgstr2RequestIds(
			@RequestBody String jsonString) {

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin GLReconInitiateScreenStatusController"
						+ ".getgstr2RequestId ,Parsing Input request";
				LOGGER.debug(msg);
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			String userName = null;
			User user = SecurityContext.getUser();

			if (user != null) {
				userName = user.getUserPrincipalName();
			} else {
				userName = "SYSTEM";
			}
			String entityId = null;

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
	

			if (requestObject.has("entityId")) {
				entityId = requestObject.get("entityId").getAsString();

			}

			Gstr2InitiateReconReqDto reqDto = gson.fromJson(requestObject,
					Gstr2InitiateReconReqDto.class);
			
			List<Gstr2InitiateReconRequestIdsDto> status = reportStatusService
					.getRequestIds(userName, Long.valueOf(entityId), reqDto);
			if (LOGGER.isDebugEnabled()) {
				String msg = "GLReconInitiateScreenStatusController"
						+ ".getgstr2RequestIds Preparing Response Object";
				LOGGER.debug(msg);
			}
			JsonObject resp = new JsonObject();
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(status);
			gstinResp.add("requestDetails", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End GLReconInitiateScreenStatusController"
						+ ".getgstr2RequestIds, before returning response";
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
	
		@RequestMapping(value = "/ui/getGlReconReportRequestStatusFilter", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getgstr2ReportRequestStatusNew(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		Gson gson = GsonUtil.newSAPGsonInstance();

		JsonObject json = requestObject.get("req").getAsJsonObject();

		String msg = String
				.format("Inside GLReconInitiateScreenStatusController.getgstr2ReportRequestStatusNew() "
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

			List<Gstr2InitiateReconReportRequestStatusDto> recResponse = reportStatusService
					.getReportRequestData(reqDto,  userName);
			
			Collections.sort(recResponse, Comparator.comparing(Gstr2InitiateReconReportRequestStatusDto::getRequestId).reversed());
			
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
		
		@PostMapping(value = "/ui/getGlReconViewSummary", produces = {
				MediaType.APPLICATION_JSON_VALUE })
		public ResponseEntity<String> getGlReconViewSumry(
				@RequestBody String jsonString) {

			try {
				if (LOGGER.isDebugEnabled()) {
					String msg = "Begin GLReconInitiateScreenStatusController"
							+ ".getGlReconViewSumry ,Parsing Input request";
					LOGGER.debug(msg);
				}
				Gson gson = GsonUtil.newSAPGsonInstance();
				String userName = null;
				User user = SecurityContext.getUser();

				if (user != null) {
					userName = user.getUserPrincipalName();
				} else {
					userName = "SYSTEM";
				}
				String entityId = null;

				JsonObject requestObject = (new JsonParser()).parse(jsonString)
						.getAsJsonObject();
		

				if (requestObject.has("requestId")) {
					entityId = requestObject.get("requestId").getAsString();

				}

				/*Gstr2InitiateReconReqDto reqDto = gson.fromJson(requestObject,
						Gstr2InitiateReconReqDto.class);*/
				
				/*List<Gstr2InitiateReconRequestIdsDto> status = reportStatusService
						.getRequestIds(userName, Long.valueOf(entityId), reqDto);*/
				if (LOGGER.isDebugEnabled()) {
					String msg = "GLReconInitiateScreenStatusController"
							+ ".getGlReconViewSummary Preparing Response Object";
					LOGGER.debug(msg);
				}
				JsonObject resp = new JsonObject();
				JsonObject gstinResp = new JsonObject();
				JsonElement respBody = gson.toJsonTree(new GlViewSummaryResponseDto());
				gstinResp.add("summaryDetails", respBody);
				resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gstinResp);
				if (LOGGER.isDebugEnabled()) {
					String msg = "End GLReconInitiateScreenStatusController"
							+ ".getgstr2RequestIds, before returning response";
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
