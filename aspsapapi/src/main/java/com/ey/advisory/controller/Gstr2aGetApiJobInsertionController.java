package com.ey.advisory.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.services.jobs.gstr2a.Gstr2aGetCallHandler;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.app.util.GstnUserRequestUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@RestController
@Slf4j
public class Gstr2aGetApiJobInsertionController {
	
	private static final List<String> GET2A_SECTIONS = ImmutableList.of(
			APIConstants.B2B, APIConstants.B2BA, APIConstants.CDN,
			APIConstants.CDNA, APIConstants.ISD, APIConstants.IMPG,
			APIConstants.IMPGSEZ);

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
	private EntityConfigPrmtRepository entityConfigRepo;
	
	@Autowired
	private Gstr2aGetCallHandler gstr2aGetCallHandler;

	@PostMapping(value = "/ui/Gstr2aGstnGetSection", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> createGstr2AGstnGetJob(
			@RequestBody String request) {

		JsonObject resp = new JsonObject();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2AGstnGetSection Request received from UI as {} ",
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
			
			List<AsyncExecJob> jobList = new ArrayList<>();

			ResponseEntity<String> response = gstr2aGetCallHandler.getCallGSTR2A(dtos, jobList);

			if (!jobList.isEmpty())
				asyncJobsService.createJobs(jobList);

			return response;

		} catch (Exception ex) {
			String msg = "Unexpected error while creating async jobs for Gstr2a GET ";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/*public ResponseEntity<String> getCallGSTR2A(
			List<Gstr1GetInvoicesReqDto> dtos) {

		// Dto will contain only active GSTIN's and
		// respBody contains invalid GSTIN's list with messages
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
			for (Gstr1GetInvoicesReqDto dto : dtos) {

				*//**
				 * Extra Logic to support fromTime/toTime
				 *//*
				Integer fromPeriod = !StringUtils.isEmpty(dto.getFromPeriod())
						? GenUtil.convertTaxPeriodToInt(dto.getFromPeriod())
						: GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod());
				Integer toPeriod = !StringUtils.isEmpty(dto.getToPeriod())
						? GenUtil.convertTaxPeriodToInt(dto.getToPeriod())
						: GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod());

				*//**
				 * Extra Logic to support month and finYear
				 *//*
				if (fromPeriod == null || toPeriod == null) {
					String finYr = dto.getFinYear();
					
					 * StringBuffer buffer = new StringBuffer();
					 * dto.getMonth().stream().forEach(month ->
					 * buffer.append(month).append(",")); String month = null;
					 * if (dto.getMonth() != null && dto.getMonth().size() > 1)
					 * { month = (buffer.toString().substring(0,
					 * buffer.toString().length()-1)); } else { month =
					 * buffer.toString(); }
					 
					List<String> monthAsList = dto.getMonth();

					if (monthAsList != null && !monthAsList.isEmpty()) {
						String firstMonth = monthAsList.get(0);
						String lastMonth = monthAsList
								.get(monthAsList.size() - 1);
						// To convert the month value as returnPeriod.
						String monthAsRetPeriod1 = getRetPeriod(firstMonth,
								finYr);
						// To convert the month value as returnPeriod.
						String monthAsRetPeriod2 = getRetPeriod(lastMonth,
								finYr);

						fromPeriod = GenUtil
								.convertTaxPeriodToInt(monthAsRetPeriod1);
						toPeriod = GenUtil
								.convertTaxPeriodToInt(monthAsRetPeriod2);

					} else if (!StringUtils.isEmpty(finYr)) {
						fromPeriod = GenUtil.convertTaxPeriodToInt(
								"04" + finYr.substring(0, 4));
						toPeriod = GenUtil.convertTaxPeriodToInt(
								"01" + finYr.substring(0, 2)
										+ finYr.substring(5, 7));

					}

				}
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
					
					*//**
					 * If taxPeriod is future period then continue
					 * next iteration;
					 *//*
					if (!GenUtil.isValidTaxPeriodForCurrentFy(taxPeriod)) {

						LOGGER.error(
								"Requested TaxPeriod {} is Future TaxPeriod, Hence skipping {} ",
								taxPeriod, APIConstants.GSTR2A.toUpperCase());
						continue;
					}
					
					dto.setReturnPeriod(taxPeriod);
					String gstin = dto.getGstin();
					Boolean isFailedGet = dto.getIsFailed() == null ? false
							: dto.getIsFailed();
					List<String> gstr2aSections = dto.getGstr2aSections();
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
							LOGGER.debug("GSTR2A Failed GET Request received");
						}
						List<String> failedStatus = new ArrayList<>();
						failedStatus.add(APIConstants.FAILED.toUpperCase());

						failedBatchs = batchRepo.findBatchByStatus(
								dto.getGstin(), dto.getReturnPeriod(),
								APIConstants.GSTR2A.toUpperCase(),
								failedStatus);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"GSTR2A Failed GET eligible sections are {} ",
									failedBatchs);
						}
					} else {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("GSTR2A All GET Request received");
						}
						List<String> initAndInProgressStatus = new ArrayList<>();
						initAndInProgressStatus
								.add(APIConstants.INITIATED.toUpperCase());
						initAndInProgressStatus.add(
								JobStatusConstants.IN_PROGRESS.toUpperCase());
						initAndInprogressBatchs = batchRepo.findBatchByStatus(
								dto.getGstin(), dto.getReturnPeriod(),
								APIConstants.GSTR2A.toUpperCase(),
								initAndInProgressStatus);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"GSTR2A All GET Not eligible sections are {} ",
									initAndInprogressBatchs);
						}
					}
					String userName = SecurityContext.getUser()
							.getUserPrincipalName();
					Long userRequestId = gstnUserRequestUtil
							.createGstnUserRequest(gstin, taxPeriod,
									APIConstants.GET,
									APIConstants.GSTR2A.toUpperCase(),
									groupCode, userName, false, false, false);
					dto.setUserRequestId(userRequestId);
					dto.setApiSection(APIConstants.GSTR2A.toUpperCase());
					
					*//** set isDeltaGet status **//*
					boolean isFDeltaGetData = false;
					EntityConfigPrmtEntity entityConfig = entityConfigRepo
							.findByGroupCodeAndEntityIdAndparamkryIdAndIsDeleteFalse(
									dto.getGroupcode(), gstinInfo.getEntityId(),
									"I25");
					String paramValue = entityConfig!= null ? entityConfig.getParamValue() : "A";
					if ("B".equals(paramValue)) {
						isFDeltaGetData = true;
					} 
					dto.setIsDeltaGet(isFDeltaGetData);
					
					for (String get2aSection : GET2A_SECTIONS) {

						if (isEligible(isFailedGet, failedBatchs,
								initAndInprogressBatchs, get2aSection,
								gstr2aSections)) {
							isAllsectionsGetInProgress = false;
							dto.setType(get2aSection.toUpperCase());
							dto = createBatchAndSave(groupCode, dto);
							jsonParam = gson.toJson(dto);
							job = asyncJobsService.createJob(groupCode,
									JobConstants.GSTR2A_GSTN_GET_SECTION,
									jsonParam, userName, JobConstants.PRIORITY,
									JobConstants.PARENT_JOB_ID,
									JobConstants.SCHEDULE_AFTER_IN_MINS);

							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug(
										"Job Created for Gstr2A Get as {} ",
										job);
							}
						}

					}
					
				}
				if (isAllsectionsGetInProgress) {
					JsonObject json = new JsonObject();
					json.addProperty("gstin", dto.getGstin());
					json.addProperty("msg",
							"Get GSTR2A is Already Inprogress, New Request can't be taken");

					respBody.add(json);
				} else {
					JsonObject json = new JsonObject();
					json.addProperty("gstin", dto.getGstin());
					json.addProperty("msg",
							"Get GSTR2A Request Initiated Successfully");
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
			String msg = "Unexpected error while creating async jobs for GSTR2A Get ";
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
				APIConstants.GSTR2A.toUpperCase(), dto.getGstin(),
				dto.getReturnPeriod());
		GetAnx1BatchEntity batch = batchUtil.makeBatchGstr1(dto, dto.getType(),
				APIConstants.GSTR2A.toUpperCase());
		// Save new Batch
		batch = batchRepo.save(batch);
		dto.setBatchId(batch.getId());
		return dto;

	}

	private JsonArray getAllActiveGstnList(List<Gstr1GetInvoicesReqDto> dtos) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Getting all the active GSTNs for GSTR2A GET");
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

	// Check weather the section is eligible to create new job or not.
	private Boolean isEligible(Boolean isFailedGet, List<String> failedBatchs,
			List<String> initAndInprogressBatchs, String section,
			List<String> gstr2aSections) {

		if ((isFailedGet == false
				&& !initAndInprogressBatchs.contains(section.toUpperCase())
				&& gstr2aSections.isEmpty())
				|| (isFailedGet == false
						&& !initAndInprogressBatchs
								.contains(section.toUpperCase())
						&& gstr2aSections.contains(section.toUpperCase()))
				|| (isFailedGet == true
						&& failedBatchs.contains(section.toUpperCase()))) {
			return true;
		} else {
			return false;
		}
	}

	private String getRetPeriod(String month, String finYear) {
		if (!StringUtils.isEmpty(month)) {
			if (month.equals("01") || month.equals("02")
					|| month.equals("03")) {
				month = month + finYear.substring(0, 2)
						+ finYear.substring(5, 7);
			} else {
				month = month + finYear.substring(0, 4);

			}

		}
		return month;
	}*/
}
