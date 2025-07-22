package com.ey.advisory.controller.approvalworkflow;

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
import com.ey.advisory.app.approvalWorkflow.ApprovalMakerDataDto;
import com.ey.advisory.app.approvalWorkflow.ApprovalMakerRequestDto;
import com.ey.advisory.app.approvalWorkflow.ApprovalRequestService;
import com.ey.advisory.app.approvalWorkflow.ApprovalRequestSummaryDto;
import com.ey.advisory.app.approvalWorkflow.MakerRequestDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi Jain
 *
 */

@RestController
@Slf4j
public class ApprovalMakerRequestController {

	@Autowired
	@Qualifier("ApprovalRequestServiceImpl")
	private ApprovalRequestService service;

	@PostMapping(value = "/ui/getApprovalRequestData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> approvalRequestData(
			@RequestBody String jsonString) {

		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Inside ApprovalMakerRequestController.getApprovalRequest,"
							+ " received the json payload ",
					jsonString);
		}
		try {
			JsonObject reqObj = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject();

			JsonObject reqObject = reqObj.get("req").getAsJsonObject();

			ApprovalMakerRequestDto dto = gson.fromJson(reqObject,
					ApprovalMakerRequestDto.class);

			List<ApprovalMakerDataDto> respList = service
					.getMakerRequestData(dto, userName);

			JsonObject gstinResp = new JsonObject();

			if (respList == null) {
				JsonElement respBody = gson.toJsonTree(String.format(
						"Onboarding is not done for user %s ", userName));
				gstinResp.add("gstinInfo", respBody);
			} else {
				JsonElement respBody = gson.toJsonTree(respList);
				gstinResp.add("gstinInfo", respBody);
			}

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);

			if (LOGGER.isDebugEnabled()) {
				String msg = "End ApprovalMakerRequestController"
						+ ".getApprovalRequest, before returning response";
				LOGGER.debug(msg);
			}

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("Exception on getApprovalWorkflow ", e);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(e.getMessage());
			gstinResp.add("errMsg", respBody);
			resp.add("resp", gstinResp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/submitApprovalRequest", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> submitApprovalRequest(
			@RequestBody String jsonString) {

		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Inside ApprovalMakerRequestController.submitApprovalRequest,"
							+ " received the json payload ",
					jsonString);
		}
		try {
			JsonObject reqObj = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			MakerRequestDto reqDto = gson.fromJson(reqObj,
					MakerRequestDto.class);

			String res = service.submitRequestData(reqDto, userName);

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));

			if (res == null) {
				resp.add("resp", gson.toJsonTree("Please select the request"));
			} else if (res.equals("SUCCESS")) {
				resp.add("resp",
						gson.toJsonTree("Request Created Successfully"));
			} else if (res.equals("DUPLICATES")) {
				resp.add("resp",
						gson.toJsonTree("Requests are already present"));
			} else {
				resp.add("resp",
						gson.toJsonTree("Request not saved successfully"));
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			LOGGER.error("Exception on submitApprovalChekerMakerDetails ", ex);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(ex.getMessage());
			gstinResp.add("errMsg", respBody);
			resp.add("resp", gstinResp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/approvalRequestSummaryData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> approvalRequestSummaryData(
			@RequestBody String jsonString) {

		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Inside ApprovalMakerRequestController.approvalRequestSummaryData,"
							+ " received the json payload ",
					jsonString);
		}
		try {
			JsonObject reqObj = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject();

			JsonObject reqObject = reqObj.get("req").getAsJsonObject();

			ApprovalMakerRequestDto dto = gson.fromJson(reqObject,
					ApprovalMakerRequestDto.class);

			List<ApprovalRequestSummaryDto> respList = service
					.getRequestSummaryData(dto, userName);

			JsonObject gstinResp = new JsonObject();

			if (respList == null) {
				JsonElement respBody = gson.toJsonTree("No Data Found");
				gstinResp.add("requestData", respBody);
			} else {
				JsonElement respBody = gson.toJsonTree(respList);
				gstinResp.add("requestData", respBody);
			}

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);

			if (LOGGER.isDebugEnabled()) {
				String msg = "End ApprovalMakerRequestController"
						+ ".approvalRequestSummaryData, before returning response";
				LOGGER.debug(msg);
			}

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("Exception on approvalRequestSummaryData ", e);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(e.getMessage());
			gstinResp.add("errMsg", respBody);
			resp.add("resp", gstinResp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/saveAndSignData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveAndSignData(
			@RequestBody String jsonString) {

		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Inside ApprovalMakerRequestController.saveAndSignData,"
							+ " received the json payload ",
					jsonString);
		}
		try {
			JsonObject reqObj = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject();

			JsonObject reqObject = reqObj.get("req").getAsJsonObject();

			ApprovalMakerRequestDto dto = gson.fromJson(reqObject,
					ApprovalMakerRequestDto.class);

			Object[] respObj = service.getSaveAndSignData(dto, userName);

			JsonObject gstinResp = new JsonObject();
			gstinResp.addProperty("optForMakerChecker", Boolean.parseBoolean(respObj[0].toString()));
			gstinResp.addProperty("isSaveEnabled", Boolean.parseBoolean(respObj[1].toString()));
			gstinResp.addProperty("isSignEnabled", Boolean.parseBoolean(respObj[2].toString()));
			
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);

			if (LOGGER.isDebugEnabled()) {
				String msg = "End ApprovalMakerRequestController"
						+ ".saveAndSignData, before returning response";
				LOGGER.debug(msg);
			}

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("Exception on saveAndSignData ", e);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(e.getMessage());
			gstinResp.add("errMsg", respBody);
			resp.add("resp", gstinResp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}
}
