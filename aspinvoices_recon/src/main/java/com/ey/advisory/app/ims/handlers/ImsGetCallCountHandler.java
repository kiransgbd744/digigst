package com.ey.advisory.app.ims.handlers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.app.util.GstnUserRequestUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.gstnapi.GstinGetStatusService;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */

@Service("ImsGetCallCountHandler")
@Slf4j
public class ImsGetCallCountHandler {

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

	private static final List<String> GETIMS_SUPPLY_TYPES = ImmutableList.of(
			APIConstants.IMS_TYPE_B2B, APIConstants.IMS_TYPE_B2BA,
			APIConstants.IMS_TYPE_CN, APIConstants.IMS_TYPE_CNA,
			APIConstants.IMS_TYPE_DN, APIConstants.IMS_TYPE_DNA,
			APIConstants.IMS_TYPE_ECOM, APIConstants.IMS_TYPE_ECOMA);

	@Transactional(value = "clientTransactionManager")
	public ResponseEntity<String> getCallImsCount(
			List<Gstr1GetInvoicesReqDto> dtos, List<AsyncExecJob> jobList) {

		// TODO
		Map<String, Config> configMap = configManager.getConfigs("ImsConfig",
				"count.api.goodstypes", "DEFAULT");

		String goodtypeStr = configMap != null
				&& configMap.get("count.api.goodstypes") != null
						? configMap.get("count.api.goodstypes").getValue()
						: APIConstants.IMS_COUNT_TYPE_ALL_OTH;

		// need imutablelist of list of string from goodTypeStr
		List<String> GETIMS_GOODS_TYPES = ImmutableList
				.copyOf(Arrays.asList(goodtypeStr.split(",")));

		JsonArray respBody = getAllActiveGstnList(dtos);

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

			// return period - month, gstin = gstin, gstr2a section = supply
			// type
			for (Gstr1GetInvoicesReqDto dto : dtos) {
				dto.setReturnPeriod("000000");
				Integer month = GenUtil
						.convertTaxPeriodToInt(dto.getReturnPeriod());
				boolean isAllsectionsGetInProgress = true;

				String taxPeriod = String.valueOf(month);
				String gstin = dto.getGstin();
				Boolean isFailedGet = dto.getIsFailed() == null ? false
						: dto.getIsFailed();
				List<String> imsCountSupplyType = GETIMS_GOODS_TYPES;

				List<String> imsInvoicesSupplyType = dto.getGstr2aSections();

				String groupCode = TenantContext.getTenantId();
				// Get the Registration date of the GSTIN from onboarding.
				GSTNDetailEntity gstinInfo = ehcachegstin
						.getGstinInfo(groupCode, gstin);

				LocalDate regDate = gstinInfo.getRegDate();
				/*
				 * if (regDate != null) { // need to pass the current year and
				 * current month
				 * 
				 * YearMonth thisYearMonth = YearMonth.now(); // Last day of the
				 * taxperiod
				 * 
				 * LocalDate lastDayOfTaxPeriod = thisYearMonth.atEndOfMonth();
				 * // TaxPeriod is greater than GSTIN registration Date as //
				 * per // onboarding. if (lastDayOfTaxPeriod.compareTo(regDate)
				 * >= 0) { if (LOGGER.isDebugEnabled()) { LOGGER.debug(
				 * "Registation date greater than ReturnPeriod for {} ", gstin);
				 * }
				 * 
				 * } else {
				 * 
				 * if (apiResp == null) { apiResp = APIRespDto.creatErrorResp();
				 * } JsonObject json = new JsonObject(); msg =
				 * "Registation date less than ReturnPeriod, as per Onboarding";
				 * json.addProperty("gstin", gstin); json.addProperty("msg",
				 * msg);
				 * 
				 * gstinsGetCallMsg.put(
				 * gstin,"Registation date less than ReturnPeriod, as per Onboarding"
				 * ); respBody.add(json); LOGGER.error(msg + " for {} ", gstin);
				 * continue; }
				 * 
				 * } else {
				 * 
				 * if (apiResp == null) { apiResp = APIRespDto.creatErrorResp();
				 * } JsonObject json = new JsonObject(); msg =
				 * "No valid Registation date is available, as per Onboarding";
				 * json.addProperty("gstin", gstin); json.addProperty("msg",
				 * msg); respBody.add(json); LOGGER.error(msg + " for {} ",
				 * gstin); continue; }
				 */

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
							APIConstants.IMS_INVOICE.toUpperCase(),
							failedStatus);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"GETIMS Failed GET eligible sections are {} ",
								failedBatchs);
					}
				} else {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(" Get IMS Count All GET Request received");
					}
					List<String> initAndInProgressStatus = new ArrayList<>();
					initAndInProgressStatus
							.add(APIConstants.INITIATED.toUpperCase());
					initAndInProgressStatus
							.add(JobStatusConstants.IN_PROGRESS.toUpperCase());
					initAndInprogressBatchs = batchRepo.findBatchByStatus(
							dto.getGstin(), dto.getReturnPeriod(),
							APIConstants.IMS_COUNT.toUpperCase(),
							initAndInProgressStatus);
					initAndInprogressBatchs.addAll(batchRepo.findBatchByStatus(
							dto.getGstin(), dto.getReturnPeriod(),
							APIConstants.IMS_INVOICE.toUpperCase(),
							initAndInProgressStatus));

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"GETIMS All GET Not eligible sections are {} ",
								initAndInprogressBatchs);
					}
				}

				if (initAndInprogressBatchs.isEmpty()) {

					String userName = SecurityContext.getUser()
							.getUserPrincipalName();
					Long userCountRequestId = gstnUserRequestUtil
							.createGstnUserRequest(gstin, taxPeriod,
									APIConstants.GET,
									APIConstants.IMS_COUNT.toUpperCase(),
									groupCode, userName, false, false, false);
					dto.setUserRequestId(userCountRequestId);
					dto.setApiSection(APIIdentifiers.IMS_COUNT.toUpperCase());

					List<AsyncExecJob> countJobs = new ArrayList<>();

					LOGGER.debug("Dto :: ", dto.toString());

					for (String suplyTyplist : GETIMS_GOODS_TYPES) {

						if (isEligible(isFailedGet, failedBatchs,
								initAndInprogressBatchs, suplyTyplist,
								imsCountSupplyType)) {
							dto.setType(suplyTyplist.toUpperCase());
							dto = createCountBatchAndSave(groupCode, dto);
							LOGGER.debug("Dto after creating batch:: {}",
									dto.toString());
							jsonParam = gson.toJson(dto);
							jobList.add(asyncJobsService.createJobAndReturn(
									groupCode, JobConstants.GET_IMS_COUNT,
									jsonParam, userName, JobConstants.PRIORITY,
									JobConstants.PARENT_JOB_ID,
									JobConstants.SCHEDULE_AFTER_IN_MINS));
							LOGGER.debug("Dto after creating jsonparams:: {}",
									jsonParam.toString());
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug(
										"Job Created for Get IMS List as {} ",
										job);
							}
						}

					}
					Long userInvoiceRequestId = gstnUserRequestUtil
							.createGstnUserRequest(gstin, taxPeriod,
									APIConstants.GET,
									APIConstants.IMS_INVOICE.toUpperCase(),
									groupCode, userName, false, false, false);
					dto.setUserRequestId(userInvoiceRequestId);
					dto.setApiSection(APIIdentifiers.IMS_INVOICE.toUpperCase());

					for (String suplyTyplistInvoices : GETIMS_SUPPLY_TYPES) {

						if (isEligible(isFailedGet, failedBatchs,
								initAndInprogressBatchs, suplyTyplistInvoices,
								imsInvoicesSupplyType)) {
							dto.setType(suplyTyplistInvoices.toUpperCase());
							dto = createInvoiceBatchAndSave(groupCode, dto);
							LOGGER.debug("Dto after creating batch:: {}",
									dto.toString());
							jsonParam = gson.toJson(dto);
							jobList.add(asyncJobsService.createJobAndReturn(
									groupCode, JobConstants.GET_IMS_INVOICE,
									jsonParam, userName, JobConstants.PRIORITY,
									JobConstants.PARENT_JOB_ID,
									JobConstants.SCHEDULE_AFTER_IN_MINS));
							LOGGER.debug("Dto after creating jsonparams:: {}",
									jsonParam.toString());
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug(
										"Job Created for Get IMS List as {} ",
										job);
							}
						}
					}

					jobList.addAll(countJobs);
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
				// adding counts job

			}

			if (apiResp == null) {
				apiResp = APIRespDto.createSuccessResp();
			}
			resp.add("hdr", gson.toJsonTree(apiResp));
			resp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);

		} catch (Exception ex) {
			String msg = "Unexpected error while creating async jobs for IRN Invoice Get ";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private Gstr1GetInvoicesReqDto createCountBatchAndSave(String groupCode,
			Gstr1GetInvoicesReqDto dto) {
		TenantContext.setTenantId(groupCode);
		// InActiveting Previous Batch Records
		batchRepo.softlyDelete(dto.getType().toUpperCase(),
				APIConstants.IMS_COUNT, dto.getGstin(), dto.getReturnPeriod());
		GetAnx1BatchEntity batch = batchUtil.makeBatchGstr1(dto, dto.getType(),
				APIConstants.IMS_COUNT);
		// Save new Batch
		batch = batchRepo.save(batch);
		dto.setBatchId(batch.getId());
		return dto;

	}

	private Gstr1GetInvoicesReqDto createInvoiceBatchAndSave(String groupCode,
			Gstr1GetInvoicesReqDto dto) {
		TenantContext.setTenantId(groupCode);
		// InActiveting Previous Batch Records
		batchRepo.softlyDelete(dto.getType().toUpperCase(),
				APIConstants.IMS_INVOICE, dto.getGstin(),
				dto.getReturnPeriod());
		GetAnx1BatchEntity batch = batchUtil.makeBatchGstr1(dto, dto.getType(),
				APIConstants.IMS_INVOICE);
		// Save new Batch
		batch = batchRepo.save(batch);
		dto.setBatchId(batch.getId());
		return dto;

	}

	private JsonArray getAllActiveGstnList(List<Gstr1GetInvoicesReqDto> dtos) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Getting all the active GSTNs for IMS detail GET");
		}
		String msg = "";
		List<Gstr1GetInvoicesReqDto> inActiveGstinDtos = new ArrayList<>();
		JsonArray respBody = new JsonArray();
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

	// Check whether the section is eligible to create new job or not.
	private Boolean isEligible(Boolean isFailedGet, List<String> failedBatchs,
			List<String> initAndInprogressBatchs, String section,
			List<String> irnSupplyTypes) {

		if ((isFailedGet == false
				&& !initAndInprogressBatchs.contains(section.toUpperCase())
				&& irnSupplyTypes.isEmpty())
				|| (isFailedGet == false
						&& !initAndInprogressBatchs
								.contains(section.toUpperCase())
						&& irnSupplyTypes.contains(section.toUpperCase()))
				|| (isFailedGet == true
						&& failedBatchs.contains(section.toUpperCase()))) {
			return true;
		} else {
			return false;
		}
	}
}
