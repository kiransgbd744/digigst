package com.ey.advisory.app.data.services.Gstr1A;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9ComputeReadyStatusFYEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9PeriodWiseEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9ComputeReadyStatusFYRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9PeriodWiseRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.app.util.GstnUserRequestUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.GstnReturnFilingStatus;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.common.service.GstnApi;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.core.dto.ReturnFilingGstnResponseDto;
import com.ey.advisory.gstnapi.GstinGetStatusService;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */

@Service("Gstr1AGetCallHandler")
@Slf4j
public class Gstr1AGetCallHandler {

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
	private GstnReturnFilingStatus returnFilingStatus;

	@Autowired
	@Qualifier("GstinGetStatusServiceImpl")
	private GstinGetStatusService gstinGetStatusService;

	@Autowired
	private GstnApi gstnApi;

	@Autowired
	Gstr9PeriodWiseRepository gstr9PeriodWiseRepo;

	@Autowired
	Gstr9ComputeReadyStatusFYRepository gstr9FyRepo;

	@Transactional(value = "clientTransactionManager")
	public ResponseEntity<String> createGstr1Gstn(
			List<Gstr1GetInvoicesReqDto> dtos, List<AsyncExecJob> jobList) {

		JsonObject resp = new JsonObject();

		try {
			Gson gson = GsonUtil.newSAPGsonInstance();
			// Dto will contain only active GSTIN's and
			// respBody contains invalid GSTIN's list with messages
			JsonArray respBody = getAllActiveGstnList(dtos);
			APIRespDto apiResp = null;

			if (respBody != null && !respBody.equals(new JsonArray())) {
				apiResp = APIRespDto.creatErrorResp();
			}
			String msg = "";
			String jsonParam = null;
			AsyncExecJob job = null;
			for (Gstr1GetInvoicesReqDto dto : dtos) {

				APIParam param1 = new APIParam("gstin", dto.getGstin());

				/**
				 * Extra Logic to support fromTime/toTime
				 */
				Integer fromPeriod = !StringUtils.isEmpty(dto.getFromPeriod())
						? GenUtil.convertTaxPeriodToInt(dto.getFromPeriod())
						: GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod());
				Integer toPeriod = !StringUtils.isEmpty(dto.getToPeriod())
						? GenUtil.convertTaxPeriodToInt(dto.getToPeriod())
						: GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod());
				if (fromPeriod == null || toPeriod == null) {
					apiResp = APIRespDto.creatErrorResp();
					JsonObject json = new JsonObject();
					msg = "ReturnPeriod can not be null";
					json.addProperty("gstin", dto.getGstin());
					json.addProperty("msg", msg);
					respBody.add(json);
					LOGGER.error(msg + " for {} ", dto.getGstin());
					break;
				}
				boolean isAllsectionsGetInProgress = true;
				for (; fromPeriod <= toPeriod; fromPeriod++) {
					String taxPeriod = GenUtil
							.convertDerivedTaxPeriodToTaxPeriod(fromPeriod);
					if ("13".equals(taxPeriod.substring(0, 2))) {
						fromPeriod = fromPeriod + 88;
						taxPeriod = GenUtil
								.convertDerivedTaxPeriodToTaxPeriod(fromPeriod);
					}
					dto.setReturnPeriod(taxPeriod);
					APIParam param2 = new APIParam("ret_period", taxPeriod);
					String gstin = dto.getGstin();
					Boolean isFailedGet = dto.getIsFailed() == null ? false
							: dto.getIsFailed();
					List<String> gstr1Sections = dto.getGstr1Sections();
					String groupCode = TenantContext.getTenantId();
					TenantContext.setTenantId(groupCode);
					// Get the Registration date of the GSTIN from onboarding.
					GSTNDetailEntity gstinInfo = ehcachegstin
							.getGstinInfo(groupCode, gstin);

					// LocalDate gstinRegDate =
					// gstinRepo.findRegistraionDate(gstin);
					LocalDate regDate = gstinInfo.getRegDate();
					if (regDate != null) {
						// Creates a YearMonth object
						YearMonth thisYearMonth = YearMonth.of(
								Integer.valueOf(taxPeriod.substring(2, 6)),
								Integer.valueOf(taxPeriod.substring(0, 2)));
						// Last day of the taxperiod
						LocalDate lastDayOfTaxPeriod = thisYearMonth
								.atEndOfMonth();
						// TaxPeriod is greater than GSTIN registration Date as
						// per
						// onboarding.
						if (lastDayOfTaxPeriod.compareTo(regDate) >= 0) {
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug(
										"Registation date greater than ReturnPeriod for {} ",
										gstin);
							}

						} else {

							if (apiResp == null) {
								apiResp = APIRespDto.creatErrorResp();
							}
							JsonObject json = new JsonObject();
							msg = "Registation date less than ReturnPeriod, as per Onboarding";
							json.addProperty("gstin", gstin);
							json.addProperty("msg", msg);
							respBody.add(json);
							LOGGER.error(msg + " for {} ", gstin);
							continue;
						}

					} else {

						if (apiResp == null) {
							apiResp = APIRespDto.creatErrorResp();
						}
						JsonObject json = new JsonObject();
						msg = "No valid Registation date is available, as per Onboarding";
						json.addProperty("gstin", gstin);
						json.addProperty("msg", msg);
						respBody.add(json);
						LOGGER.error(msg + " for {} ", gstin);
						continue;
					}

					List<String> failedBatchs = new ArrayList<>();
					List<String> initAndInprogressBatchs = new ArrayList<>();
					// If request is for fetching only for Failed Sections
					if (isFailedGet == true) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("GSTR1A Failed GET Request received");
						}
						List<String> failedStatus = new ArrayList<>();
						failedStatus.add(APIConstants.FAILED.toUpperCase());

						failedBatchs = batchRepo.findBatchByStatus(
								dto.getGstin(), dto.getReturnPeriod(),
								APIConstants.GSTR1A.toUpperCase(), failedStatus);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"GSTR1A Failed GET eligible sections are {} ",
									failedBatchs);
						}
					} else {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("GSTR1A All GET Request received");
						}
						List<String> initAndInProgressStatus = new ArrayList<>();
						initAndInProgressStatus
								.add(APIConstants.INITIATED.toUpperCase());
						initAndInProgressStatus.add(
								JobStatusConstants.IN_PROGRESS.toUpperCase());
						initAndInprogressBatchs = batchRepo.findBatchByStatus(
								dto.getGstin(), dto.getReturnPeriod(),
								APIConstants.GSTR1A.toUpperCase(),
								initAndInProgressStatus);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"GSTR1A All GET Not eligible sections are {} ",
									initAndInprogressBatchs);
						}
					}
					String userName = SecurityContext.getUser()
							.getUserPrincipalName();
					Long userRequestId = gstnUserRequestUtil
							.createGstnUserRequest(gstin, taxPeriod,
									APIConstants.GET,
									APIConstants.GSTR1A.toUpperCase(), groupCode,
									userName, false, false, false);
					dto.setUserRequestId(userRequestId);
					/** Return Filed status **/

					// String filingStatus = getFilingStatus(gstin, taxPeriod);
					dto.setIsFiled(APIConstants.N);
					Integer fy = GenUtil
							.convertFytoIntFromReturnPeriod(taxPeriod);
					if (isEligible(isFailedGet, failedBatchs,
							initAndInprogressBatchs, APIConstants.B2B,
							gstr1Sections, gstin, taxPeriod)) {
						isAllsectionsGetInProgress = false;
						dto.setType(APIConstants.B2B.toUpperCase());
						dto = createBatchAndSave(groupCode, dto);
						jsonParam = gson.toJson(dto);
						jobList.add(asyncJobsService.createJobAndReturn(
								groupCode, JobConstants.GSTR1A_GSTN_GET_SECTION,
								jsonParam, userName, JobConstants.PRIORITY,
								JobConstants.PARENT_JOB_ID,
								JobConstants.SCHEDULE_AFTER_IN_MINS));

						APIParams params = new APIParams(groupCode,
								APIProviderEnum.GSTN,
								APIIdentifiers.GSTR1A_GET_B2B, param1, param2);
						JsonElement apiParams = gson.toJsonTree(params);

						gstinGetStatusService.saveOrUpdateGSTNGetStatus(
								apiParams.toString(), "INITIATED", null);

//						Commented as part of this US - 139464
//						saveOrUpdatePeriodWiseTable(gstin, taxPeriod);
//						saveOrUpdateFyTable(gstin, fy);

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Job Created for Gstr1A Get as {} ",
									job);
						}
					}
					if (isEligible(isFailedGet, failedBatchs,
							initAndInprogressBatchs, APIConstants.B2BA,
							gstr1Sections, gstin, taxPeriod)) {
						isAllsectionsGetInProgress = false;
						dto.setType(APIConstants.B2BA.toUpperCase());
						dto = createBatchAndSave(groupCode, dto);
						jsonParam = gson.toJson(dto);
						jobList.add(asyncJobsService.createJobAndReturn(
								groupCode, JobConstants.GSTR1A_GSTN_GET_SECTION,
								jsonParam, userName, JobConstants.PRIORITY,
								JobConstants.PARENT_JOB_ID,
								JobConstants.SCHEDULE_AFTER_IN_MINS));

						APIParams params = new APIParams(groupCode,
								APIProviderEnum.GSTN,
								APIIdentifiers.GSTR1A_GET_B2BA, param1, param2);
						JsonElement apiParams = gson.toJsonTree(params);

						gstinGetStatusService.saveOrUpdateGSTNGetStatus(
								apiParams.toString(), "INITIATED", null);

//						Commented as part of this US - 139464
//						saveOrUpdatePeriodWiseTable(gstin, taxPeriod);
//						saveOrUpdateFyTable(gstin, fy);
						
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Job Created for Gstr1A Get as {} ",
									job);
						}
					}
					if (isEligible(isFailedGet, failedBatchs,
							initAndInprogressBatchs, APIConstants.B2CL,
							gstr1Sections, gstin, taxPeriod)) {
						isAllsectionsGetInProgress = false;
						dto.setType(APIConstants.B2CL.toUpperCase());
						dto = createBatchAndSave(groupCode, dto);
						jsonParam = gson.toJson(dto);
						jobList.add(asyncJobsService.createJobAndReturn(
								groupCode, JobConstants.GSTR1A_GSTN_GET_SECTION,
								jsonParam, userName, JobConstants.PRIORITY,
								JobConstants.PARENT_JOB_ID,
								JobConstants.SCHEDULE_AFTER_IN_MINS));

						APIParams params = new APIParams(groupCode,
								APIProviderEnum.GSTN,
								APIIdentifiers.GSTR1A_GET_B2CL, param1, param2);
						JsonElement apiParams = gson.toJsonTree(params);

						gstinGetStatusService.saveOrUpdateGSTNGetStatus(
								apiParams.toString(), "INITIATED", null);

//						Commented as part of this US - 139464
//						saveOrUpdatePeriodWiseTable(gstin, taxPeriod);
//						saveOrUpdateFyTable(gstin, fy);
						
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Job Created for GSTR1A Get as {} ",
									job);
						}
					}
					if (isEligible(isFailedGet, failedBatchs,
							initAndInprogressBatchs, APIConstants.B2CLA,
							gstr1Sections, gstin, taxPeriod)) {
						isAllsectionsGetInProgress = false;
						dto.setType(APIConstants.B2CLA.toUpperCase());
						dto = createBatchAndSave(groupCode, dto);
						jsonParam = gson.toJson(dto);
						jobList.add(asyncJobsService.createJobAndReturn(
								groupCode, JobConstants.GSTR1A_GSTN_GET_SECTION,
								jsonParam, userName, JobConstants.PRIORITY,
								JobConstants.PARENT_JOB_ID,
								JobConstants.SCHEDULE_AFTER_IN_MINS));

						APIParams params = new APIParams(groupCode,
								APIProviderEnum.GSTN,
								APIIdentifiers.GSTR1A_GET_B2CLA, param1, param2);
						JsonElement apiParams = gson.toJsonTree(params);

						gstinGetStatusService.saveOrUpdateGSTNGetStatus(
								apiParams.toString(), "INITIATED", null);

//						Commented as part of this US - 139464
//						saveOrUpdatePeriodWiseTable(gstin, taxPeriod);
//						saveOrUpdateFyTable(gstin, fy);
						
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Job Created for GSTR1A Get as {} ",
									job);
						}
					}

					if (isEligible(isFailedGet, failedBatchs,
							initAndInprogressBatchs, APIConstants.EXP,
							gstr1Sections, gstin, taxPeriod)) {
						isAllsectionsGetInProgress = false;
						dto.setType(APIConstants.EXP.toUpperCase());
						dto = createBatchAndSave(groupCode, dto);
						jsonParam = gson.toJson(dto);
						jobList.add(asyncJobsService.createJobAndReturn(
								groupCode, JobConstants.GSTR1A_GSTN_GET_SECTION,
								jsonParam, userName, JobConstants.PRIORITY,
								JobConstants.PARENT_JOB_ID,
								JobConstants.SCHEDULE_AFTER_IN_MINS));

						APIParams params = new APIParams(groupCode,
								APIProviderEnum.GSTN,
								APIIdentifiers.GSTR1A_GET_EXP, param1, param2);
						JsonElement apiParams = gson.toJsonTree(params);

						gstinGetStatusService.saveOrUpdateGSTNGetStatus(
								apiParams.toString(), "INITIATED", null);

//						Commented as part of this US - 139464
//						saveOrUpdatePeriodWiseTable(gstin, taxPeriod);
//						saveOrUpdateFyTable(gstin, fy);
						
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Job Created for Gstr1 Get as {} ",
									job);
						}
					}
					if (isEligible(isFailedGet, failedBatchs,
							initAndInprogressBatchs, APIConstants.EXPA,
							gstr1Sections, gstin, taxPeriod)) {
						isAllsectionsGetInProgress = false;
						dto.setType(APIConstants.EXPA.toUpperCase());
						dto = createBatchAndSave(groupCode, dto);
						jsonParam = gson.toJson(dto);
						jobList.add(asyncJobsService.createJobAndReturn(
								groupCode, JobConstants.GSTR1A_GSTN_GET_SECTION,
								jsonParam, userName, JobConstants.PRIORITY,
								JobConstants.PARENT_JOB_ID,
								JobConstants.SCHEDULE_AFTER_IN_MINS));

						APIParams params = new APIParams(groupCode,
								APIProviderEnum.GSTN,
								APIIdentifiers.GSTR1A_GET_EXPA, param1, param2);
						JsonElement apiParams = gson.toJsonTree(params);

						gstinGetStatusService.saveOrUpdateGSTNGetStatus(
								apiParams.toString(), "INITIATED", null);

//						Commented as part of this US - 139464
//						saveOrUpdatePeriodWiseTable(gstin, taxPeriod);
//						saveOrUpdateFyTable(gstin, fy);
						
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Job Created for Gstr1 Get as {} ",
									job);
						}
					}
					if (isEligible(isFailedGet, failedBatchs,
							initAndInprogressBatchs, APIConstants.CDNR,
							gstr1Sections, gstin, taxPeriod)) {
						isAllsectionsGetInProgress = false;
						dto.setType(APIConstants.CDNR.toUpperCase());
						dto = createBatchAndSave(groupCode, dto);
						jsonParam = gson.toJson(dto);
						jobList.add(asyncJobsService.createJobAndReturn(
								groupCode, JobConstants.GSTR1A_GSTN_GET_SECTION,
								jsonParam, userName, JobConstants.PRIORITY,
								JobConstants.PARENT_JOB_ID,
								JobConstants.SCHEDULE_AFTER_IN_MINS));

						APIParams params = new APIParams(groupCode,
								APIProviderEnum.GSTN,
								APIIdentifiers.GSTR1A_GET_CDNR, param1, param2);
						JsonElement apiParams = gson.toJsonTree(params);

						gstinGetStatusService.saveOrUpdateGSTNGetStatus(
								apiParams.toString(), "INITIATED", null);

//						Commented as part of this US - 139464
//						saveOrUpdatePeriodWiseTable(gstin, taxPeriod);
//						saveOrUpdateFyTable(gstin, fy);
						
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Job Created for Gstr1 Get as {} ",
									job);
						}
					}
					if (isEligible(isFailedGet, failedBatchs,
							initAndInprogressBatchs, APIConstants.CDNRA,
							gstr1Sections, gstin, taxPeriod)) {
						isAllsectionsGetInProgress = false;
						dto.setType(APIConstants.CDNRA.toUpperCase());
						dto = createBatchAndSave(groupCode, dto);
						jsonParam = gson.toJson(dto);
						jobList.add(asyncJobsService.createJobAndReturn(
								groupCode, JobConstants.GSTR1A_GSTN_GET_SECTION,
								jsonParam, userName, JobConstants.PRIORITY,
								JobConstants.PARENT_JOB_ID,
								JobConstants.SCHEDULE_AFTER_IN_MINS));

						APIParams params = new APIParams(groupCode,
								APIProviderEnum.GSTN,
								APIIdentifiers.GSTR1A_GET_CDNRA, param1, param2);
						JsonElement apiParams = gson.toJsonTree(params);

						gstinGetStatusService.saveOrUpdateGSTNGetStatus(
								apiParams.toString(), "INITIATED", null);

//						Commented as part of this US - 139464
//						saveOrUpdatePeriodWiseTable(gstin, taxPeriod);
//						saveOrUpdateFyTable(gstin, fy);
						
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Job Created for Gstr1 Get as {} ",
									job);
						}
					}
					if (isEligible(isFailedGet, failedBatchs,
							initAndInprogressBatchs, APIConstants.CDNUR,
							gstr1Sections, gstin, taxPeriod)) {
						isAllsectionsGetInProgress = false;
						dto.setType(APIConstants.CDNUR.toUpperCase());
						dto = createBatchAndSave(groupCode, dto);
						jsonParam = gson.toJson(dto);
						jobList.add(asyncJobsService.createJobAndReturn(
								groupCode, JobConstants.GSTR1A_GSTN_GET_SECTION,
								jsonParam, userName, JobConstants.PRIORITY,
								JobConstants.PARENT_JOB_ID,
								JobConstants.SCHEDULE_AFTER_IN_MINS));

						APIParams params = new APIParams(groupCode,
								APIProviderEnum.GSTN,
								APIIdentifiers.GSTR1A_GET_CDNUR, param1, param2);
						JsonElement apiParams = gson.toJsonTree(params);

						gstinGetStatusService.saveOrUpdateGSTNGetStatus(
								apiParams.toString(), "INITIATED", null);

//						Commented as part of this US - 139464
//						saveOrUpdatePeriodWiseTable(gstin, taxPeriod);
//						saveOrUpdateFyTable(gstin, fy);

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Job Created for Gstr1 Get as {} ",
									job);
						}
					}
					if (isEligible(isFailedGet, failedBatchs,
							initAndInprogressBatchs, APIConstants.CDNURA,
							gstr1Sections, gstin, taxPeriod)) {
						isAllsectionsGetInProgress = false;
						dto.setType(APIConstants.CDNURA.toUpperCase());
						dto = createBatchAndSave(groupCode, dto);
						jsonParam = gson.toJson(dto);
						jobList.add(asyncJobsService.createJobAndReturn(
								groupCode, JobConstants.GSTR1A_GSTN_GET_SECTION,
								jsonParam, userName, JobConstants.PRIORITY,
								JobConstants.PARENT_JOB_ID,
								JobConstants.SCHEDULE_AFTER_IN_MINS));

						APIParams params = new APIParams(groupCode,
								APIProviderEnum.GSTN,
								APIIdentifiers.GSTR1A_GET_CDNURA, param1,
								param2);
						JsonElement apiParams = gson.toJsonTree(params);

						gstinGetStatusService.saveOrUpdateGSTNGetStatus(
								apiParams.toString(), "INITIATED", null);

//						Commented as part of this US - 139464
//						saveOrUpdatePeriodWiseTable(gstin, taxPeriod);
//						saveOrUpdateFyTable(gstin, fy);
						
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Job Created for Gstr1 Get as {} ",
									job);
						}
					}

					if (isEligible(isFailedGet, failedBatchs,
							initAndInprogressBatchs, APIConstants.B2CS,
							gstr1Sections, gstin, taxPeriod)) {
						isAllsectionsGetInProgress = false;
						dto.setType(APIConstants.B2CS.toUpperCase());
						dto = createBatchAndSave(groupCode, dto);
						jsonParam = gson.toJson(dto);
						jobList.add(asyncJobsService.createJobAndReturn(
								groupCode, JobConstants.GSTR1A_GSTN_GET_SECTION,
								jsonParam, userName, JobConstants.PRIORITY,
								JobConstants.PARENT_JOB_ID,
								JobConstants.SCHEDULE_AFTER_IN_MINS));

						APIParams params = new APIParams(groupCode,
								APIProviderEnum.GSTN,
								APIIdentifiers.GSTR1A_GET_B2CS, param1, param2);
						JsonElement apiParams = gson.toJsonTree(params);

						gstinGetStatusService.saveOrUpdateGSTNGetStatus(
								apiParams.toString(), "INITIATED", null);

//						Commented as part of this US - 139464
//						saveOrUpdatePeriodWiseTable(gstin, taxPeriod);
//						saveOrUpdateFyTable(gstin, fy);
						
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Job Created for Gstr1 Get as {} ",
									job);
						}
					}
					if (isEligible(isFailedGet, failedBatchs,
							initAndInprogressBatchs, APIConstants.B2CSA,
							gstr1Sections, gstin, taxPeriod)) {
						isAllsectionsGetInProgress = false;
						dto.setType(APIConstants.B2CSA.toUpperCase());
						dto = createBatchAndSave(groupCode, dto);
						jsonParam = gson.toJson(dto);
						jobList.add(asyncJobsService.createJobAndReturn(
								groupCode, JobConstants.GSTR1A_GSTN_GET_SECTION,
								jsonParam, userName, JobConstants.PRIORITY,
								JobConstants.PARENT_JOB_ID,
								JobConstants.SCHEDULE_AFTER_IN_MINS));

						APIParams params = new APIParams(groupCode,
								APIProviderEnum.GSTN,
								APIIdentifiers.GSTR1A_GET_B2CSA, param1, param2);
						JsonElement apiParams = gson.toJsonTree(params);

						gstinGetStatusService.saveOrUpdateGSTNGetStatus(
								apiParams.toString(), "INITIATED", null);

//						Commented as part of this US - 139464
//						saveOrUpdatePeriodWiseTable(gstin, taxPeriod);
//						saveOrUpdateFyTable(gstin, fy);

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Job Created for Gstr1 Get as {} ",
									job);
						}
					}
					if (isEligible(isFailedGet, failedBatchs,
							initAndInprogressBatchs, APIConstants.AT,
							gstr1Sections, gstin, taxPeriod)) {
						isAllsectionsGetInProgress = false;
						dto.setType(APIConstants.AT.toUpperCase());
						dto = createBatchAndSave(groupCode, dto);
						jsonParam = gson.toJson(dto);
						jobList.add(asyncJobsService.createJobAndReturn(
								groupCode, JobConstants.GSTR1A_GSTN_GET_SECTION,
								jsonParam, userName, JobConstants.PRIORITY,
								JobConstants.PARENT_JOB_ID,
								JobConstants.SCHEDULE_AFTER_IN_MINS));

						APIParams params = new APIParams(groupCode,
								APIProviderEnum.GSTN,
								APIIdentifiers.GSTR1A_GET_AT, param1, param2);
						JsonElement apiParams = gson.toJsonTree(params);

						gstinGetStatusService.saveOrUpdateGSTNGetStatus(
								apiParams.toString(), "INITIATED", null);

//						Commented as part of this US - 139464
//						saveOrUpdatePeriodWiseTable(gstin, taxPeriod);
//						saveOrUpdateFyTable(gstin, fy);

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Job Created for Gstr1 Get as {} ",
									job);
						}
					}
					if (isEligible(isFailedGet, failedBatchs,
							initAndInprogressBatchs, APIConstants.ATA,
							gstr1Sections, gstin, taxPeriod)) {
						isAllsectionsGetInProgress = false;
						dto.setType(APIConstants.ATA.toUpperCase());
						dto = createBatchAndSave(groupCode, dto);
						jsonParam = gson.toJson(dto);
						jobList.add(asyncJobsService.createJobAndReturn(
								groupCode, JobConstants.GSTR1A_GSTN_GET_SECTION,
								jsonParam, userName, JobConstants.PRIORITY,
								JobConstants.PARENT_JOB_ID,
								JobConstants.SCHEDULE_AFTER_IN_MINS));

						APIParams params = new APIParams(groupCode,
								APIProviderEnum.GSTN,
								APIIdentifiers.GSTR1A_GET_ATA, param1, param2);
						JsonElement apiParams = gson.toJsonTree(params);

						gstinGetStatusService.saveOrUpdateGSTNGetStatus(
								apiParams.toString(), "INITIATED", null);

//						Commented as part of this US - 139464
//						saveOrUpdatePeriodWiseTable(gstin, taxPeriod);
//						saveOrUpdateFyTable(gstin, fy);

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Job Created for Gstr1 Get as {} ",
									job);
						}
					}
					if (isEligible(isFailedGet, failedBatchs,
							initAndInprogressBatchs, APIConstants.TXP,
							gstr1Sections, gstin, taxPeriod)) {
						isAllsectionsGetInProgress = false;
						dto.setType(APIConstants.TXP.toUpperCase());
						dto = createBatchAndSave(groupCode, dto);
						jsonParam = gson.toJson(dto);
						jobList.add(asyncJobsService.createJobAndReturn(
								groupCode, JobConstants.GSTR1A_GSTN_GET_SECTION,
								jsonParam, userName, JobConstants.PRIORITY,
								JobConstants.PARENT_JOB_ID,
								JobConstants.SCHEDULE_AFTER_IN_MINS));

						APIParams params = new APIParams(groupCode,
								APIProviderEnum.GSTN,
								APIIdentifiers.GSTR1A_GET_TXP, param1, param2);
						JsonElement apiParams = gson.toJsonTree(params);

						gstinGetStatusService.saveOrUpdateGSTNGetStatus(
								apiParams.toString(), "INITIATED", null);

//						Commented as part of this US - 139464
//						saveOrUpdatePeriodWiseTable(gstin, taxPeriod);
//						saveOrUpdateFyTable(gstin, fy);

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Job Created for Gstr1 Get as {} ",
									job);
						}
					}
					if (isEligible(isFailedGet, failedBatchs,
							initAndInprogressBatchs, APIConstants.TXPA,
							gstr1Sections, gstin, taxPeriod)) {
						isAllsectionsGetInProgress = false;
						dto.setType(APIConstants.TXPA.toUpperCase());
						dto = createBatchAndSave(groupCode, dto);
						jsonParam = gson.toJson(dto);
						jobList.add(asyncJobsService.createJobAndReturn(
								groupCode, JobConstants.GSTR1A_GSTN_GET_SECTION,
								jsonParam, userName, JobConstants.PRIORITY,
								JobConstants.PARENT_JOB_ID,
								JobConstants.SCHEDULE_AFTER_IN_MINS));

						APIParams params = new APIParams(groupCode,
								APIProviderEnum.GSTN,
								APIIdentifiers.GSTR1A_GET_TXPA, param1, param2);
						JsonElement apiParams = gson.toJsonTree(params);

						gstinGetStatusService.saveOrUpdateGSTNGetStatus(
								apiParams.toString(), "INITIATED", null);

//						Commented as part of this US - 139464
//						saveOrUpdatePeriodWiseTable(gstin, taxPeriod);
//						saveOrUpdateFyTable(gstin, fy);

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Job Created for Gstr1 Get as {} ",
									job);
						}
					}
					if (isEligible(isFailedGet, failedBatchs,
							initAndInprogressBatchs, APIConstants.NIL,
							gstr1Sections, gstin, taxPeriod)) {
						isAllsectionsGetInProgress = false;
						dto.setType(APIConstants.NIL.toUpperCase());
						dto = createBatchAndSave(groupCode, dto);
						jsonParam = gson.toJson(dto);
						jobList.add(asyncJobsService.createJobAndReturn(
								groupCode, JobConstants.GSTR1A_GSTN_GET_SECTION,
								jsonParam, userName, JobConstants.PRIORITY,
								JobConstants.PARENT_JOB_ID,
								JobConstants.SCHEDULE_AFTER_IN_MINS));

						APIParams params = new APIParams(groupCode,
								APIProviderEnum.GSTN,
								APIIdentifiers.GSTR1A_GET_NIL_RATED, param1,
								param2);
						JsonElement apiParams = gson.toJsonTree(params);

						gstinGetStatusService.saveOrUpdateGSTNGetStatus(
								apiParams.toString(), "INITIATED", null);

//						Commented as part of this US - 139464
//						saveOrUpdatePeriodWiseTable(gstin, taxPeriod);
//						saveOrUpdateFyTable(gstin, fy);
						
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Job Created for Gstr1 Get as {} ",
									job);
						}
					}
					if (isEligible(isFailedGet, failedBatchs,
							initAndInprogressBatchs, APIConstants.HSN,
							gstr1Sections, gstin, taxPeriod)) {
						isAllsectionsGetInProgress = false;
						dto.setType(APIConstants.HSN.toUpperCase());
						dto = createBatchAndSave(groupCode, dto);
						jsonParam = gson.toJson(dto);
						jobList.add(asyncJobsService.createJobAndReturn(
								groupCode, JobConstants.GSTR1A_GSTN_GET_SECTION,
								jsonParam, userName, JobConstants.PRIORITY,
								JobConstants.PARENT_JOB_ID,
								JobConstants.SCHEDULE_AFTER_IN_MINS));

						APIParams params = new APIParams(groupCode,
								APIProviderEnum.GSTN,
								APIIdentifiers.GSTR1A_GET_HSN_SUMMARY, param1,
								param2);
						JsonElement apiParams = gson.toJsonTree(params);

						gstinGetStatusService.saveOrUpdateGSTNGetStatus(
								apiParams.toString(), "INITIATED", null);

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Job Created for Gstr1 Get as {} ",
									job);
						}
					}
					if (isEligible(isFailedGet, failedBatchs,
							initAndInprogressBatchs, APIConstants.DOC_ISSUE,
							gstr1Sections, gstin, taxPeriod)) {
						isAllsectionsGetInProgress = false;
						dto.setType(APIConstants.DOC_ISSUE.toUpperCase());
						dto = createBatchAndSave(groupCode, dto);
						jsonParam = gson.toJson(dto);
						jobList.add(asyncJobsService.createJobAndReturn(
								groupCode, JobConstants.GSTR1A_GSTN_GET_SECTION,
								jsonParam, userName, JobConstants.PRIORITY,
								JobConstants.PARENT_JOB_ID,
								JobConstants.SCHEDULE_AFTER_IN_MINS));

						APIParams params = new APIParams(groupCode,
								APIProviderEnum.GSTN,
								APIIdentifiers.GSTR1A_GET_DOC_ISSUED, param1,
								param2);
						JsonElement apiParams = gson.toJsonTree(params);

						gstinGetStatusService.saveOrUpdateGSTNGetStatus(
								apiParams.toString(), "INITIATED", null);

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Job Created for Gstr1 Get as {} ",
									job);
						}
					}
					if (isEligible(isFailedGet, failedBatchs,
							initAndInprogressBatchs, APIConstants.SUPECO,
							gstr1Sections, gstin, taxPeriod)) {
						isAllsectionsGetInProgress = false;
						dto.setType(APIConstants.SUPECO.toUpperCase());
						dto = createBatchAndSave(groupCode, dto);
						jsonParam = gson.toJson(dto);
						jobList.add(asyncJobsService.createJobAndReturn(
								groupCode, JobConstants.GSTR1A_GSTN_GET_SECTION,
								jsonParam, userName, JobConstants.PRIORITY,
								JobConstants.PARENT_JOB_ID,
								JobConstants.SCHEDULE_AFTER_IN_MINS));

						APIParams params = new APIParams(groupCode,
								APIProviderEnum.GSTN,
								APIIdentifiers.GSTR1A_GET_SUPECO, param1,
								param2);
						JsonElement apiParams = gson.toJsonTree(params);

						gstinGetStatusService.saveOrUpdateGSTNGetStatus(
								apiParams.toString(), "INITIATED", null);

//						Commented as part of this US - 139464
//						saveOrUpdatePeriodWiseTable(gstin, taxPeriod);
//						saveOrUpdateFyTable(gstin, fy);

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Job Created for Gstr1 Get as {} ",
									job);
						}
					}
					if (isEligible(isFailedGet, failedBatchs,
							initAndInprogressBatchs, APIConstants.SUPECOAMD,
							gstr1Sections, gstin, taxPeriod)) {
						isAllsectionsGetInProgress = false;
						dto.setType(APIConstants.SUPECOAMD.toUpperCase());
						dto = createBatchAndSave(groupCode, dto);
						jsonParam = gson.toJson(dto);
						jobList.add(asyncJobsService.createJobAndReturn(
								groupCode, JobConstants.GSTR1A_GSTN_GET_SECTION,
								jsonParam, userName, JobConstants.PRIORITY,
								JobConstants.PARENT_JOB_ID,
								JobConstants.SCHEDULE_AFTER_IN_MINS));

						APIParams params = new APIParams(groupCode,
								APIProviderEnum.GSTN,
								APIIdentifiers.GSTR1A_GET_SUPECO_AMD, param1,
								param2);
						JsonElement apiParams = gson.toJsonTree(params);

						gstinGetStatusService.saveOrUpdateGSTNGetStatus(
								apiParams.toString(), "INITIATED", null);

//						Commented as part of this US - 139464
//						saveOrUpdatePeriodWiseTable(gstin, taxPeriod);
//						saveOrUpdateFyTable(gstin, fy);

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Job Created for Gstr1 Get as {} ",
									job);
						}
					}
					if (isEligible(isFailedGet, failedBatchs,
							initAndInprogressBatchs, APIConstants.ECOM,
							gstr1Sections, gstin, taxPeriod)) {
						isAllsectionsGetInProgress = false;
						dto.setType(APIConstants.ECOM.toUpperCase());
						dto = createBatchAndSave(groupCode, dto);
						jsonParam = gson.toJson(dto);
						jobList.add(asyncJobsService.createJobAndReturn(
								groupCode, JobConstants.GSTR1A_GSTN_GET_SECTION,
								jsonParam, userName, JobConstants.PRIORITY,
								JobConstants.PARENT_JOB_ID,
								JobConstants.SCHEDULE_AFTER_IN_MINS));

						APIParams params = new APIParams(groupCode,
								APIProviderEnum.GSTN,
								APIIdentifiers.GSTR1A_GET_ECOM, param1, param2);
						JsonElement apiParams = gson.toJsonTree(params);

						gstinGetStatusService.saveOrUpdateGSTNGetStatus(
								apiParams.toString(), "INITIATED", null);

//						Commented as part of this US - 139464
//						saveOrUpdatePeriodWiseTable(gstin, taxPeriod);
//						saveOrUpdateFyTable(gstin, fy);

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Job Created for Gstr1 Get as {} ",
									job);
						}
					}
					if (isEligible(isFailedGet, failedBatchs,
							initAndInprogressBatchs, APIConstants.ECOMAMD,
							gstr1Sections, gstin, taxPeriod)) {
						isAllsectionsGetInProgress = false;
						dto.setType(APIConstants.ECOMAMD.toUpperCase());
						dto = createBatchAndSave(groupCode, dto);
						jsonParam = gson.toJson(dto);
						jobList.add(asyncJobsService.createJobAndReturn(
								groupCode, JobConstants.GSTR1A_GSTN_GET_SECTION,
								jsonParam, userName, JobConstants.PRIORITY,
								JobConstants.PARENT_JOB_ID,
								JobConstants.SCHEDULE_AFTER_IN_MINS));

						APIParams params = new APIParams(groupCode,
								APIProviderEnum.GSTN,
								APIIdentifiers.GSTR1A_GET_ECOM_AMD, param1,
								param2);
						JsonElement apiParams = gson.toJsonTree(params);

						gstinGetStatusService.saveOrUpdateGSTNGetStatus(
								apiParams.toString(), "INITIATED", null);

//						Commented as part of this US - 139464
//						saveOrUpdatePeriodWiseTable(gstin, taxPeriod);
//						saveOrUpdateFyTable(gstin, fy);

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Job Created for Gstr1 Get as {} ",
									job);
						}
					}

				}

				if (isAllsectionsGetInProgress) {
					JsonObject json = new JsonObject();
					json.addProperty("gstin", dto.getGstin());
					json.addProperty("msg",
							"Get GSTR1A is Already Inprogress, New Request can't be taken");

					respBody.add(json);
				} else {
					JsonObject json = new JsonObject();
					json.addProperty("gstin", dto.getGstin());
					json.addProperty("msg",
							"Get GSTR1A Request Initiated Successfully");
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
			String msg = "Unexpected error while creating async jobs for GSTR1A Get ";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	private Gstr1GetInvoicesReqDto createBatchAndSave(String groupCode,
			Gstr1GetInvoicesReqDto dto) {

		TenantContext.setTenantId(groupCode);
		// InActiveting Previous Batch Records
		batchRepo.softlyDelete(dto.getType().toUpperCase(),
				APIConstants.GSTR1A.toUpperCase(), dto.getGstin(),
				dto.getReturnPeriod());
		GetAnx1BatchEntity batch = batchUtil.makeBatchGstr1(dto, dto.getType(),
				APIConstants.GSTR1A.toUpperCase());
		// Save new Batch
		batch = batchRepo.save(batch);
		dto.setBatchId(batch.getId());
		dto.setApiSection(APIConstants.GSTR1A.toUpperCase());
		return dto;

	}

	private JsonArray getAllActiveGstnList(List<Gstr1GetInvoicesReqDto> dtos) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Getting all the active GSTNs for GSTR1A GET");
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
				dto.getGstr1Sections().replaceAll(String::toUpperCase);
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

	// Check weather the section is eligible to create new job or not.
	private Boolean isEligible(Boolean isFailedGet, List<String> failedBatchs,
			List<String> initAndInprogressBatchs, String section,
			List<String> gstr1Sections, String gstin, String taxPeriod) {

		String previousGetFiledStatus = batchRepo.findByGstinAndTaxPeriod(gstin,
				taxPeriod, APIConstants.GSTR1A.toUpperCase(),
				section.toUpperCase());
		if (APIConstants.Y.equalsIgnoreCase(getFilingStatus(gstin, taxPeriod))
				&& APIConstants.Y.equalsIgnoreCase(previousGetFiledStatus)) {
			LOGGER.error(
					"Gstr1 get {} section for the given gstin {} and taxperiod {} is already filed",
					section, gstin, taxPeriod);
			return false;
		}
		if ((isFailedGet == false
				&& !initAndInprogressBatchs.contains(section.toUpperCase())
				&& gstr1Sections.isEmpty())
				|| (isFailedGet == false
						&& !initAndInprogressBatchs
								.contains(section.toUpperCase())
						&& gstr1Sections.contains(section.toUpperCase()))
				|| (isFailedGet == true
						&& failedBatchs.contains(section.toUpperCase()))) {
			return isEligibletoPost(taxPeriod, section);
		} else {
			return false;
		}
	}

	private boolean isEligibletoPost(String taxPeriod, String section) {
		boolean isSupecoOrEcom = section.equalsIgnoreCase(APIConstants.SUPECO)
				|| section.equalsIgnoreCase(APIConstants.ECOM);
		boolean isSupecoAmdOrEcomAmd = isSupecoOrEcom
				|| section.equalsIgnoreCase(APIConstants.SUPECOAMD)
				|| section.equalsIgnoreCase(APIConstants.ECOMAMD);

		if (isSupecoAmdOrEcomAmd) {
			YearMonth givenDate = YearMonth.parse(taxPeriod,
					DateTimeFormatter.ofPattern("MMyyyy"));
			YearMonth january2024 = YearMonth.of(2024, 1);
			YearMonth february2024 = YearMonth.of(2024, 2);

			if ((givenDate.compareTo(january2024) >= 0 && isSupecoOrEcom)
					|| (givenDate.compareTo(february2024) >= 0
							&& isSupecoAmdOrEcomAmd)) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}

	}

	private String getFilingStatus(String gstin, String retPeriod) {

		String confValue = gstnApi.getReturnFilingStatusConfig();
		// Do not invoke return status api
		if ("0".equals(confValue)) {
			return APIConstants.N;
		}
		String isFiled = APIConstants.N;
		List<String> listGstins = new ArrayList<>();
		listGstins.add(gstin);
		try {
			List<ReturnFilingGstnResponseDto> filingStatusDto = returnFilingStatus
					.callGstnApi(listGstins, getFinancialYear(), false);
			for (ReturnFilingGstnResponseDto dto : filingStatusDto) {
				if (APIConstants.GSTR1A.equalsIgnoreCase(dto.getRetType())
						&& retPeriod.equalsIgnoreCase(dto.getRetPeriod())) {
					isFiled = dto.getStatus() != null ? APIConstants.Y
							: APIConstants.N;
					break;
				}
			}
		} catch (Exception e) {
			LOGGER.error("Retrun filing status api has failed", e);
		}
		return isFiled;

	}

	private String getFinancialYear() {

		int year = LocalDate.now().getYear();
		int month = LocalDate.now().getMonthValue() + 1;

		String finYear = null;
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Financial month : {} ", month);
		if (month < 3) {
			finYear = (year - 1) + "-" + String.valueOf(year).substring(2);
		} else {
			finYear = year + "-" + String.valueOf(year + 1).substring(2);
		}
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Financial year : {} ", finYear);
		return finYear;
	}

	private void saveOrUpdatePeriodWiseTable(String gstin, String taxPeriod) {

		int rowsEffected = 0;

		try {
			rowsEffected = gstr9PeriodWiseRepo.updateGstr1GetStatus(gstin,
					taxPeriod, false, LocalDateTime.now(), null);

			if (rowsEffected == 0) {

				if (LOGGER.isDebugEnabled()) {

					LOGGER.debug(
							"Creating new entry for GSTR1A Period wise with "
									+ "gstin : " + gstin + ", taxperiod : "
									+ taxPeriod);
				}

				try {

					Integer derivedTaxPeriod = Integer.valueOf(taxPeriod
							.substring(2).concat(taxPeriod.substring(0, 2)));

					Integer fy = Integer.valueOf(
							GenUtil.getFinancialYearByTaxperiod(taxPeriod)
									.replace("-", ""));

					Gstr9PeriodWiseEntity gstr9PeriodWiseEntity = new Gstr9PeriodWiseEntity(
							gstin, fy, taxPeriod, derivedTaxPeriod, "SYSTEM",
							LocalDateTime.now());

					gstr9PeriodWiseRepo.save(gstr9PeriodWiseEntity);

				} catch (DataIntegrityViolationException ex) {
					// Log the exception and proceed. This is normal and can
					// happen if a race condition occurs when 2 threads try
					// to insert into the AnnualReturnController table.
					String errMsg = String
							.format("Trying to insert duplicate entry in GSTR1A period wise "
									+ "table. Ignoring the error");
					// Don't throw back the error.
					LOGGER.error(errMsg, ex);
				}

			} else {

				if (LOGGER.isDebugEnabled()) {
					String msg1 = String.format(
							"After Resetting GSTR1A Period wise table for"
									+ "GSTIN: '%s', TaxPeriod: '%s'",
							gstin, taxPeriod);
					LOGGER.debug(msg1);
				}
			}

		} catch (Exception ex) {
			// Log the exception and proceed. This is normal and can
			// happen if a race condition occurs when 2 threads try
			// to insert into the AnnualReturnController table.
			String errMsg = String
					.format("Trying to update entry in GSTR1A Period wise "
							+ "table. Ignoring the error");
			// Don't throw back the error.
			LOGGER.error(errMsg, ex);
		}
	}

	private void saveOrUpdateFyTable(String gstin, Integer fy) {

		int rowsEffected = 0;

		try {
			rowsEffected = gstr9FyRepo.updateGstr1GetStatus(
					Arrays.asList(gstin), fy, false, LocalDateTime.now());

			if (rowsEffected == 0) {

				if (LOGGER.isDebugEnabled()) {

					LOGGER.debug("Creating new entry for GSTR1A Fy table for "
							+ "gstin : " + gstin + ", fy : " + fy);
				}

				try {

					Gstr9ComputeReadyStatusFYEntity entity = new Gstr9ComputeReadyStatusFYEntity(
							gstin, fy, LocalDateTime.now());

					gstr9FyRepo.save(entity);
				} catch (DataIntegrityViolationException ex) {
					// Log the exception and proceed. This is normal and can
					// happen if a race condition occurs when 2 threads try
					// to insert into the AnnualReturnController table.
					String errMsg = String
							.format("Trying to insert duplicate entry in GSTR1A Fy "
									+ "table. Ignoring the error");
					// Don't throw back the error.
					LOGGER.error(errMsg, ex);
				}

			} else {

				if (LOGGER.isDebugEnabled()) {
					String msg1 = String.format("After Resetting Fy table for"
							+ "GSTIN: '%s', fy: '%s'", gstin, fy);
					LOGGER.debug(msg1);
				}
			}

		} catch (Exception ex) {
			// Log the exception and proceed. This is normal and can
			// happen if a race condition occurs when 2 threads try
			// to insert into the AnnualReturnController table.
			String errMsg = String.format("Trying to update entry in GSTR1A Fy "
					+ "table. Ignoring the error");
			// Don't throw back the error.
			LOGGER.error(errMsg, ex);
		}
	}

}
