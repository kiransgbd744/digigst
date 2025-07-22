package com.ey.advisory.controller.gstr6;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.asprecon.ApprovalStatusEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.ApprovalStatusRepository;
import com.ey.advisory.app.docs.dto.SetApprovalStatusReqDto;
import com.ey.advisory.app.docs.dto.erp.ApprovalStatusReqDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
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
import com.google.gson.JsonParser;

public class Gstr6ApprovalStatusController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6ApprovalStatusController.class);

	@Autowired
	@Qualifier("ApprovalStatusRepository")
	private ApprovalStatusRepository approvalStatusRepository;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@PostMapping(value = "/ui/requestGstr6Approval", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> requestGstr6Approval(
			@RequestBody String jsonReq) {
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		String jobParam = null;
		try {
			JsonObject reqJson = new JsonParser().parse(jsonReq)
					.getAsJsonObject().get("req").getAsJsonObject();
			String groupCode = TenantContext.getTenantId();
			User user = SecurityContext.getUser();
			String userPrincipalName = user.getUserPrincipalName();
			ApprovalStatusReqDto dto = gson.fromJson(reqJson,
					ApprovalStatusReqDto.class);
			List<String> gstins = dto.getGstins();
			String returnPeriod = dto.getReturnPeriod();
			dto.setDestinationName(APIConstants.ERP_GSTR6_WORKFLOW);
			dto.setGroupcode(groupCode);
			Map<Long, String> gstinsId = new HashMap<>();
			if (gstins != null && !gstins.isEmpty() && returnPeriod != null) {
				for (String gstin : gstins) {
					ApprovalStatusEntity entity = new ApprovalStatusEntity();
					entity.setGstin(gstin);
					entity.setReturnPeriod(returnPeriod);

					// 0 - Initiated
					entity.setApprovalStatus(0);

					entity.setInitiatedOn(EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now()));
					entity.setInitiatedBy(userPrincipalName);
					ApprovalStatusEntity entityStatus = approvalStatusRepository
							.save(entity);
					gstinsId.put(entityStatus.getId(), entityStatus.getGstin());
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
			LOGGER.error("Exception Occred:{}", e);
		}
		return createGst1ApprovalRequestJob(jobParam);
	}

	public ResponseEntity<String> createGst1ApprovalRequestJob(
			String jobParam) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			String groupCode = TenantContext.getTenantId();
			AsyncExecJob job = asyncJobsService.createJob(groupCode,
					JobConstants.GSTR6_APPROVAL_REQUEST, jobParam,
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
		} catch (Exception ex) {
			LOGGER.error("Unexpected error:{}", ex);
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
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

	@PostMapping(value = "setGstr6ApprovalStatus", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> setGstr6ApprovalStatus(
			@RequestBody String jsonReq) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {
			String groupCode = TenantContext.getTenantId();
			JsonObject reqJson = new JsonParser().parse(jsonReq)
					.getAsJsonObject().get("req").getAsJsonObject();
			SetApprovalStatusReqDto dto = gson.fromJson(reqJson,
					SetApprovalStatusReqDto.class);
			Long id = dto.getId();
			String gstin = dto.getGstin();
			String retPeriod = dto.getReturnPeriod();
			String approvedBy = dto.getApprovedUser();
			LocalDateTime approvedOn = DateUtil.stringToTime(
					dto.getApprovedOn().replace("T", " "),
					DateUtil.DATE_FORMAT1);
			/**
			 * 0-Requested for approvals 1-Approved 2-Rejected
			 */
			Integer approvedStatus = dto.getApprovalStatus();
			// if approvedOn is null then current time is inserted in the table.
			if (approvedOn == null) {
				approvedOn = LocalDateTime.now();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"approvedOn is null So current time is considered.");
				}
			}

			if (gstin != null && retPeriod != null && approvedOn != null
					&& approvedBy != null && id != null) {
				TenantContext.setTenantId(groupCode);
				approvedOn = EYDateUtil.toUTCDateTimeFromIST(approvedOn);
				approvalStatusRepository.updateGstinId(id, gstin, retPeriod,
						approvedStatus, approvedBy, approvedOn);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("erp approval set response dto is {}", dto);
				}
				JsonElement respBody = gson.toJsonTree("success");
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", respBody);
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occred:{} ", e);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}
}
