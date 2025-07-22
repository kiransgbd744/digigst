/**
 * 
 */
package com.ey.advisory.processors.test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.ErpScenarioPermissionEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioMasterRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.app.util.GstnUserRequestUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("GetGstr2aErpAsyncjobsInsertionProcessor")
public class GetGstr2aErpAsyncjobsInsertionProcessor
		extends DefaultMultiTenantTaskProcessor {

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
	
	/*@Autowired
	@Qualifier("groupInfoDetailsRepository")
	private GroupInfoDetailsRepository groupRepo;*/
	
	@Autowired
	private GSTNDetailRepository gstinRepo;
	
	@Autowired
	private ErpScenarioPermissionRepository erpScenPermissionRepo;
	
	@Autowired
	private ErpScenarioMasterRepository scenarioMasterRepo;
	

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		try {
			String groupCode = group.getGroupCode();
			//sp0023 - NALCO, other than NALCO dont execute this scenario
			/*if (!"sp0023".equals(groupCode)) {
				return;
			}*/
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2AErpGstnGetSection execution started for the groupcode {} ",
						groupCode);
			}
			TenantContext.setTenantId(groupCode);
			
			List<String> Gstins = gstinRepo.findByGroupCode(groupCode);
			
			Gson gson = GsonUtil.newSAPGsonInstance();
			
			// Gstins will contain only active GSTIN's and
			// respBody contains invalid GSTIN's list with messages
			JsonArray respBody = getAllActiveGstnList(Gstins);
			APIRespDto apiResp = null;
			if (respBody != null && !respBody.equals(new JsonArray())) {
				apiResp = APIRespDto.creatErrorResp();
			}
			
			
			List<Gstr1GetInvoicesReqDto> dtos = new ArrayList<>();
			List<String> allRetPeriods = getAllReturnPeriodsCurrentFinancialYear();
			Optional.ofNullable(Gstins).ifPresent(gstins -> {
				if (!gstins.isEmpty()) {
					gstins.forEach(gstin -> {
						GSTNDetailEntity gstinInfo = gstinRepo.findByGstinAndIsDeleteFalse(gstin);
						Long gstinId = gstinInfo != null ? gstinInfo.getId() : 0l;
						Long scenarioId = scenarioMasterRepo
								.findSceIdOnScenarioName(JobConstants.OLD_GSTR2A_GET_REV_INTG);
						// Assuming it as Background based job
						List<ErpScenarioPermissionEntity> scenarioList = erpScenPermissionRepo
								.findByScenarioIdAndGstinIdAndIsDeleteFalse(scenarioId, gstinId);
						if (!scenarioList.isEmpty()) {
							LOGGER.error(
									"Reverse Integration Scenario {} is not onboarded for the groupCode {} and gstin {} ",
									JobConstants.OLD_GSTR2A_GET_REV_INTG, groupCode, gstin);
							return;
						}

						allRetPeriods.forEach(retPeriod -> {
							Gstr1GetInvoicesReqDto childDto = new Gstr1GetInvoicesReqDto();
							childDto.setGstin(gstin);
							childDto.setReturnPeriod(retPeriod);
							dtos.add(childDto);
						});
					});
				}
			});
			
			
			String msg = "";
			String jsonParam = null;
			AsyncExecJob job = null;
			for (Gstr1GetInvoicesReqDto dto : dtos) {

				String gstin = dto.getGstin();
				String taxPeriod = dto.getReturnPeriod();
				/*Boolean isFailedGet = dto.getIsFailed() == null ? false
						: dto.getIsFailed();
				List<String> gstr2aSections = dto.getGstr2aSections();*/
				/*String groupCode = TenantContext.getTenantId();
				TenantContext.setTenantId(groupCode);*/
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
					LocalDate lastDayOfTaxPeriod = thisYearMonth.atEndOfMonth();
					// TaxPeriod is greater than GSTIN registration Date as per
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
						//continue;
						return ;
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
					//continue;
					return ;
				}

				//List<String> failedBatchs = new ArrayList<>();
				List<String> initAndInprogressBatchs = new ArrayList<>();
				// If request is for fetching only for Failed Sections
				/*if (isFailedGet == true) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("GSTR2A Failed GET Request received");
					}
					List<String> failedStatus = new ArrayList<>();
					failedStatus.add(APIConstants.FAILED.toUpperCase());

					failedBatchs = batchRepo.findBatchByStatus(dto.getGstin(),
							dto.getReturnPeriod(),
							APIConstants.GSTR2A_ERP.toUpperCase(), failedStatus);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"GSTR2A Failed GET eligible sections are {} ",
								failedBatchs);
					}
				} else {*/
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("GSTR2A All GET Request received");
					}
					List<String> initAndInProgressStatus = new ArrayList<>();
					initAndInProgressStatus
							.add(APIConstants.INITIATED.toUpperCase());
					initAndInProgressStatus
							.add(JobStatusConstants.IN_PROGRESS.toUpperCase());
					initAndInprogressBatchs = batchRepo.findBatchByStatus(
							dto.getGstin(), dto.getReturnPeriod(),
							APIConstants.GSTR2A_ERP.toUpperCase(),
							initAndInProgressStatus);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"GSTR2A All GET Not eligible sections are {} ",
								initAndInprogressBatchs);
					}
				//}
				Long userRequestId = gstnUserRequestUtil.createGstnUserRequest(
						gstin, taxPeriod, APIConstants.GET,
						APIConstants.GSTR2A_ERP.toUpperCase(), groupCode,
						JobConstants.SYSTEM.toUpperCase(), false, false, false);
				dto.setUserRequestId(userRequestId);
				dto.setApiSection(APIConstants.GSTR2A_ERP.toUpperCase());

				if (isEligible( 
						initAndInprogressBatchs, APIConstants.B2B)) {
					dto.setType(APIConstants.B2B.toUpperCase());
					dto = createBatchWithFromTimeAndSave(groupCode, dto);
					jsonParam = gson.toJson(dto);
					job = asyncJobsService.createJob(groupCode,
							JobConstants.GSTR2A_GSTN_GET_SECTION, jsonParam,
							JobConstants.SYSTEM.toUpperCase(), JobConstants.PRIORITY,
							JobConstants.PARENT_JOB_ID,
							JobConstants.SCHEDULE_AFTER_IN_MINS);

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Job Created for Gstr2A Get as {} ", job);
					}
					
				/*gstr2AJobHandler.gstr2AGstnGetCall(jsonParam, groupCode);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Gstr2A Get Gstn Processed with args {} ",
							jsonParam);
				}*/
				}
				if (isEligible( 
						initAndInprogressBatchs, APIConstants.B2BA)) {
					dto.setType(APIConstants.B2BA.toUpperCase());
					dto = createBatchWithFromTimeAndSave(groupCode, dto);
					jsonParam = gson.toJson(dto);
					job = asyncJobsService.createJob(groupCode,
							JobConstants.GSTR2A_GSTN_GET_SECTION, jsonParam,
							JobConstants.SYSTEM.toUpperCase(), JobConstants.PRIORITY,
							JobConstants.PARENT_JOB_ID,
							JobConstants.SCHEDULE_AFTER_IN_MINS);

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Job Created for Gstr2A Get as {} ", job);
					}
					/*gstr2AJobHandler.gstr2AGstnGetCall(jsonParam, groupCode);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Gstr2A Get Gstn Processed with args {} ",
								jsonParam);
					}*/
				}
				if (isEligible(
						initAndInprogressBatchs, APIConstants.CDN)) {
					dto.setType(APIConstants.CDN.toUpperCase());
					dto = createBatchWithFromTimeAndSave(groupCode, dto);
					jsonParam = gson.toJson(dto);
					job = asyncJobsService.createJob(groupCode,
							JobConstants.GSTR2A_GSTN_GET_SECTION, jsonParam,
							JobConstants.SYSTEM.toUpperCase(), JobConstants.PRIORITY,
							JobConstants.PARENT_JOB_ID,
							JobConstants.SCHEDULE_AFTER_IN_MINS);

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Job Created for Gstr2A Get as {} ", job);
					}
					/*gstr2AJobHandler.gstr2AGstnGetCall(jsonParam, groupCode);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Gstr2A Get Gstn Processed with args {} ",
								jsonParam);
					}*/
				}
				if (isEligible(
						initAndInprogressBatchs, APIConstants.CDNA)) {
					dto.setType(APIConstants.CDNA.toUpperCase());
					dto = createBatchWithFromTimeAndSave(groupCode, dto);
					jsonParam = gson.toJson(dto);
					job = asyncJobsService.createJob(groupCode,
							JobConstants.GSTR2A_GSTN_GET_SECTION, jsonParam,
							JobConstants.SYSTEM.toUpperCase(), JobConstants.PRIORITY,
							JobConstants.PARENT_JOB_ID,
							JobConstants.SCHEDULE_AFTER_IN_MINS);

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Job Created for Gstr2A Get as {} ", job);
					}
					/*gstr2AJobHandler.gstr2AGstnGetCall(jsonParam, groupCode);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Gstr2A Get Gstn Processed with args {} ",
								jsonParam);
					}*/
				}

				if (isEligible(
						initAndInprogressBatchs, APIConstants.ISD)) {
					dto.setType(APIConstants.ISD.toUpperCase());
					dto = createBatchWithFromTimeAndSave(groupCode, dto);
					jsonParam = gson.toJson(dto);
					job = asyncJobsService.createJob(groupCode,
							JobConstants.GSTR2A_GSTN_GET_SECTION, jsonParam,
							JobConstants.SYSTEM.toUpperCase(), JobConstants.PRIORITY,
							JobConstants.PARENT_JOB_ID,
							JobConstants.SCHEDULE_AFTER_IN_MINS);

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Job Created for Gstr2A Get as {} ", job);
					}
					/*gstr2AJobHandler.gstr2AGstnGetCall(jsonParam, groupCode);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Gstr2A Get Gstn Processed with args {} ",
								jsonParam);
					}*/
				}
				/*if (isEligible(
						initAndInprogressBatchs, APIConstants.ISDA)) {
					dto.setType(APIConstants.ISDA.toUpperCase());
					dto = createBatchWithFromTimeAndSave(groupCode, dto);
					jsonParam = gson.toJson(dto);
					job = asyncJobsService.createJob(groupCode,
							JobConstants.GSTR2A_GSTN_GET_SECTION, jsonParam,
							JobConstants.SYSTEM.toUpperCase(), JobConstants.PRIORITY,
							JobConstants.PARENT_JOB_ID,
							JobConstants.SCHEDULE_AFTER_IN_MINS);

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Job Created for Gstr2A Get as {} ", job);
					}
					gstr2AJobHandler.gstr2AGstnGetCall(jsonParam, groupCode);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Gstr2A Get Gstn Processed with args {} ",
								jsonParam);
					}
				}*/
				/*
				 * if (isEligible(isFailedGet,
				 * failedBatchs,initAndInprogressBatchs,APIConstants.TDS,
				 * gstr2aSections)) {
				 * dto.setType(APIConstants.TDS.toUpperCase()); dto =
				 * createBatchAndSave(groupCode, dto); jsonParam =
				 * gson.toJson(dto); job = asyncJobsService.createJob(groupCode,
				 * JobConstants.GSTR2A_GSTN_GET_SECTION, jsonParam,
				 * JobConstants.SYSTEM.toUpperCase(), JobConstants.PRIORITY,
				 * JobConstants.PARENT_JOB_ID,
				 * JobConstants.SCHEDULE_AFTER_IN_MINS);
				 * 
				 * if(LOGGER.isDebugEnabled()) {
				 * LOGGER.debug("Job Created for Gstr2A Get as {} ", job); } }
				 * if (isEligible(isFailedGet,
				 * failedBatchs,initAndInprogressBatchs,APIConstants.TDSA,
				 * gstr2aSections)) {
				 * dto.setType(APIConstants.TDSA.toUpperCase()); dto =
				 * createBatchAndSave(groupCode, dto); jsonParam =
				 * gson.toJson(dto); job = asyncJobsService.createJob(groupCode,
				 * JobConstants.GSTR2A_GSTN_GET_SECTION, jsonParam,
				 * JobConstants.SYSTEM.toUpperCase(), JobConstants.PRIORITY,
				 * JobConstants.PARENT_JOB_ID,
				 * JobConstants.SCHEDULE_AFTER_IN_MINS);
				 * 
				 * if(LOGGER.isDebugEnabled()) {
				 * LOGGER.debug("Job Created for Gstr2A Get as {} ", job); } }
				 * if (isEligible(isFailedGet,
				 * failedBatchs,initAndInprogressBatchs,APIConstants.TCS,
				 * gstr2aSections)) {
				 * dto.setType(APIConstants.TCS.toUpperCase()); dto =
				 * createBatchAndSave(groupCode, dto); jsonParam =
				 * gson.toJson(dto); job = asyncJobsService.createJob(groupCode,
				 * JobConstants.GSTR2A_GSTN_GET_SECTION, jsonParam,
				 * JobConstants.SYSTEM.toUpperCase(), JobConstants.PRIORITY,
				 * JobConstants.PARENT_JOB_ID,
				 * JobConstants.SCHEDULE_AFTER_IN_MINS);
				 * 
				 * if(LOGGER.isDebugEnabled()) {
				 * LOGGER.debug("Job Created for Gstr2A Get as {} ", job); } }
				 */
				JsonObject json = new JsonObject();
				json.addProperty("gstin", gstin);
				json.addProperty("msg",
						"Get GSTR2A  Request initiated Successfully");
				respBody.add(json);

			}
			if (apiResp == null) {
				apiResp = APIRespDto.createSuccessResp();
			}
			/*resp.add("hdr", gson.toJsonTree(apiResp));
			resp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);*/
		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents";
			throw new AppException(msg, ex);
			/*resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);*/
		}
	}

	private Gstr1GetInvoicesReqDto createBatchWithFromTimeAndSave(String groupCode,
			Gstr1GetInvoicesReqDto dto) {
		
		List<String> status = new ArrayList<>();
		status.add(APIConstants.SUCCESS.toUpperCase());
		status.add(APIConstants.SUCCESS_WITH_NO_DATA.toUpperCase());

		TenantContext.setTenantId(groupCode);
		Object objArray = batchRepo.findLastSuccessDate(
				APIConstants.GSTR2A_ERP.toUpperCase(), dto.getGstin(),
				dto.getReturnPeriod(), dto.getType().toUpperCase(), status);
		String lastExecutedOn = objArray != null ? String.valueOf(objArray)
					: null;
		String	fromTime = null;
		if (lastExecutedOn != null) {
			LocalDate locDate = LocalDate.parse(lastExecutedOn.substring(0, 10),
					DateUtil.SUPPORTED_DATE_FORMAT1);
			 fromTime = locDate.format(DateUtil.SUPPORTED_DATE_FORMAT2);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Last GSTR2A ERP GET call fromTime is {} for the section {} ",
					fromTime, dto.getType().toUpperCase());
		}
		
		/*if (fromTime != null) {
			
			// InActiveting Previous Batch Records only if older GET was happend
			batchRepo.softlyDeleteByFromTime(dto.getType().toUpperCase(),
					APIConstants.GSTR2A_ERP.toUpperCase(), dto.getGstin(),
					dto.getReturnPeriod(),
					DateUtil.stringToTime(fromTime, DateUtil.DATE_FORMAT1));
		}*/
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Current GSTR2A ERP GET call fromTime is {} for the section {} ",
					fromTime, dto.getType().toUpperCase());
		}
		//setting FromTime
		dto.setFromTime(fromTime);
		GetAnx1BatchEntity batch = batchUtil.makeBatchGstr1(dto, dto.getType(),
				APIConstants.GSTR2A_ERP.toUpperCase());
		// Save new Batch
		batch = batchRepo.save(batch);
		dto.setBatchId(batch.getId());
		return dto;

	}

	private JsonArray getAllActiveGstnList(
			List<String> gstins) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Getting all the active GSTNs for GSTR2A ERP GET");
		}

		String msg = "";
		List<String> inActiveGstins = new ArrayList<>();
		JsonArray respBody = new JsonArray();
		if (gstins != null) {

			for (String gstin : gstins) {
				// String gstin = dto.getGstin();
				JsonObject json = new JsonObject();
				String authStatus = authTokenService
						.getAuthTokenStatusForGstin(gstin);
				// dto.getGstr2aSections().replaceAll(String::toUpperCase);
				if (!"A".equalsIgnoreCase(authStatus)) {

					inActiveGstins.add(gstin);
					msg = "Auth Token is Inactive, Please Activate";
					json.addProperty("gstin", gstin);
					json.addProperty("msg", msg);
					respBody.add(json);

				}
			}
			gstins.removeAll(inActiveGstins);
		}
		return respBody;
	}

	// Check weather the section is eligible to create new job or not.
	private Boolean isEligible(List<String> initAndInprogressBatchs,
			String section) {

		if (!initAndInprogressBatchs.contains(section.toUpperCase())) {
			return true;
		} else {
			return false;
		}
	}

	private List<String> getAllReturnPeriodsCurrentFinancialYear(){
		
		List<String> retPeriods = new ArrayList<>();
		LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
		
		//LocalDateTime istDateTimeFromUTC = EYDateUtil.toISTDateTimeFromUTC(now);
		String nowString = String.valueOf(now);
		Integer month = Integer.valueOf(nowString.substring(5, 7));
		
		
		if (4 > month) {

			String previous = String.valueOf(now.minusYears(1));

			String retPeriod04 = "04".concat(previous.substring(0, 4));
			String retPeriod05 = "05".concat(previous.substring(0, 4));
			String retPeriod06 = "06".concat(previous.substring(0, 4));
			String retPeriod07 = "07".concat(previous.substring(0, 4));
			String retPeriod08 = "08".concat(previous.substring(0, 4));
			String retPeriod09 = "09".concat(previous.substring(0, 4));
			String retPeriod10 = "10".concat(previous.substring(0, 4));
			String retPeriod11 = "11".concat(previous.substring(0, 4));
			String retPeriod12 = "12".concat(previous.substring(0, 4));

			retPeriods.add(retPeriod04);
			retPeriods.add(retPeriod05);
			retPeriods.add(retPeriod06);
			retPeriods.add(retPeriod07);
			retPeriods.add(retPeriod08);
			retPeriods.add(retPeriod09);
			retPeriods.add(retPeriod10);
			retPeriods.add(retPeriod11);
			retPeriods.add(retPeriod12);

			if (1 >= month) {
				String retPeriod01 = "01".concat(nowString.substring(0, 4));
				retPeriods.add(retPeriod01);
			}
			if (2 >= month) {
				String retPeriod02 = "02".concat(nowString.substring(0, 4));
				retPeriods.add(retPeriod02);
			}
			if (3 >= month) {
				String retPeriod03 = "03".concat(nowString.substring(0, 4));
				retPeriods.add(retPeriod03);
			}
			

		} else if (4 < month) {

			if (4 <= month) {
				String retPeriod04 = "04".concat(nowString.substring(0, 4));
				retPeriods.add(retPeriod04);
			}
			if (5 <= month) {
				String retPeriod05 = "05".concat(nowString.substring(0, 4));
				retPeriods.add(retPeriod05);
			}
			if (6 <= month) {
				String retPeriod06 = "06".concat(nowString.substring(0, 4));
				retPeriods.add(retPeriod06);
			}
			if (7 <= month) {
				String retPeriod07 = "07".concat(nowString.substring(0, 4));
				retPeriods.add(retPeriod07);
			}
			if (8 <= month) {
				String retPeriod08 = "08".concat(nowString.substring(0, 4));
				retPeriods.add(retPeriod08);
			}
			if (9 <= month) {
				String retPeriod09 = "09".concat(nowString.substring(0, 4));
				retPeriods.add(retPeriod09);
			}
			if (10 <= month) {
				String retPeriod10 = "10".concat(nowString.substring(0, 4));
				retPeriods.add(retPeriod10);
			}
			if (11 <= month) {
				String retPeriod11 = "11".concat(nowString.substring(0, 4));
				retPeriods.add(retPeriod11);
			}
			if (12 <= month) {
				String retPeriod12 = "12".concat(nowString.substring(0, 4));
				retPeriods.add(retPeriod12);
			}

		} else {
			String retPeriod = nowString.substring(5, 7).concat(nowString.substring(0, 4));
			retPeriods.add(retPeriod);
		}
		return retPeriods;
		
	}
	
}
