package com.ey.advisory.app.services.gstr8;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
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
 * @author Shashikant.Shukla
 *
 */

@Service("Gstr8ApiGetCallHandler")
@Slf4j
public class Gstr8ApiGetCallHandler {

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

	@Transactional(value = "clientTransactionManager")
	public ResponseEntity<String> createGstr8Gstn(
			List<Gstr1GetInvoicesReqDto> dtos, List<AsyncExecJob> jobList) {

		JsonObject resp = new JsonObject();

		try {
			Gson gson = GsonUtil.newSAPGsonInstance();
			// Dto will contain only active GSTIN's and
			// respBody contains invalid GSTIN's list with messages
//			JsonArray respBody = getAllActiveGstnList(dtos);
			APIRespDto apiResp = null;

//			if (respBody != null && !respBody.equals(new JsonArray())) {
//				apiResp = APIRespDto.creatErrorResp();
//			}
			JsonArray respBody = new JsonArray();
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
							LOGGER.debug("GSTR8 Failed GET Request received");
						}
						List<String> failedStatus = new ArrayList<>();
						failedStatus.add(APIConstants.FAILED.toUpperCase());

						failedBatchs = batchRepo.findBatchByStatus(
								dto.getGstin(), dto.getReturnPeriod(),
								APIConstants.GSTR8.toUpperCase(), failedStatus);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"GSTR8 Failed GET eligible sections are {} ",
									failedBatchs);
						}
					} else {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("GSTR8 All GET Request received");
						}
						List<String> initAndInProgressStatus = new ArrayList<>();
						initAndInProgressStatus
								.add(APIConstants.INITIATED.toUpperCase());
						initAndInProgressStatus.add(
								JobStatusConstants.IN_PROGRESS.toUpperCase());
						initAndInprogressBatchs = batchRepo.findBatchByStatus(
								dto.getGstin(), dto.getReturnPeriod(),
								APIConstants.GSTR8.toUpperCase(),
								initAndInProgressStatus);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"GSTR8 All GET Not eligible sections are {} ",
									initAndInprogressBatchs);
						}
					}
					String userName = SecurityContext.getUser()
							.getUserPrincipalName();
					Long userRequestId = gstnUserRequestUtil
							.createGstnUserRequest(gstin, taxPeriod,
									APIConstants.GET,
									APIConstants.GSTR8.toUpperCase(), groupCode,
									userName, false, false, false);
					dto.setUserRequestId(userRequestId);
					/** Return Filed status **/

					// String filingStatus = getFilingStatus(gstin, taxPeriod);
					dto.setIsFiled(APIConstants.N);
					if (isEligible(isFailedGet, failedBatchs,
							initAndInprogressBatchs, APIConstants.TCS,
							gstr1Sections, gstin, taxPeriod)) {
						isAllsectionsGetInProgress = false;
						dto.setType(APIConstants.TCS.toUpperCase());
						dto = createBatchAndSave(groupCode, dto);
						jsonParam = gson.toJson(dto);
						jobList.add(asyncJobsService.createJobAndReturn(
								groupCode, JobConstants.GSTR8_GSTN_GET_SECTION,
								jsonParam, userName, JobConstants.PRIORITY,
								JobConstants.PARENT_JOB_ID,
								JobConstants.SCHEDULE_AFTER_IN_MINS));

						APIParams params = new APIParams(groupCode,
								APIProviderEnum.GSTN,
								APIIdentifiers.GSTR8_GET_TCS, param1, param2);
						JsonElement apiParams = gson.toJsonTree(params);

						gstinGetStatusService.saveOrUpdateGSTNGetStatus(
								apiParams.toString(), "INITIATED", null);
						
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Job Created for Gstr1 Get as {} ",
									job);
						}
					}
					if (isEligible(isFailedGet, failedBatchs,
							initAndInprogressBatchs, APIConstants.URD,
							gstr1Sections, gstin, taxPeriod)) {
						isAllsectionsGetInProgress = false;
						dto.setType(APIConstants.URD.toUpperCase());
						dto = createBatchAndSave(groupCode, dto);
						jsonParam = gson.toJson(dto);
						jobList.add(asyncJobsService.createJobAndReturn(
								groupCode, JobConstants.GSTR8_GSTN_GET_SECTION,
								jsonParam, userName, JobConstants.PRIORITY,
								JobConstants.PARENT_JOB_ID,
								JobConstants.SCHEDULE_AFTER_IN_MINS));

						APIParams params = new APIParams(groupCode,
								APIProviderEnum.GSTN,
								APIIdentifiers.GSTR8_GET_URD, param1, param2);
						JsonElement apiParams = gson.toJsonTree(params);

						gstinGetStatusService.saveOrUpdateGSTNGetStatus(
								apiParams.toString(), "INITIATED", null);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Job Created for Gstr8 Get as {} ",
									job);
						}
					}
				}

				if (isAllsectionsGetInProgress) {
					JsonObject json = new JsonObject();
					json.addProperty("gstin", dto.getGstin());
					json.addProperty("msg",
							"Get GSTR8 is Already Inprogress, New Request can't be taken");

					respBody.add(json);
				} else {
					JsonObject json = new JsonObject();
					json.addProperty("gstin", dto.getGstin());
					json.addProperty("msg",
							"Get GSTR8 Request Initiated Successfully");
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
			String msg = "Unexpected error while creating async jobs for GSTR8 Get ";
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
				APIConstants.GSTR8.toUpperCase(), dto.getGstin(),
				dto.getReturnPeriod());
		GetAnx1BatchEntity batch = batchUtil.makeBatchGstr1(dto, dto.getType(),
				APIConstants.GSTR8.toUpperCase());
		// Save new Batch
		batch = batchRepo.save(batch);
		dto.setBatchId(batch.getId());
		return dto;

	}

	private JsonArray getAllActiveGstnList(List<Gstr1GetInvoicesReqDto> dtos) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Getting all the active GSTNs for GSTR1 GET");
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
				taxPeriod, APIConstants.GSTR8.toUpperCase(),
				section.toUpperCase());
		if (APIConstants.Y.equalsIgnoreCase(getFilingStatus(gstin, taxPeriod))
				&& APIConstants.Y.equalsIgnoreCase(previousGetFiledStatus)) {
			LOGGER.error(
					"Gstr8 get {} section for the given gstin {} and taxperiod {} is already filed",
					section, gstin, taxPeriod);
			return false;
		}
		return true;
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
				if (APIConstants.GSTR8.equalsIgnoreCase(dto.getRetType())
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
}
