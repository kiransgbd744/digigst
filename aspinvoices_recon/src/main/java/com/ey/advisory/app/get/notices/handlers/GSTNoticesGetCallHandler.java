/**
 * 
 */
package com.ey.advisory.app.get.notices.handlers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.app.util.GstnUserRequestUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstnapi.GstinGetStatusService;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */

@Service("GSTNoticesGetCallHandler")
@Slf4j
public class GSTNoticesGetCallHandler {

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	@Autowired
	private GstnUserRequestUtil gstnUserRequestUtil;

	@Autowired
	@Qualifier("GstinGetStatusServiceImpl")
	private GstinGetStatusService gstinGetStatusService;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gSTNDetailRepository;

	@Transactional(value = "clientTransactionManager")
	public ResponseEntity<String> getGstNotices(List<String> gstins,
			List<AsyncExecJob> jobList) {

		// Dto will contain only active GSTIN's and
		// respBody contains invalid GSTIN's list with messages
		JsonArray respBody = getAllActiveGstnList(gstins);

		JsonObject resp = new JsonObject();
		try {
			Gson gson = GsonUtil.newSAPGsonInstance();
			APIRespDto apiResp = null;

			if (respBody != null && !respBody.equals(new JsonArray())) {
				apiResp = APIRespDto.creatErrorResp();
			}
			String msg = "";
			String jsonParam = null;
			AsyncExecJob job = null;

			for (String gstin : gstins) {
				GstNoticesReqDto dto = new GstNoticesReqDto();
				dto.setGstin(gstin);
				boolean isAllsectionsGetInProgress = true;
				dto.setType("GST_NOTICE");
				String groupCode = TenantContext.getTenantId();
				List<String> initAndInprogressBatchs = new ArrayList<>();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(" GST NOTICE GET Request received");
				}
				List<String> initAndInProgressStatus = new ArrayList<>();
				initAndInProgressStatus
						.add(APIConstants.INITIATED.toUpperCase());
				initAndInProgressStatus
						.add(JobStatusConstants.IN_PROGRESS.toUpperCase());
				initAndInprogressBatchs = batchRepo.findBatchByStatus(gstin,
						"000000", APIConstants.GST_NOTICE_DTL.toUpperCase(),
						initAndInProgressStatus);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("GST NOTICE GET Not eligible sections are {} ",
							initAndInprogressBatchs);
				}
				String userName = SecurityContext.getUser()
						.getUserPrincipalName();
				Long userRequestId = gstnUserRequestUtil.createGstnUserRequest(
						gstin, "000000", APIConstants.GET,
						APIConstants.GST_NOTICE_DTL.toUpperCase(), groupCode,
						userName, false, false, false);

				dto.setUserRequestId(userRequestId);
				dto.setApiSection(APIIdentifiers.GST_NOTICE_DTL.toUpperCase());

				LOGGER.debug("Dto :: ", dto.toString());

				if (initAndInprogressBatchs.isEmpty()) {
					isAllsectionsGetInProgress = false;
					// in dto supply type is DEXP
					dto = createBatchAndSave(groupCode, dto);
					LOGGER.debug("Dto after creating batch:: {}",
							dto.toString());
					jsonParam = gson.toJson(dto);
					jobList.add(asyncJobsService.createJobAndReturn(groupCode,
							JobConstants.GST_NOTICE_DTL, jsonParam, userName,
							JobConstants.PRIORITY, JobConstants.PARENT_JOB_ID,
							JobConstants.SCHEDULE_AFTER_IN_MINS));
					LOGGER.debug("Dto after creating jsonparams:: {}",
							jsonParam.toString());
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Job Created for GST NOTICE List as {} ",
								job);
					}
				}

				if (isAllsectionsGetInProgress) {
					JsonObject json = new JsonObject();
					json.addProperty("gstin", dto.getGstin());
					json.addProperty("msg",
							"Gst Notice Get Call is Already Inprogress, New Request can't be taken");

					respBody.add(json);
				} else {
					JsonObject json = new JsonObject();
					json.addProperty("gstin", dto.getGstin());
					json.addProperty("msg",
							"Gst Notice Get Call Request Initiated Successfully");
					respBody.add(json);
				}
			}
			if (apiResp == null) {
				apiResp = APIRespDto.createSuccessResp();
			}
			resp.add("hdr", gson.toJsonTree(apiResp));
			resp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			String msg = "Unexpected error while creating async jobs for GET GST NOTICE ";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private GstNoticesReqDto createBatchAndSave(String groupCode,
			GstNoticesReqDto dto) {
		TenantContext.setTenantId(groupCode);
		// InActiveting Previous Batch Records
		batchRepo.softlyDelete(dto.getType(), APIConstants.GST_NOTICE_DTL,
				dto.getGstin(), "000000");

		GetAnx1BatchEntity batch = makeBatchGstNotices(dto, dto.getType());
		// Save new Batch
		batch = batchRepo.save(batch);
		dto.setBatchId(batch.getId());
		return dto;

	}

	private JsonArray getAllActiveGstnList(List<String> gstnList) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Getting all the active GSTNs for GST NOTICE");
		}
		String msg = "";
		List<String> inActiveGstin = new ArrayList<>();
		JsonArray respBody = new JsonArray();
		if (gstnList != null && !gstnList.isEmpty()) {
			Map<String, String> gstinAuthMap = authTokenService
					.getAuthTokenStatusForGstins(gstnList);

			for (String gstin : gstnList) {
				JsonObject json = new JsonObject();
				String authStatus = gstinAuthMap.get(gstin);
				if (!"A".equalsIgnoreCase(authStatus)) {
					inActiveGstin.add(gstin);
					msg = "Auth Token is Inactive, Please Activate";
					json.addProperty("gstin", gstin);
					json.addProperty("msg", msg);
					respBody.add(json);
				}
			}
			gstnList.removeAll(inActiveGstin);
		}
		return respBody;
	}

	public GetAnx1BatchEntity makeBatchGstNotices(GstNoticesReqDto dto,
			String type) {
		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		GetAnx1BatchEntity batch = new GetAnx1BatchEntity();
		batch.setUserRequestId(dto.getUserRequestId());
		batch.setApiSection(APIConstants.GST_NOTICE_DTL.toUpperCase());
		batch.setSgstin(dto.getGstin());
		batch.setTaxPeriod("000000");
		batch.setAction("N");

		batch.setDerTaxPeriod(000000);
		batch.setType(type != null ? type.toUpperCase() : null);
		batch.setCreatedBy(APIConstants.SYSTEM.toUpperCase());
		batch.setCreatedOn(now);
		batch.setDelete(false);
		batch.setStartTime(now);
		batch.setStatus(APIConstants.INITIATED);
		return batch;
	}

}
