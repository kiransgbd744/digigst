/**
 * 
 */
package com.ey.advisory.app.services.jobs.gstr2a;

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

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
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
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.GSTNAPIUtil;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.gstnapi.GstinGetStatusService;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */

@Service("Gstr2aGetCallHandler")
@Slf4j
public class Gstr2aGetCallHandler {

	private static final List<String> GET2A_SECTIONS = ImmutableList.of(
			APIConstants.B2B, APIConstants.B2BA, APIConstants.CDN,
			APIConstants.CDNA, APIConstants.ISD, APIConstants.IMPG,
			APIConstants.IMPGSEZ, APIConstants.ECOM, APIConstants.ECOMA);

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
	@Qualifier("GstinGetStatusServiceImpl")
	private GstinGetStatusService gstinGetStatusService;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gSTNDetailRepository;

	@Transactional(value = "clientTransactionManager")
	public ResponseEntity<String> getCallGSTR2A(
			List<Gstr1GetInvoicesReqDto> dtos, List<AsyncExecJob> jobList) {

		long dbLoadStTime = System.currentTimeMillis();

		// Dto will contain only active GSTIN's and
		// respBody contains invalid GSTIN's list with messages
		JsonArray respBody = getAllActiveGstnList(dtos);
		long dbLoadEndTime = System.currentTimeMillis();
		long dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);

		LOGGER.error("time taken for getting active gstin list {} ",
				dbLoadTimeDiff);
		dbLoadStTime = System.currentTimeMillis();

		JsonObject resp = new JsonObject();
		List<String> taxPeriodList = new ArrayList<>();
		try {
			Gson gson = GsonUtil.newSAPGsonInstance();
			APIRespDto apiResp = null;

			if (respBody != null && !respBody.equals(new JsonArray())) {
				apiResp = APIRespDto.creatErrorResp();
			}
			String msg = "";
			String jsonParam = null;
			AsyncExecJob job = null;
			boolean isFDeltaGetData = false;

			dbLoadStTime = System.currentTimeMillis();

			if (!dtos.isEmpty()) {
				EntityConfigPrmtEntity entityConfig = entityConfigRepo
						.findByGroupCodeAndEntityIdAndparamkryIdAndIsDeleteFalse(
								TenantContext.getTenantId(),
								gSTNDetailRepository.findEntityIdByGstin(
										dtos.get(0).getGstin()),
								"I25");
				String paramValue = entityConfig != null
						? entityConfig.getParamValue() : "A";
				if ("B".equals(paramValue)) {
					isFDeltaGetData = true;
				}
			}
			dbLoadEndTime = System.currentTimeMillis();
			dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);

			LOGGER.error("time taken for fetching onboarding entry {} ",
					dbLoadTimeDiff);

			for (Gstr1GetInvoicesReqDto dto : dtos) {

				dbLoadStTime = System.currentTimeMillis();
				/**
				 * Extra Logic to support fromTime/toTime
				 */
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

				boolean isAllsectionsGetInProgress = true;
				for (String taxPeriod : taxPeriodList) {

					int periodInt = GenUtil.convertTaxPeriodToInt(taxPeriod);

					dbLoadStTime = System.currentTimeMillis();
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
					dbLoadEndTime = System.currentTimeMillis();
					dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);

					LOGGER.error("time taken for fetching taxperiod entry {} ",
							dbLoadTimeDiff);
					dbLoadStTime = System.currentTimeMillis();
					// no need to do each time
					GSTNDetailEntity gstinInfo = ehcachegstin
							.getGstinInfo(groupCode, gstin);
					dbLoadEndTime = System.currentTimeMillis();
					dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);

					LOGGER.error("time taken for gstinInfo {} ",
							dbLoadTimeDiff);
					dbLoadStTime = System.currentTimeMillis();
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

						dbLoadEndTime = System.currentTimeMillis();
						dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);

						LOGGER.error("time taken for Registration {} ",
								dbLoadTimeDiff);

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

						dbLoadStTime = System.currentTimeMillis();

						failedBatchs = batchRepo.findBatchByStatus(
								dto.getGstin(), dto.getReturnPeriod(),
								APIConstants.GSTR2A.toUpperCase(),
								failedStatus);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"GSTR2A Failed GET eligible sections are {} ",
									failedBatchs);
						}
						dbLoadEndTime = System.currentTimeMillis();
						dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);

						LOGGER.error(
								"time taken for fetching failed batches for taxperiod {} ",
								dbLoadTimeDiff);

					} else {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("GSTR2A All GET Request received");
						}
						List<String> initAndInProgressStatus = new ArrayList<>();
						initAndInProgressStatus
								.add(APIConstants.INITIATED.toUpperCase());
						initAndInProgressStatus.add(
								JobStatusConstants.IN_PROGRESS.toUpperCase());

						dbLoadStTime = System.currentTimeMillis();

						initAndInprogressBatchs = batchRepo.findBatchByStatus(
								dto.getGstin(), dto.getReturnPeriod(),
								APIConstants.GSTR2A.toUpperCase(),
								initAndInProgressStatus);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"GSTR2A All GET Not eligible sections are {} ",
									initAndInprogressBatchs);
						}
						dbLoadEndTime = System.currentTimeMillis();
						dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);

						LOGGER.error(
								"time taken {} for fetching initiated or inprogress "
										+ " batches for taxperiod {} ",
								dbLoadTimeDiff, dto.getReturnPeriod());

					}
					String userName = SecurityContext.getUser()
							.getUserPrincipalName();

					dbLoadStTime = System.currentTimeMillis();

					Long userRequestId = gstnUserRequestUtil
							.createGstnUserRequest(gstin, taxPeriod,
									APIConstants.GET,
									APIConstants.GSTR2A.toUpperCase(),
									groupCode, userName, false, false, false);

					dbLoadEndTime = System.currentTimeMillis();
					dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);

					LOGGER.error(
							"time taken {} for creating gstn user request  "
									+ " for taxperiod {} ",
							dbLoadTimeDiff, dto.getReturnPeriod());

					dto.setUserRequestId(userRequestId);
					dto.setApiSection(APIConstants.GSTR2A.toUpperCase());

					/** set isDeltaGet status **/
					dto.setIsDeltaGet(isFDeltaGetData);
					LOGGER.debug("Dto :: ", dto.toString());
					for (String get2aSection : GET2A_SECTIONS) {

						dbLoadStTime = System.currentTimeMillis();

						if (isEligible(isFailedGet, failedBatchs,
								initAndInprogressBatchs, get2aSection,
								gstr2aSections)) {
							dbLoadEndTime = System.currentTimeMillis();
							dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);

							LOGGER.error(
									"time taken {} for isEligible  "
											+ " for taxperiod {} ",
									dbLoadTimeDiff, dto.getReturnPeriod());

							isAllsectionsGetInProgress = false;
							dto.setType(get2aSection.toUpperCase());
							dbLoadStTime = System.currentTimeMillis();

							dto = createBatchAndSave(groupCode, dto);

							dbLoadEndTime = System.currentTimeMillis();
							dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);

							LOGGER.error(
									"time taken {} for createBatchAndSave  "
											+ " for taxperiod {} ",
									dbLoadTimeDiff, dto.getReturnPeriod());

							LOGGER.debug("Dto after creating batch:: {}",
									dto.toString());
							jsonParam = gson.toJson(dto);
							APIParam param1 = new APIParam("gstin",
									dto.getGstin());
							APIParam param2 = new APIParam("ret_period",
									taxPeriod);

							APIParams params = new APIParams(groupCode,
									APIProviderEnum.GSTN,
									GSTNAPIUtil.gstr2AGetAPIIdentifier
											.get(get2aSection.toUpperCase()),
									param1, param2);
							JsonElement apiParams = gson.toJsonTree(params);

							dbLoadStTime = System.currentTimeMillis();

							gstinGetStatusService.saveOrUpdateGSTNGetStatus(
									apiParams.toString(), "INITIATED", null);

							jobList.add(asyncJobsService.createJobAndReturn(
									groupCode,
									JobConstants.GSTR2A_GSTN_GET_SECTION,
									jsonParam, userName, JobConstants.PRIORITY,
									JobConstants.PARENT_JOB_ID,
									JobConstants.SCHEDULE_AFTER_IN_MINS));
							dbLoadEndTime = System.currentTimeMillis();
							dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);

							LOGGER.error(
									"time taken {} for saveOrUpdateGSTNGetStatus  "
											+ " and job addition {} ",
									dbLoadTimeDiff, dto.getReturnPeriod());

							LOGGER.debug("Dto after creating jsonparams:: {}",
									jsonParam.toString());
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
			LOGGER.debug("Getting all the active GSTNs for GSTR2 GET");
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
	}

}
