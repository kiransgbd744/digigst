package com.ey.advisory.controller.gstr2;

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
import com.ey.advisory.app.asprecon.gstr2.initiaterecon.Gstr2InitiateReconEmailDto;
import com.ey.advisory.app.asprecon.gstr2.initiaterecon.Gstr2InitiateReconReportRequestStatusForGroupService;
import com.ey.advisory.app.asprecon.gstr2.initiaterecon.Gstr2InitiateReconUsernameDto;
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
public class Gstr2ReconRequestStatusGroupLevelController {

	@Autowired
	@Qualifier("Gstr2InitiateReconReportRequestStatusGroupLevelServiceImpl")
	private Gstr2InitiateReconReportRequestStatusForGroupService reportStatusService;

	@PostMapping(value = "/ui/getgstr2RequestIdsForGroup", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getgstr2RequestIdsForGroup(
			@RequestBody String jsonString) {

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin Gstr2ReconRequestStatusGroupLevelController"
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
			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();

			Gstr2InitiateReconReqDto reqDto = gson.fromJson(requestObject,
					Gstr2InitiateReconReqDto.class);

			List<Long> entityIdList = null;

			//List<Gstr2InitiateReconRequestIdsDto> status = new ArrayList<>();
			if (reqDto.getEntityIds() != null) {
				entityIdList = reqDto.getEntityIds();
			}
				//for (Long entityId : entityIdList) {
			List<Gstr2InitiateReconRequestIdsDto> status = reportStatusService
					.getRequestIds(userName,entityIdList, reqDto);
				//}
			 
			if (LOGGER.isDebugEnabled()) {
				String msg = "Gstr2InitiateReconReportRequestStatusServiceImpl"
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
				String msg = "End Gstr2ReconRequestStatusGroupLevelController"
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

	@PostMapping(value = "/ui/getgstr2UserNamesForGroup", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getgstr2UserNamesForGroup(
			@RequestBody String jsonString) {

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin Gstr2ReconRequestStatusGroupLevelController"
						+ ".getgstr2UserNames ,Parsing Input request";
				LOGGER.debug(msg);
			}

			String userName = null;
			User user = SecurityContext.getUser();
			if (user != null) {
				userName = user.getUserPrincipalName();
			} else {
				userName = "SYSTEM";
			}
			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();

			Gson gson = GsonUtil.newSAPGsonInstance();

			List<Long> entityIdList = null;

			Gstr2InitiateReconReqDto reqDto = gson.fromJson(requestObject,
					Gstr2InitiateReconReqDto.class);
		
			if (reqDto.getEntityIds() != null) {
				entityIdList = reqDto.getEntityIds();
			}
			
			//List<Gstr2InitiateReconUsernameDto> status = new ArrayList<>();
			//for (Long entityId : entityIdList) {
			List<Gstr2InitiateReconUsernameDto>	status = reportStatusService
					.getgstr2UserNames(entityIdList,
						userName);
			//}

			if (LOGGER.isDebugEnabled()) {
				String msg = "Gstr2InitiateReconReportRequestStatusServiceImpl"
						+ ".getgstr2UserNames Preparing Response Object";
				LOGGER.debug(msg);
			}
			JsonObject resp = new JsonObject();
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(status);
			gstinResp.add("requestDetails", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End Gstr2ReconRequestStatusGroupLevelController"
						+ ".getgstr2UserNames, before returning response";
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

	@PostMapping(value = "/ui/getgstr2EmailIdsForGroup", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getgstr2EmailIdsForGroup(
			@RequestBody String jsonString) {

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin Gstr2ReconRequestStatusGroupLevelController"
						+ ".getgstr2EmailIds ,Parsing Input request";
				LOGGER.debug(msg);
			}
			String userName = null;
			User user = SecurityContext.getUser();
			if (user != null) {
				userName = user.getUserPrincipalName();
			} else {
				userName = "SYSTEM";
			}

			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();

			Gson gson = GsonUtil.newSAPGsonInstance();

			List<Long> entityIdList = null;

			Gstr2InitiateReconReqDto reqDto = gson.fromJson(requestObject,
					Gstr2InitiateReconReqDto.class);
		
			if (reqDto.getEntityIds() != null) {
				entityIdList = reqDto.getEntityIds();
			}
			//List<Gstr2InitiateReconEmailDto> status = new ArrayList<>();
			//for (Long entityId : entityIdList) {

			List<Gstr2InitiateReconEmailDto> status = reportStatusService
					.getgstr2EmailIds(entityIdList,
						userName);
			//}
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
				String msg = "End Gstr2ReconRequestStatusGroupLevelController"
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

	@RequestMapping(value = "/ui/getgstr2ReportRequestStatusFilterForGroup", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getgstr2ReportRequestStatusNewForGroup(
			@RequestBody String jsonString) {

		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();

		Gson gson = GsonUtil.newSAPGsonInstance();

		JsonObject json = requestObject.get("req").getAsJsonObject();

		String msg = String
				.format("Inside Gstr2ReconRequestStatusGroupLevelController.getgstr2ReportRequestStatusNew() "
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
					.getReportRequestData(reqDto, userName);

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
