package com.ey.advisory.controller.approvalworkflow;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.approvalWorkflow.ApprovalCheckerActionRequestDto;
import com.ey.advisory.app.approvalWorkflow.ApprovalCheckerGstinsDto;
import com.ey.advisory.app.approvalWorkflow.ApprovalCheckerRequestDto;
import com.ey.advisory.app.approvalWorkflow.ApprovalCheckerRequestService;
import com.ey.advisory.app.approvalWorkflow.ApprovalCheckerStatusSummaryDto;
import com.ey.advisory.app.approvalWorkflow.ApprovalRequestSummaryDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ApprovalCheckerRequestController {

	@Autowired
	@Qualifier("ApprovalCheckerRequestServiceImpl")
	private ApprovalCheckerRequestService checkerService;

	@PostMapping(value = "/ui/approvalCheckerRequestData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> approvalCheckerRequestData(
			@RequestBody String jsonString) {

		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Inside ApprovalCheckerRequestController.approvalCheckerRequestData,"
							+ " received the json payload ",
					jsonString);
		}
		try {
			JsonObject reqObj = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject();

			JsonObject reqObject = reqObj.get("req").getAsJsonObject();

			int totalApproved = 0;
			int totalRequests = 0;
			int totalRejected = 0;
			int totalPending = 0;

			ApprovalCheckerRequestDto dto = gson.fromJson(reqObject,
					ApprovalCheckerRequestDto.class);

			List<ApprovalCheckerStatusSummaryDto> respList = checkerService
					.getCheckerRequestData(dto, userName);

			JsonObject gstinResp = new JsonObject();

			if (respList == null) {
				JsonElement respBody = gson.toJsonTree(String
						.format("No Requests found for user -> %s ", userName));
				gstinResp.add("requestInfo", respBody);
			} else {
				respList.sort(
						Comparator.comparing(ApprovalCheckerStatusSummaryDto::getRequestId));
				
				totalRequests = respList.size();

				Map<String, List<ApprovalCheckerStatusSummaryDto>> reqStatusList = respList
						.stream().collect(Collectors.groupingBy(
								ApprovalCheckerStatusSummaryDto::getStatus));
				if(LOGGER.isDebugEnabled())
				{
				LOGGER.debug("Request Status Map based on filter -> ",reqStatusList);	
				}
				
				for (Map.Entry<String, List<ApprovalCheckerStatusSummaryDto>> entry : reqStatusList
						.entrySet()) {
					if (entry.getKey().equalsIgnoreCase("Approved")) {
						totalApproved += entry.getValue().size();
					} else if (entry.getKey().equalsIgnoreCase("Rejected")) {
						totalRejected += entry.getValue().size();
					} else if (entry.getKey().equalsIgnoreCase("Pending")
							|| entry.getKey()
									.equalsIgnoreCase("Pending (reversed)")) {
						totalPending += entry.getValue().size();

					}

				}
				Collections.reverse(respList);
				JsonElement respBody = gson.toJsonTree(respList);
				gstinResp.add("requestInfo", respBody);
			}

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.addProperty("totalRequests", totalRequests);
			resp.addProperty("totalApproved", totalApproved);
			resp.addProperty("totalRejected", totalRejected);
			resp.addProperty("totalPending", totalPending);

			resp.add("resp", gstinResp);

			if (LOGGER.isDebugEnabled()) {
				String msg = "End ApprovalCheckerRequestController"
						+ ".approvalCheckerRequestData, before returning response";
				LOGGER.debug(msg);
			}

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("Exception on approvalCheckerRequestData ", e);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(e.getMessage());
			gstinResp.add("errMsg", respBody);
			resp.add("resp", gstinResp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/getCheckerGstins", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getCheckerGstins(
			@RequestBody String jsonString) {

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Inside ApprovalCheckerRequestController.checkerGstins");
		}

		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {
			JsonObject reqObj = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject();

			JsonObject reqObject = reqObj.get("req").getAsJsonObject();

			Long entityId = reqObject.get("entityId").getAsLong();

			List<ApprovalCheckerGstinsDto> gstinList = checkerService
					.getCheckerGstinsData(userName, entityId);

			JsonObject gstinResp = new JsonObject();

			JsonElement respBody = gson.toJsonTree(gstinList);
			gstinResp.add("checkerGstins", respBody);

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);

			if (LOGGER.isDebugEnabled()) {
				String msg = "End ApprovalCheckerRequestController"
						+ ".getCheckerGstins, before returning response";
				LOGGER.debug(msg);
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

	/*@PostMapping(value = "/ui/getCheckerTabSummary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getCheckerTabSummary(
			@RequestBody String jsonString) {

		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Inside ApprovalCheckerRequestController.getCheckerTabSummary");
		}
		try {
			JsonObject reqObj = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonObject reqObject = reqObj.get("req").getAsJsonObject();

			Long entityId = reqObject.get("entityId").getAsLong();

			ApprovalCheckerTabSummaryDto requestList = service
					.findRequestTabCounts(userName, entityId);

			JsonElement respBody = gson.toJsonTree(requestList);
			JsonObject gstinResp = new JsonObject();
			gstinResp.add("tabSummary", respBody);

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);

			if (LOGGER.isDebugEnabled()) {
				String msg = "End ApprovalCheckerRequestController"
						+ ".getCheckerTabSummary, before returning response";
				LOGGER.debug(msg, resp);
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
*/
	@PostMapping(value = "ui/submitAndRevertCheckerResponse", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> submitAndRevertCheckerResponse(
			@RequestBody String jsonString) {
		JsonObject resp = new JsonObject();
		Gson gson = new Gson();
		try {

			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Start ApprovalCheckerRequestController"
						+ ".submitAndRevertCheckerResponse, for user -> {}";
				LOGGER.debug(msg, userName);
			}

			JsonObject jsonObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject reqData = jsonObject.getAsJsonObject("req");

			ApprovalCheckerActionRequestDto reqDto = gson.fromJson(reqData,
					ApprovalCheckerActionRequestDto.class);

			String res = checkerService.submitandRevertRequestAction(userName, reqDto);

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			
			JsonObject gstinResp = new JsonObject();

			if (res.equalsIgnoreCase("SUCCESS")) {
				JsonElement respBody = gson.toJsonTree("Action Saved Successfully");
				gstinResp.add("actionInfo", respBody);
				resp.add("resp", gstinResp);

			}
			if (LOGGER.isDebugEnabled()) {
				String msg = "End ApprovalCheckerRequestController"
						+ ".submitAndRevertCheckerResponse, before returning response";
				LOGGER.debug(msg, resp);
			}

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception in submitAndRevertCheckerResponse");
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(ex.getMessage());
			gstinResp.add("errMsg", respBody);
			resp.add("resp", gstinResp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}
}
