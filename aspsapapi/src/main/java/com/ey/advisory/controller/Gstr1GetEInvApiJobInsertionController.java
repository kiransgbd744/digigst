package com.ey.advisory.controller;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.core.dto.ReturnFilingGstnResponseDto;
import com.ey.advisory.gstnapi.PublicApiConstants;
import com.ey.advisory.gstnapi.PublicApiContext;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@RestController
@Slf4j
public class Gstr1GetEInvApiJobInsertionController {

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

	@PostMapping(value = "/ui/Gstr1EInvGstnGetSection", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> createGstr1EInvGstnGetJob(
			@RequestBody String request) {

		JsonObject resp = new JsonObject();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr1EInvGstnGetSection Request received from UI as {} ",
						request);
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject requestObject = (new JsonParser()).parse(request)
					.getAsJsonObject();
			JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();
			Type listType = new TypeToken<List<Gstr1GetInvoicesReqDto>>() {
			}.getType();
			List<Gstr1GetInvoicesReqDto> dtos = gson.fromJson(asJsonArray,
					listType);
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

				/**
				 * Extra Logic to support fromTime/toTime
				 */
				Integer fromPeriod = StringUtils.isNotEmpty(dto.getFromPeriod())
						? GenUtil.convertTaxPeriodToInt(dto.getFromPeriod())
						: null;
				Integer toPeriod = StringUtils.isNotEmpty(dto.getToPeriod())
						? GenUtil.convertTaxPeriodToInt(dto.getToPeriod())
						: null;
				if (fromPeriod == null || toPeriod == null) {
					apiResp = APIRespDto.creatErrorResp();
					JsonObject json = new JsonObject();
					msg = "FromPeriod and ToPeriod cannot be null";
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
					String gstin = dto.getGstin();
					Boolean isFailedGet = dto.getIsFailed() == null ? false
							: dto.getIsFailed();
					List<String> gstr1EinvSections = dto.getGstr1EinvSections();
					String groupCode = TenantContext.getTenantId();
					TenantContext.setTenantId(groupCode);
					// Get the Registration date of the GSTIN from onboarding.
					GSTNDetailEntity gstinInfo = ehcachegstin
							.getGstinInfo(groupCode, gstin);

					LocalDate regDate = gstinInfo.getRegDate();
					if (regDate != null) {
						// Creates a YearMonth object
						YearMonth thisYearMonth = YearMonth.of(
								Integer.valueOf(taxPeriod.substring(2, 6)),
								Integer.valueOf(taxPeriod.substring(0, 2)));
						// Last day of the taxperiod
						LocalDate lastDayOfTaxPeriod = thisYearMonth
								.atEndOfMonth();

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
							LOGGER.debug(
									"GSTR1 Failed GET-EInvoice Request received");
						}
						List<String> failedStatus = new ArrayList<>();
						failedStatus.add(APIConstants.FAILED.toUpperCase());

						failedBatchs = batchRepo.findBatchByStatus(
								dto.getGstin(), dto.getReturnPeriod(),
								APIConstants.GSTR1_EINV.toUpperCase(),
								failedStatus);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"GSTR1 Failed GET-EInvoice eligible sections are {} ",
									failedBatchs);
						}
					} else {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"GSTR1 All GET-EInvoice Request received");
						}
						List<String> initAndInProgressStatus = new ArrayList<>();
						initAndInProgressStatus
								.add(APIConstants.INITIATED.toUpperCase());
						initAndInProgressStatus.add(
								JobStatusConstants.IN_PROGRESS.toUpperCase());
						initAndInprogressBatchs = batchRepo.findBatchByStatus(
								dto.getGstin(), dto.getReturnPeriod(),
								APIConstants.GSTR1_EINV.toUpperCase(),
								initAndInProgressStatus);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"GSTR1 All GET-EInvoice Not eligible sections are {} ",
									initAndInprogressBatchs);
						}
					}
					String userName = SecurityContext.getUser()
							.getUserPrincipalName();
					Long userRequestId = gstnUserRequestUtil
							.createGstnUserRequest(gstin, taxPeriod,
									APIConstants.GET,
									APIConstants.GSTR1_EINV.toUpperCase(),
									groupCode, userName, false, false, false);
					dto.setUserRequestId(userRequestId);
					/** Return Filed status **/

					String filingStatus = getFilingStatus(gstin, taxPeriod);
					dto.setIsFiled(filingStatus);
					if (isEligible(isFailedGet, failedBatchs,
							initAndInprogressBatchs, APIConstants.EINV_B2B,
							gstr1EinvSections, gstin, taxPeriod)) {
						isAllsectionsGetInProgress = false;
						dto.setType(APIConstants.EINV_B2B.toUpperCase());
						dto = createBatchAndSave(groupCode, dto);
						jsonParam = gson.toJson(dto);
						job = asyncJobsService.createJob(groupCode,
								JobConstants.GSTR1_EINVOICE_GET_SECTION,
								jsonParam, userName, JobConstants.PRIORITY,
								JobConstants.PARENT_JOB_ID,
								JobConstants.SCHEDULE_AFTER_IN_MINS);

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Job Created for Gstr1 GET-EInvoice as {} ",
									job);
						}
					}
					if (isEligible(isFailedGet, failedBatchs,
							initAndInprogressBatchs, APIConstants.EINV_CDNR,
							gstr1EinvSections, gstin, taxPeriod)) {
						isAllsectionsGetInProgress = false;
						dto.setType(APIConstants.EINV_CDNR.toUpperCase());
						dto = createBatchAndSave(groupCode, dto);
						jsonParam = gson.toJson(dto);
						job = asyncJobsService.createJob(groupCode,
								JobConstants.GSTR1_EINVOICE_GET_SECTION,
								jsonParam, userName, JobConstants.PRIORITY,
								JobConstants.PARENT_JOB_ID,
								JobConstants.SCHEDULE_AFTER_IN_MINS);

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Job Created for Gstr1 GET-EInvoice as {} ",
									job);
						}
					}
					if (isEligible(isFailedGet, failedBatchs,
							initAndInprogressBatchs, APIConstants.EINV_CDNUR,
							gstr1EinvSections, gstin, taxPeriod)) {
						isAllsectionsGetInProgress = false;
						dto.setType(APIConstants.EINV_CDNUR.toUpperCase());
						dto = createBatchAndSave(groupCode, dto);
						jsonParam = gson.toJson(dto);
						job = asyncJobsService.createJob(groupCode,
								JobConstants.GSTR1_EINVOICE_GET_SECTION,
								jsonParam, userName, JobConstants.PRIORITY,
								JobConstants.PARENT_JOB_ID,
								JobConstants.SCHEDULE_AFTER_IN_MINS);

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Job Created for Gstr1 GET-EInvoice as {} ",
									job);
						}
					}
					if (isEligible(isFailedGet, failedBatchs,
							initAndInprogressBatchs, APIConstants.EINV_EXP,
							gstr1EinvSections, gstin, taxPeriod)) {
						isAllsectionsGetInProgress = false;
						dto.setType(APIConstants.EINV_EXP.toUpperCase());
						dto = createBatchAndSave(groupCode, dto);
						jsonParam = gson.toJson(dto);
						job = asyncJobsService.createJob(groupCode,
								JobConstants.GSTR1_EINVOICE_GET_SECTION,
								jsonParam, userName, JobConstants.PRIORITY,
								JobConstants.PARENT_JOB_ID,
								JobConstants.SCHEDULE_AFTER_IN_MINS);

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Job Created for Gstr1 GET-EInvoice as {} ",
									job);
						}
					}

				}

				if (isAllsectionsGetInProgress) {
					JsonObject json = new JsonObject();
					json.addProperty("gstin", dto.getGstin());
					json.addProperty("msg",
							"Get GSTR1 E-Invoices is Already Inprogress, New Request can't be taken");

					respBody.add(json);
				} else {
					JsonObject json = new JsonObject();
					json.addProperty("gstin", dto.getGstin());
					json.addProperty("msg",
							"Get GSTR1 E-Invoices Request Initiated Successfully");
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
			String msg = "Unexpected error while creating async jobs for Gstr1 GET-EInvoices ";
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
				APIConstants.GSTR1_EINV.toUpperCase(), dto.getGstin(),
				dto.getReturnPeriod());
		GetAnx1BatchEntity batch = batchUtil.makeBatchGstr1(dto, dto.getType(),
				APIConstants.GSTR1_EINV.toUpperCase());
		// Save new Batch
		batch = batchRepo.save(batch);
		dto.setBatchId(batch.getId());
		return dto;

	}

	private JsonArray getAllActiveGstnList(List<Gstr1GetInvoicesReqDto> dtos) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Getting all the active GSTNs for GSTR1 GET-EInvoices");
		}

		String msg = "";
		List<Gstr1GetInvoicesReqDto> inActiveGstinDtos = new ArrayList<>();
		JsonArray respBody = new JsonArray();
		if (dtos != null && !dtos.isEmpty()) {

			for (Gstr1GetInvoicesReqDto dto : dtos) {
				String gstin = dto.getGstin();
				JsonObject json = new JsonObject();
				String authStatus = authTokenService
						.getAuthTokenStatusForGstin(gstin);
				dto.getGstr1EinvSections().replaceAll(String::toUpperCase);
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
				taxPeriod, APIConstants.GSTR1_EINV.toUpperCase(),
				section.toUpperCase());
		if (APIConstants.Y.equalsIgnoreCase(previousGetFiledStatus)) {
			LOGGER.error(
					"Gstr1 get-einvoices {} section for the given gstin {} and taxperiod {} is already filed",
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
			return true;
		} else {
			return false;
		}
	}

	private String getFilingStatus(String gstin, String retPeriod) {
		String isFiled = APIConstants.N;
		List<String> listGstins = new ArrayList<>();
		listGstins.add(gstin);

		PublicApiContext.setContextMap(PublicApiConstants.SOURCE,
				PublicApiConstants.GSTR1_EINV_GSTN_GET_SEC_RET);

		List<ReturnFilingGstnResponseDto> filingStatusDto = returnFilingStatus
				.callGstnApi(listGstins, getFinancialYear(), false);
		for (ReturnFilingGstnResponseDto dto : filingStatusDto) {
			if (APIConstants.GSTR1.equalsIgnoreCase(dto.getRetType())
					&& retPeriod.equalsIgnoreCase(dto.getRetPeriod())) {
				isFiled = dto.getStatus() != null ? APIConstants.Y
						: APIConstants.N;
				break;
			}
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
