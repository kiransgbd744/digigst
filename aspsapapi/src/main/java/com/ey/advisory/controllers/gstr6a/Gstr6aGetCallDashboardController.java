package com.ey.advisory.controllers.gstr6a;

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
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr6aDashboardReqDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Ravindra V S
 *
 */

@RestController
@Slf4j
public class Gstr6aGetCallDashboardController {

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

	@PostMapping(value = "/ui/Gstr6aGetCallDashboard", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> createGstr6aGstnGetJob(
			@RequestBody String request) {

		JsonObject resp = new JsonObject();
		List<String> taxPeriodList = new ArrayList<>();

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr6aGetCallDashboard Request received from UI as {} ",
						request);
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject requestObject = (new JsonParser()).parse(request)
					.getAsJsonObject();
			JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();
			Type listType = new TypeToken<List<Gstr6aDashboardReqDto>>() {
			}.getType();
			List<Gstr6aDashboardReqDto> dtos = gson.fromJson(asJsonArray,
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

			for (Gstr6aDashboardReqDto dto : dtos) {

				String gstin = dto.getGstin();

				Integer fromPeriod = !StringUtils.isEmpty(dto.getFromPeriod())
						? GenUtil.convertTaxPeriodToInt(dto.getFromPeriod())
						: GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod());
				Integer toPeriod = !StringUtils.isEmpty(dto.getToPeriod())
						? GenUtil.convertTaxPeriodToInt(dto.getToPeriod())
						: GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod());

				/**
				 * Extra Logic to support month and finYear
				 */
				if (fromPeriod == null || toPeriod == null) {
					String finYr = dto.getFinYear();
					List<String> monthAsList = dto.getMonth();
					if (monthAsList != null && !monthAsList.isEmpty()) {

						for (String month : monthAsList) {
							String monthAsRetPeriod = getRetPeriod(month,
									finYr);
							taxPeriodList.add(monthAsRetPeriod);
						}

					} else if (!StringUtils.isEmpty(finYr)) {
						taxPeriodList.add("04" + finYr.substring(0, 4));
						taxPeriodList.add("01" + finYr.substring(0, 2)
								+ finYr.substring(5, 7));
					}

				}

				for (String taxPeriod : taxPeriodList) {

					int periodInt = GenUtil.convertTaxPeriodToInt(taxPeriod);

					if (taxPeriod.startsWith("13")) {
						periodInt += 88;
						taxPeriod = GenUtil
								.convertDerivedTaxPeriodToTaxPeriod(periodInt);
					}

					/**
					 * If taxPeriod is future period then continue next
					 * iteration;
					 */
					if (!GenUtil.isValidTaxPeriodForCurrentFy(taxPeriod)) {
						LOGGER.error(
								"Requested TaxPeriod {} is Future TaxPeriod, Hence skipping {} ",
								taxPeriod, APIConstants.GSTR6A.toUpperCase());
						continue;
					}

					Boolean isFailedGet = (dto.getIsFailed() == null) ? false
							: true;

					List<String> gstr6aSections = dto.getGstr6aSections();
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
							LOGGER.debug("GSTR6a Failed GET Request received");
						}
						List<String> failedStatus = new ArrayList<>();
						failedStatus.add(APIConstants.FAILED.toUpperCase());

						failedBatchs = batchRepo.findBatchByStatus(
								dto.getGstin(), taxPeriod,
								APIConstants.GSTR6A.toUpperCase(),
								failedStatus);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"GSTR6a Failed GET eligible sections are {} ",
									failedBatchs);
						}
					} else {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("GSTR6a All GET Request received");
						}
						List<String> initAndInProgressStatus = new ArrayList<>();
						initAndInProgressStatus
								.add(APIConstants.INITIATED.toUpperCase());
						initAndInProgressStatus.add(
								JobStatusConstants.IN_PROGRESS.toUpperCase());
						initAndInprogressBatchs = batchRepo.findBatchByStatus(
								dto.getGstin(), taxPeriod,
								APIConstants.GSTR6A.toUpperCase(),
								initAndInProgressStatus);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"GSTR6a All GET Not eligible sections are {} ",
									initAndInprogressBatchs);
						}
					}
					String userName = SecurityContext.getUser()
							.getUserPrincipalName();
					Long userRequestId = gstnUserRequestUtil
							.createGstnUserRequest(gstin, taxPeriod,
									APIConstants.GET,
									APIConstants.GSTR6A.toUpperCase(),
									groupCode, userName, false, false, false);
					dto.setUserRequestId(userRequestId);

					if (isEligible(isFailedGet, failedBatchs,
							initAndInprogressBatchs, APIConstants.B2B,
							gstr6aSections)) {
						dto.setType(APIConstants.B2B.toUpperCase());
						List<Gstr6aDashboardReqDto> createBatchAndSave = createBatchAndSave(
								groupCode, dto, taxPeriod);
						for (Gstr6aDashboardReqDto dto1 : createBatchAndSave) {
							jsonParam = gson.toJson(dto1);
							job = asyncJobsService.createJob(groupCode,
									JobConstants.GSTR6A_GSTN_GET_SECTION,
									jsonParam, userName, JobConstants.PRIORITY,
									JobConstants.PARENT_JOB_ID,
									JobConstants.SCHEDULE_AFTER_IN_MINS);
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug(
										"Job Created for Gstr6a Get as {} ",
										job);
							}
						}

					}
					if (isEligible(isFailedGet, failedBatchs,
							initAndInprogressBatchs, APIConstants.B2BA,
							gstr6aSections)) {
						dto.setType(APIConstants.B2BA.toUpperCase());
						List<Gstr6aDashboardReqDto> createBatchAndSave = createBatchAndSave(
								groupCode, dto, taxPeriod);
						for (Gstr6aDashboardReqDto dto1 : createBatchAndSave) {
							jsonParam = gson.toJson(dto1);
							job = asyncJobsService.createJob(groupCode,
									JobConstants.GSTR6A_GSTN_GET_SECTION,
									jsonParam, userName, JobConstants.PRIORITY,
									JobConstants.PARENT_JOB_ID,
									JobConstants.SCHEDULE_AFTER_IN_MINS);

							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug(
										"Job Created for Gstr6a Get as {} ",
										job);
							}
						}
					}
					if (isEligible(isFailedGet, failedBatchs,
							initAndInprogressBatchs, APIConstants.CDN,
							gstr6aSections)) {
						dto.setType(APIConstants.CDN.toUpperCase());
						List<Gstr6aDashboardReqDto> createBatchAndSave = createBatchAndSave(
								groupCode, dto, taxPeriod);
						for (Gstr6aDashboardReqDto dto1 : createBatchAndSave) {
							jsonParam = gson.toJson(dto1);
							job = asyncJobsService.createJob(groupCode,
									JobConstants.GSTR6A_GSTN_GET_SECTION,
									jsonParam, userName, JobConstants.PRIORITY,
									JobConstants.PARENT_JOB_ID,
									JobConstants.SCHEDULE_AFTER_IN_MINS);

							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug(
										"Job Created for Gstr6a Get as {} ",
										job);
							}
						}
					}
					if (isEligible(isFailedGet, failedBatchs,
							initAndInprogressBatchs, APIConstants.CDNA,
							gstr6aSections)) {
						dto.setType(APIConstants.CDNA.toUpperCase());
						List<Gstr6aDashboardReqDto> createBatchAndSave = createBatchAndSave(
								groupCode, dto, taxPeriod);
						for (Gstr6aDashboardReqDto dto1 : createBatchAndSave) {
							jsonParam = gson.toJson(dto1);
							job = asyncJobsService.createJob(groupCode,
									JobConstants.GSTR6A_GSTN_GET_SECTION,
									jsonParam, userName, JobConstants.PRIORITY,
									JobConstants.PARENT_JOB_ID,
									JobConstants.SCHEDULE_AFTER_IN_MINS);

							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug(
										"Job Created for Gstr6a Get as {} ",
										job);
							}
						}
					}

					JsonObject json = new JsonObject();
					json.addProperty("gstin", gstin);
					json.addProperty("msg",
							"Get GSTR6A  Request Initiated Successfully");
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
			String msg = "Unexpected error while creating async jobs for GSTR6A Get ";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private List<Gstr6aDashboardReqDto> createBatchAndSave(String groupCode,
			Gstr6aDashboardReqDto dto, String returnPeriod) {

		List<Gstr6aDashboardReqDto> returnDto = new ArrayList<>();
		TenantContext.setTenantId(groupCode);
		// InActiveting Previous Batch Records
		batchRepo.softlyDelete(dto.getType().toUpperCase(),
				APIConstants.GSTR6A.toUpperCase(), dto.getGstin(),
				returnPeriod);

		List<GetAnx1BatchEntity> batches = batchUtil
				.makeBatchGstr6aDashboard(dto, dto.getType(), returnPeriod);
		// Save new Batch
		for (GetAnx1BatchEntity batch : batches) {
			Gstr6aDashboardReqDto reqDto = new Gstr6aDashboardReqDto();
			reqDto.setGstin(dto.getGstin());
			reqDto.setMonth(dto.getMonth());
			reqDto.setFinYear(dto.getFinYear());
			reqDto.setGstr6aSections(dto.getGstr6aSections());
			reqDto.setGroupcode(dto.getGroupcode());
			reqDto.setApiSection(dto.getApiSection());
			reqDto.setType(dto.getType());
			reqDto.setUserRequestId(dto.getUserRequestId());
			GetAnx1BatchEntity batchEntity = batchRepo.save(batch);
			reqDto.setReturnPeriod(batchEntity.getTaxPeriod());
			reqDto.setBatchId(batchEntity.getId());
			returnDto.add(reqDto);
		}

		return returnDto;

	}

	private JsonArray getAllActiveGstnList(List<Gstr6aDashboardReqDto> dtos) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Getting all the active GSTNs for GSTR6a GET");
		}

		String msg = "";
		List<Gstr6aDashboardReqDto> inActiveGstinDtos = new ArrayList<>();
		JsonArray respBody = new JsonArray();
		if (dtos != null && !dtos.isEmpty()) {

			for (Gstr6aDashboardReqDto dto : dtos) {
				String gstin = dto.getGstin();
				JsonObject json = new JsonObject();
				String authStatus = authTokenService
						.getAuthTokenStatusForGstin(gstin);
				dto.getGstr6aSections().replaceAll(String::toUpperCase);
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
			List<String> gstr6aSections) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Checking eligibility for section: {}",
					section.toUpperCase());
			LOGGER.debug("isFailedGet: {}", isFailedGet);
			LOGGER.debug("Failed batches: {}", failedBatchs);
			LOGGER.debug("Init/InProgress batches: {}",
					initAndInprogressBatchs);
			LOGGER.debug("GSTR6A Sections: {}", gstr6aSections);
		}

		if ((isFailedGet == false
				&& !initAndInprogressBatchs.contains(section.toUpperCase())
				&& gstr6aSections.isEmpty())
				|| (isFailedGet == false
						&& !initAndInprogressBatchs
								.contains(section.toUpperCase())
						&& gstr6aSections.contains(section.toUpperCase()))
				|| (isFailedGet == true
						&& failedBatchs.contains(section.toUpperCase()))) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Inside true");
			}
			return true;
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Inside false");
			}
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
	}
}


