package com.ey.advisory.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.asprecon.ApprovalStatusEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.ApprovalStatusRepository;
import com.ey.advisory.app.docs.dto.GetApprovalStatusRespDto;
import com.ey.advisory.app.docs.dto.SetApprovalStatusReqDto;
import com.ey.advisory.app.docs.dto.erp.ApprovalStatusReqDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class Ret1ApprovalStatusController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Ret1ApprovalStatusController.class);

	@Autowired
	private ApprovalStatusRepository approvalStatusRepository;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@PostMapping(value = "/api/setRet1ApprovalStatus", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> setRet1ApprovalStatus(
			@RequestBody String jsonString) {

		String groupCode = TenantContext.getTenantId();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		JsonObject resp = new JsonObject();
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			SetApprovalStatusReqDto reqDto = gson.fromJson(reqJson,
					SetApprovalStatusReqDto.class);
			Long id = reqDto.getId();
			String gstin = reqDto.getGstin();
			String returnPeriod = reqDto.getReturnPeriod();
			String approvedUser = reqDto.getApprovedUser();
			LocalDateTime approvedOn = DateUtil.stringToTime(
					reqDto.getApprovedOn().replace("T", " "),
					DateUtil.DATE_FORMAT1);
			/**
			 * 0-Requested for approvals 1-Approved 2-Rejected
			 */
			Integer approvalStatus = reqDto.getApprovalStatus();

			if (approvedOn == null) {
				approvedOn = LocalDateTime.now();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"approvedOn is null So current time is considered.");
				}
			}
			if (id != null && gstin != null && returnPeriod != null
					&& approvedUser != null && approvalStatus != null) {
				TenantContext.setTenantId(groupCode);
				approvalStatusRepository.updategstin(gstin, returnPeriod,
						approvalStatus, approvedUser, approvedOn);

				JsonElement respBody = gson.toJsonTree("success");
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", respBody);
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured:{} ", e);
			JsonElement respBody = gson.toJsonTree("failed");
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.EXPECTATION_FAILED);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
	}

	@PostMapping(value = "/ui/getRet1ApprovalStatus.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getRet1ApprovalStatus(
			@RequestBody String jsonReq) {
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		JsonObject resp = new JsonObject();
		JsonElement respBody = null;
		try {
			String groupCode = TenantContext.getTenantId();
			JsonObject req = new JsonParser().parse(jsonReq).getAsJsonObject()
					.get("req").getAsJsonObject();
			ApprovalStatusReqDto reqDto = gson.fromJson(req,
					ApprovalStatusReqDto.class);
			String gstin = reqDto.getGstin();
			String returnPeriod = reqDto.getReturnPeriod();
			GetApprovalStatusRespDto respDto = new GetApprovalStatusRespDto();
			TenantContext.setTenantId(groupCode);
			ApprovalStatusEntity statusEntity = null;
			if (gstin != null && returnPeriod != null) {
				statusEntity = approvalStatusRepository.findByStatus(gstin,
						returnPeriod);
			} else {
				return new ResponseEntity<>(
						"gstin and returnPeriod are mandatory params in request",
						HttpStatus.PARTIAL_CONTENT);
			}
			if (statusEntity != null) {
				int status = statusEntity.getApprovalStatus();
				String initiatedOn = statusEntity.getInitiatedOn() != null
						? String.valueOf(statusEntity.getInitiatedOn()) : null;
				String approvedOn = statusEntity.getApprovedOn() != null
						? String.valueOf(statusEntity.getApprovedOn()) : null;
				String approvedBy = statusEntity.getApprovedBy();
				String initiatedBy = statusEntity.getInitiatedBy();
				respDto.setStatus(status);
				respDto.setApprovedOn(approvedOn);
				respDto.setApprovedBy(approvedBy);
				respDto.setInitiatedOn(initiatedOn);
				respDto.setInitiatedBy(initiatedBy);
				resp.add("resp", gson.toJsonTree(respDto));
			}

		} catch (Exception e) {
			LOGGER.error("Exception Occured:{} ", e);
			respBody = gson.toJsonTree("failed");
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.EXPECTATION_FAILED);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/ui/requestRet1ForApproval.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> requestRet1ForApproval(
			@RequestBody String jsonReq) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		JsonElement respBody = null;
		String jobParam = null;
		try {
			String groupCode = TenantContext.getTenantId();
			User user = SecurityContext.getUser();
			String userPrincipalName = user.getUserPrincipalName();
			JsonObject obj = new JsonParser().parse(jsonReq).getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			ApprovalStatusReqDto dto = gson.fromJson(reqJson,
					ApprovalStatusReqDto.class);
			List<String> gstins = dto.getGstins();
			String returnPeriod = dto.getReturnPeriod();
			/**
			 * This destination names should come as per onboarding tables
			 */
			dto.setReturnPeriod(returnPeriod);
			dto.setDestinationName(APIConstants.ERP_RET1_APPROVAL);
			dto.setGroupcode(groupCode);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"request for approval is initiated for gstin "
								+ "{} and returnPriod {}",
						gstins, returnPeriod);
			}
			Map<Long, String> gstinsId = new HashMap<>();
			if (gstins != null && returnPeriod != null) {

				for (String gstin : gstins) {
					ApprovalStatusEntity statusEntitiy = new ApprovalStatusEntity();
					statusEntitiy.setGstin(gstin);
					statusEntitiy.setReturnPeriod(returnPeriod);
					// 0 - Initiated
					statusEntitiy.setApprovalStatus(0);
					statusEntitiy.setInitiatedOn(LocalDateTime.now());
					statusEntitiy.setInitiatedBy(userPrincipalName);
					ApprovalStatusEntity entity = approvalStatusRepository
							.save(statusEntitiy);
					gstinsId.put(entity.getId(), entity.getGstin());
				}
			} else {
				return new ResponseEntity<>(
						"gstin and returnPeriod are mandatory "
								+ "params in request",
						HttpStatus.PARTIAL_CONTENT);
			}
			dto.setGstinIds(gstinsId);
			jobParam = gson.toJson(dto);
			TenantContext.setTenantId(groupCode);

		} catch (Exception e) {
			LOGGER.error("Exception Occured:{} ", e);
			respBody = gson.toJsonTree("failed");
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.EXPECTATION_FAILED);
		}
		return createRet1ApprovalRequestJob(jobParam);
	}

	public ResponseEntity<String> createRet1ApprovalRequestJob(
			String jsonParam) {
		try {
			String groupCode = TenantContext.getTenantId();
			AsyncExecJob job = asyncJobsService.createJob(groupCode,
					JobConstants.RET1_APPROVAL_REQUEST, jsonParam,
					JobConstants.SYSTEM, JobConstants.PRIORITY,
					JobConstants.PARENT_JOB_ID,
					JobConstants.SCHEDULE_AFTER_IN_MINS);
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(job);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents";
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
