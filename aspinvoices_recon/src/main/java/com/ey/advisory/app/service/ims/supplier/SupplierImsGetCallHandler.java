package com.ey.advisory.app.service.ims.supplier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.app.util.GstnUserRequestUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.gstnapi.GstinGetStatusService;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Service("SupplierImsGetCallHandler")
@Slf4j
public class SupplierImsGetCallHandler {

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

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Transactional(value = "clientTransactionManager")
	public JsonArray createImsJobs(List<Gstr1GetInvoicesReqDto> dtos,
			List<AsyncExecJob> jobList, JsonArray respBody) {

		getAllActiveGstnList(dtos, respBody);

		try {
			Gson gson = GsonUtil.newSAPGsonInstance();

			String jsonParam = null;
			AsyncExecJob job = null;

			for (Gstr1GetInvoicesReqDto dto : dtos) {

				String taxPeriod = String.valueOf(dto.getReturnPeriod());
				String gstin = dto.getGstin();
				Boolean isFailedGet = dto.getIsFailed() == null ? false
						: dto.getIsFailed();

				String groupCode = TenantContext.getTenantId();

				List<String> failedBatchs = new ArrayList<>();
				List<String> initAndInprogressBatchs = new ArrayList<>();
				// If request is for fetching only for Failed Sections
				if (isFailedGet) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("GETIMS Failed GET Request received");
					}
					List<String> failedStatus = new ArrayList<>();
					failedStatus.add(APIConstants.FAILED.toUpperCase());

					failedBatchs = batchRepo.findBatchByStatus(dto.getGstin(),
							dto.getReturnPeriod(),
							APIConstants.SUPPLIER_IMS.toUpperCase(),
							failedStatus);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"GETIMS Failed GET eligible sections are {} ",
								failedBatchs);
					}
				} else {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								" Get Supllier IMS All GET Request received");
					}
					List<String> initAndInProgressStatus = new ArrayList<>();
					initAndInProgressStatus
							.add(APIConstants.INITIATED.toUpperCase());
					initAndInProgressStatus
							.add(JobStatusConstants.IN_PROGRESS.toUpperCase());

					initAndInprogressBatchs = batchRepo.findBatchByStatus(
							dto.getGstin(), dto.getReturnPeriod(),
							APIConstants.SUPPLIER_IMS.toUpperCase(),
							initAndInProgressStatus);

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"GET Supplier IMS All GET Not eligible sections are {} ",
								initAndInprogressBatchs);
					}
				}

				if (initAndInprogressBatchs.isEmpty()) {

					String userName = SecurityContext.getUser()
							.getUserPrincipalName();

					Long userImsRequestId = gstnUserRequestUtil
							.createGstnUserRequest(gstin, taxPeriod,
									APIConstants.GET,
									APIConstants.SUPPLIER_IMS.toUpperCase(),
									groupCode, userName, false, false, false);

					for (String ImsReturnType : dto.getImsReturnTypeList()) {
						if (ImsReturnType.equalsIgnoreCase("GSTR1")) {

							for (String section : dto.getGstr1Sections()) {

								dto = setJobParams(jobList, gson, job, dto,
										taxPeriod, gstin, groupCode, userName,
										userImsRequestId, ImsReturnType,
										section);

							}
						}
						if (ImsReturnType.equalsIgnoreCase("GSTR1A")) {

							for (String section : dto.getGstr1aSections()) {

								dto = setJobParams(jobList, gson, job, dto,
										taxPeriod, gstin, groupCode, userName,
										userImsRequestId, ImsReturnType,
										section);

							}

						}
					}

					if (!jobList.isEmpty())
						asyncJobsService.createJobs(jobList);

					JsonObject json = new JsonObject();
					json.addProperty("gstin", dto.getGstin());
					json.addProperty("msg",
							"Get IMS Request Initiated Successfully");
					respBody.add(json);
				} else {
					JsonObject json = new JsonObject();
					json.addProperty("gstin", dto.getGstin());
					json.addProperty("msg",
							"Get IMS is Already Inprogress, New Request can't be taken");

					respBody.add(json);
				}

			}

			/*
			 * if (apiResp == null) { apiResp = APIRespDto.createSuccessResp();
			 * } resp.add("hdr", gson.toJsonTree(apiResp)); resp.add("resp",
			 * gson.toJsonTree(respBody)); return new
			 * ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
			 */
			return respBody;

		} catch (Exception ex) {
			String msg = "Unexpected error while creating async jobs for IRN Invoice Get ";
			LOGGER.error(msg, ex);
			throw new AppException(ex);
		}
	}

	/**
	 * @param jobList
	 * @param gson
	 * @param job
	 * @param dto
	 * @param taxPeriod
	 * @param gstin
	 * @param groupCode
	 * @param userName
	 * @param userImsRequestId
	 * @param ImsReturnType
	 * @param section
	 * @return
	 */
	private Gstr1GetInvoicesReqDto setJobParams(List<AsyncExecJob> jobList,
			Gson gson, AsyncExecJob job, Gstr1GetInvoicesReqDto dto,
			String taxPeriod, String gstin, String groupCode, String userName,
			Long userImsRequestId, String ImsReturnType, String section) {
		String jsonParam;
		dto.setUserRequestId(userImsRequestId);
		dto.setApiSection(APIIdentifiers.SUPPLIER_IMS);

		dto = createCountBatchAndSave(groupCode, dto, ImsReturnType, section);
		LOGGER.debug("Dto after creating batch:: {}", dto.toString());

		JsonObject jsonParams = new JsonObject();
		jsonParams.addProperty("batchId", dto.getBatchId().toString());
		jsonParams.addProperty("gstin", gstin);
		jsonParams.addProperty("taxPeriod", taxPeriod);
		jsonParams.addProperty("section", section);
		jsonParams.addProperty("returnType", ImsReturnType);

		jsonParam = gson.toJson(jsonParams);
		jobList.add(asyncJobsService.createJobAndReturn(groupCode,
				JobConstants.GET_SUPPLIER_IMS, jsonParam, userName,
				JobConstants.PRIORITY, JobConstants.PARENT_JOB_ID,
				JobConstants.SCHEDULE_AFTER_IN_MINS));
		LOGGER.debug("Dto after creating jsonparams:: {}",
				jsonParam.toString());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Job Created for Get SUPPLIER IMS List as {} ", job);
		}
		return dto;
	}

	private Gstr1GetInvoicesReqDto createCountBatchAndSave(String groupCode,
			Gstr1GetInvoicesReqDto dto, String imsReturntype, String section) {
		TenantContext.setTenantId(groupCode);
		// InActiveting Previous Batch Records
		batchRepo.softlyDeleteSupplierIms(section.toUpperCase(),
				APIConstants.SUPPLIER_IMS, dto.getGstin(),
				dto.getReturnPeriod(), imsReturntype);
		GetAnx1BatchEntity batch = batchUtil.makeBatchImsSupplier(dto, section,
				APIConstants.SUPPLIER_IMS, imsReturntype);
		// Save new Batch
		batch = batchRepo.save(batch);
		dto.setBatchId(batch.getId());
		return dto;

	}

	private JsonArray getAllActiveGstnList(List<Gstr1GetInvoicesReqDto> dtos,
			JsonArray respBody) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Getting all the active GSTNs for IMS detail GET");
		}
		String msg = "";
		List<Gstr1GetInvoicesReqDto> inActiveGstinDtos = new ArrayList<>();
		if (dtos != null && !dtos.isEmpty()) {
			List<String> gstnList = dtos.stream().map(e -> e.getGstin())
					.collect(Collectors.toList());
			Map<String, String> gstinAuthMap = authTokenService
					.getAuthTokenStatusForGstins(gstnList);

			for (Gstr1GetInvoicesReqDto dto : dtos) {
				String gstin = dto.getGstin();
				JsonObject json = new JsonObject();
				String authStatus = gstinAuthMap.get(gstin);
				dto.getGstr2aSections().replaceAll(String::toUpperCase);
				if (!"A".equalsIgnoreCase(authStatus)) {
					inActiveGstinDtos.add(dto);
					msg = "Auth Token is Inactive, Please Activate";

					json.addProperty("gstin", gstin);
					json.addProperty("msg", msg);
					respBody.add(json);
				}
			}
			dtos.removeAll(inActiveGstinDtos);
		}
		return respBody;
	}

	/*
	 * // Check whether the section is eligible to create new job or not.
	 * private Boolean isEligible(Boolean isFailedGet, List<String>
	 * failedBatchs, List<String> initAndInprogressBatchs, String section,
	 * List<String> irnSupplyTypes) {
	 * 
	 * if ((isFailedGet == false &&
	 * !initAndInprogressBatchs.contains(section.toUpperCase()) &&
	 * irnSupplyTypes.isEmpty()) || (isFailedGet == false &&
	 * !initAndInprogressBatchs .contains(section.toUpperCase()) &&
	 * irnSupplyTypes.contains(section.toUpperCase())) || (isFailedGet == true
	 * && failedBatchs.contains(section.toUpperCase()))) { return true; } else {
	 * return false; } }
	 */
}
