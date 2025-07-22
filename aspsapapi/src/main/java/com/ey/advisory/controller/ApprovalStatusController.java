package com.ey.advisory.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.ApprovalStatusRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mohana.Dasari
 *
 */
@RestController
@Slf4j
public class ApprovalStatusController {

	@Autowired
	private ApprovalStatusRepository approvalStatusRepository;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@PostMapping(value = "/api/setApprovalStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> setApprovalStatus(
			@RequestBody String jsonString) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Inside setApprovalStatus of erp");
		}
		String groupcode = TenantContext.getTenantId();
		JsonObject obj = new JsonParser().parse(jsonString).getAsJsonObject();
		String reqJson = obj.get("req").getAsJsonObject().toString();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		SetApprovalStatusReqDto dto = gson.fromJson(reqJson,
				SetApprovalStatusReqDto.class);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("erp response dto is {}", dto);
		}

		String gstin = dto.getGstin();
		String returnPeriod = dto.getReturnPeriod();
		String approvedUser = dto.getApprovedUser();
		// Extra charactor T is comming custom hanlding
		LocalDateTime approvedOn = DateUtil.stringToTime(
				dto.getApprovedOn().replace("T", " "), DateUtil.DATE_FORMAT1);
		/**
		 * 0-Requested for approvals 1-Approved 2-Rejected
		 */
		Integer approvalStatus = dto.getApprovalStatus();
		// if approvedOn is null then current time is inserted in the table.
		if (approvedOn == null) {
			approvedOn = LocalDateTime.now();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"approvedOn is null So current time is considered.");
			}
		}

		if (gstin != null && returnPeriod != null && approvalStatus != null
				&& approvedUser != null && approvedOn != null) {
			TenantContext.setTenantId(groupcode);
			approvalStatusRepository.updategstin(gstin, returnPeriod,
					approvalStatus, approvedUser, approvedOn);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("erp approval set response dto is {}", dto);
			}
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree("success");
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(
					"gstin, retPeriod user, date and status are "
							+ "mandatory params in request",
					HttpStatus.PARTIAL_CONTENT);
		}

	}

	@PostMapping(value = "/ui/getApprovalStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getApprovalStatus(
			@RequestBody String jsonString) {
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Inside getApprovalStatus for fetching the erp"
						+ " approval status");
			}
			String groupcode = TenantContext.getTenantId();
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.gsonInstanceWithExpose();
			ApprovalStatusReqDto dto = gson.fromJson(reqJson,
					ApprovalStatusReqDto.class);
			String gstin = dto.getGstin();
			String returnPeriod = dto.getReturnPeriod();
			JsonObject resp = new JsonObject();
			GetApprovalStatusRespDto respDto = new GetApprovalStatusRespDto();
			TenantContext.setTenantId(groupcode);
			ApprovalStatusEntity statusEntity = null;
			if (gstin != null && returnPeriod != null) {
				statusEntity = approvalStatusRepository.findByStatus(gstin,
						returnPeriod);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Most recent status of gstin {} and retPeriod {}",
							gstin, returnPeriod);
				}
			} else {
				return new ResponseEntity<>(
						"gstin and returnPeriod are mandatory params in request",
						HttpStatus.PARTIAL_CONTENT);
			}
			if (statusEntity != null) {
				int status = statusEntity.getApprovalStatus();
				String initiatedOn = statusEntity.getInitiatedOn() != null
						? String.valueOf(EYDateUtil.toISTDateTimeFromUTC(
								statusEntity.getInitiatedOn()))
						: null;
				String approvedOn = statusEntity.getApprovedOn() != null
						? String.valueOf(EYDateUtil.toISTDateTimeFromUTC(
								statusEntity.getApprovedOn()))
						: null;
				String approvedBy = statusEntity.getApprovedBy();
				String initiatedBy = statusEntity.getInitiatedBy();
				respDto.setStatus(status);
				respDto.setApprovedOn(approvedOn);
				respDto.setApprovedBy(approvedBy);
				respDto.setInitiatedOn(initiatedOn);
				respDto.setInitiatedBy(initiatedBy);
				resp.add("resp", gson.toJsonTree(respDto));
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
			String msg = "Unexpected error while saving documents";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/api/setGstr1ApprovalStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> setGstr1ApprovalStatus(
			@RequestBody String jsonString) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Inside setGstr1ApprovalStatus of erp");
		}
		String groupcode = TenantContext.getTenantId();
		JsonObject obj = new JsonParser().parse(jsonString).getAsJsonObject();
		String reqJson = obj.get("req").getAsJsonObject().toString();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		SetApprovalStatusReqDto dto = gson.fromJson(reqJson,
				SetApprovalStatusReqDto.class);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("erp response dto is {}", dto);
		}

		Long id = dto.getId();
		String gstin = dto.getGstin();
		String returnPeriod = dto.getReturnPeriod();
		String approvedUser = dto.getApprovedUser();
		// Extra charactor T is comming custom hanlding
		LocalDateTime approvedOn = DateUtil.stringToTime(
				dto.getApprovedOn().replace("T", " "), DateUtil.DATE_FORMAT1);
		/**
		 * 0-Requested for approvals 1-Approved 2-Rejected
		 */
		Integer approvalStatus = dto.getApprovalStatus();
		// if approvedOn is null then current time is inserted in the table.
		if (approvedOn == null) {
			approvedOn = LocalDateTime.now();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"approvedOn is null So current time is considered.");
			}
		}

		if (gstin != null && returnPeriod != null && approvalStatus != null
				&& approvedUser != null && approvedOn != null && id != null) {
			TenantContext.setTenantId(groupcode);
			approvedOn = EYDateUtil.toUTCDateTimeFromIST(approvedOn);
			approvalStatusRepository.updateGstinId(id, gstin, returnPeriod,
					approvalStatus, approvedUser, approvedOn);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("erp approval set response dto is {}", dto);
			}
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree("success");
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(
					"gstin, retPeriod user, date and status are "
							+ "mandatory params in request",
					HttpStatus.PARTIAL_CONTENT);
		}

	}

	@PostMapping(value = "/ui/gstr1ApprovalStatus", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> gstr1ApprovalStatus(
			@RequestBody String jsonReq) {
		JsonObject resp = new JsonObject();
		try {
			String groupCode = TenantContext.getTenantId();
			JsonObject reqObj = new JsonParser().parse(jsonReq)
					.getAsJsonObject().get("req").getAsJsonObject();
			Gson gson = GsonUtil.gsonInstanceWithExpose();
			ApprovalStatusReqDto dto = gson.fromJson(reqObj,
					ApprovalStatusReqDto.class);
			String gstin = dto.getGstin();
			String returnPeriod = dto.getReturnPeriod();
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
						? String.valueOf(EYDateUtil.toISTDateTimeFromUTC(
								statusEntity.getInitiatedOn()))
						: null;
				String approvedOn = statusEntity.getApprovedOn() != null
						? String.valueOf(EYDateUtil.toISTDateTimeFromUTC(
								statusEntity.getApprovedOn()))
						: null;
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
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/ui/requestForApproval", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> requestForApproval(
			@RequestBody String jsonString) {
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Inside requestForApproval");
			}
			String groupcode = TenantContext.getTenantId();
			User user = SecurityContext.getUser();
			String userPrincipalName = user.getUserPrincipalName();
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.gsonInstanceWithExpose();
			ApprovalStatusReqDto dto = gson.fromJson(reqJson,
					ApprovalStatusReqDto.class);
			String gstin = dto.getGstin();
			String returnPeriod = dto.getReturnPeriod();
			/**
			 * This destination names should come as per onboarding tables
			 */
			dto.setDestinationName(APIConstants.ERP_WORKFLOW);
			dto.setGroupcode(groupcode);
			String jobParam = gson.toJson(dto);
			TenantContext.setTenantId(groupcode);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("request for approval is initiated for gstin "
						+ "{} and returnPriod {}", gstin, returnPeriod);
			}
			if (gstin != null && returnPeriod != null) {
				ApprovalStatusEntity entity = new ApprovalStatusEntity();
				entity.setGstin(gstin);
				entity.setReturnPeriod(returnPeriod);
				// 0 - Initiated
				entity.setApprovalStatus(0);
				entity.setInitiatedOn(
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
				entity.setInitiatedBy(userPrincipalName);
				approvalStatusRepository.save(entity);
				return createApprovalRequestJob(jobParam);
			} else {
				return new ResponseEntity<>(
						"gstin and returnPeriod are mandatory "
								+ "params in request",
						HttpStatus.PARTIAL_CONTENT);
			}
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/ui/gstr1RequestApproval", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> requestGstr1ForApproval(
			@RequestBody String jsonString) {
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Inside requestForApproval");
			}
			String groupcode = TenantContext.getTenantId();
			User user = SecurityContext.getUser();
			String userPrincipalName = user.getUserPrincipalName();
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.gsonInstanceWithExpose();
			ApprovalStatusReqDto dto = gson.fromJson(reqJson,
					ApprovalStatusReqDto.class);
			List<String> gstins = dto.getGstins();
			String returnPeriod = dto.getReturnPeriod();
			/**
			 * This destination names should come as per onboarding tables
			 */
			dto.setReturnPeriod(returnPeriod);
			dto.setDestinationName(APIConstants.ERP_GSTR1_WORKFLOW);
			dto.setGroupcode(groupcode);

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
					statusEntitiy.setInitiatedOn(EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now()));
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
			String jobParam = gson.toJson(dto);
			TenantContext.setTenantId(groupcode);
			return createGst1ApprovalRequestJob(jobParam);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<String> createGst1ApprovalRequestJob(
			String jsonParam) {
		JsonObject resp = new JsonObject();
		try {
			String groupCode = TenantContext.getTenantId();
			AsyncExecJob job = asyncJobsService.createJob(groupCode,
					JobConstants.GSTR1_APPROVAL_REQUEST, jsonParam,
					JobConstants.SYSTEM, JobConstants.PRIORITY,
					JobConstants.PARENT_JOB_ID,
					JobConstants.SCHEDULE_AFTER_IN_MINS);

			String updatedDate = formatDateToString(job.getUpdatedDate(),
					"dd-MM-yyyy HH:mm:ss", "IST");
			String createdDate = formatDateToString(job.getCreatedDate(),
					"dd-MM-yyyy HH:mm:ss", "IST");
			Gson gson = GsonUtil.newSAPGsonInstance();
			ApprovalStatusRespDto respDto = new ApprovalStatusRespDto();
			respDto.setUpdatedDate(updatedDate);
			respDto.setCreatedDate(createdDate);
			JsonElement respStatus = gson.toJsonTree(respDto);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public static String formatDateToString(Date date, String format,
			String timeZone) {
		// null check
		if (date == null)
			return null;
		// create SimpleDateFormat object with input format
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		// default system timezone if passed null or empty
		if (timeZone == null || "".equalsIgnoreCase(timeZone.trim())) {
			timeZone = Calendar.getInstance().getTimeZone().getID();
		}
		// set timezone to SimpleDateFormat
		sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
		// return Date in required format with timezone as String
		return sdf.format(date);
	}

	public ResponseEntity<String> createApprovalRequestJob(String jsonParam) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			String groupCode = TenantContext.getTenantId();
			AsyncExecJob job = asyncJobsService.createJob(groupCode,
					JobConstants.ANX1_APPROVAL_REQUEST, jsonParam,
					JobConstants.SYSTEM, JobConstants.PRIORITY,
					JobConstants.PARENT_JOB_ID,
					JobConstants.SCHEDULE_AFTER_IN_MINS);
			String updatedDate = formatDateToString(job.getUpdatedDate(),
					"dd-MM-yyyy HH:mm:ss", "IST");
			String createdDate = formatDateToString(job.getCreatedDate(),
					"dd-MM-yyyy HH:mm:ss", "IST");
			ApprovalStatusRespDto respDto = new ApprovalStatusRespDto();
			respDto.setUpdatedDate(updatedDate);
			respDto.setCreatedDate(createdDate);
			JsonElement respStatus = gson.toJsonTree(respDto);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respStatus);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
