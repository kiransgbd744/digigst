package com.ey.advisory.sap.controller;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.YearMonth;
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

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.app.util.GstnUserRequestUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr7GetInvoicesReqDto;
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
public class Gstr7GetGstnInvoicesTestController {
	

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

	@PostMapping(value = "/ui/Gstr7GstnGetSectionTest", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> createGstr7GstnGetJob(@RequestBody String request) {

		JsonObject resp = new JsonObject();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr7GstnGetSection Request received from UI as {} ", request);
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject requestObject = (new JsonParser()).parse(request).getAsJsonObject();
			JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();
			Type listType = new TypeToken<List<Gstr7GetInvoicesReqDto>>() {
			}.getType();
			List<Gstr7GetInvoicesReqDto> dtos = gson.fromJson(asJsonArray, listType);
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
			for (Gstr7GetInvoicesReqDto dto : dtos) {

				String gstin = dto.getGstin();
				String taxPeriod = dto.getReturnPeriod();
				Boolean isFailedGet = dto.getIsFailed();
				List<String> gstr6Sections = dto.getGstr7Sections();
				String groupCode = TenantContext.getTenantId();
				TenantContext.setTenantId(groupCode);
				// Get the Registration date of the GSTIN from onboarding.
				GSTNDetailEntity gstinInfo = ehcachegstin.getGstinInfo(groupCode, gstin);

				// LocalDate gstinRegDate =
				// gstinRepo.findRegistraionDate(gstin);
				LocalDate regDate = gstinInfo.getRegDate();
				if (regDate != null) {
					// Creates a YearMonth object
					YearMonth thisYearMonth = YearMonth.of(Integer.valueOf(taxPeriod.substring(2, 6)),
							Integer.valueOf(taxPeriod.substring(0, 2)));
					// Last day of the taxperiod
					LocalDate lastDayOfTaxPeriod = thisYearMonth.atEndOfMonth();
					// TaxPeriod is greater than GSTIN registration Date as per
					// onboarding.
					if (lastDayOfTaxPeriod.compareTo(regDate) >= 0) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Registation date greater than ReturnPeriod for {} ", gstin);
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
						LOGGER.debug("GSTR7 Failed GET Request received");
					}
					List<String> failedStatus = new ArrayList<>();
					failedStatus.add(APIConstants.FAILED.toUpperCase());

					failedBatchs = batchRepo.findBatchByStatus(dto.getGstin(), dto.getReturnPeriod(),
							APIConstants.GSTR7.toUpperCase(), failedStatus);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("GSTR7 Failed GET eligible sections are {} ", failedBatchs);
					}
				} else {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("GSTR7 All GET Request received");
					}
					List<String> initAndInProgressStatus = new ArrayList<>();
					initAndInProgressStatus.add(APIConstants.INITIATED.toUpperCase());
					initAndInProgressStatus.add(JobStatusConstants.IN_PROGRESS.toUpperCase());
					initAndInprogressBatchs = batchRepo.findBatchByStatus(dto.getGstin(), dto.getReturnPeriod(),
							APIConstants.GSTR7.toUpperCase(), initAndInProgressStatus);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("GSTR7 All GET Not eligible sections are {} ", initAndInProgressStatus);
					}
				}

				Long userRequestId = gstnUserRequestUtil.createGstnUserRequest(gstin, taxPeriod, APIConstants.GET,
						APIConstants.GSTR7.toUpperCase(), groupCode, JobConstants.SYSTEM.toUpperCase(), false, false, false);
				dto.setUserRequestId(userRequestId);

				if (isEligible(isFailedGet, failedBatchs, initAndInprogressBatchs, APIConstants.TDS, gstr6Sections)) {
					dto.setType(APIConstants.TDS.toUpperCase());
					dto = createBatchAndSave(groupCode, dto);
					jsonParam = gson.toJson(dto);
					job = asyncJobsService.createJob(groupCode, JobConstants.GSTR7_GSTN_GET_SECTION, jsonParam,
							JobConstants.SYSTEM, JobConstants.PRIORITY, JobConstants.PARENT_JOB_ID,
							JobConstants.SCHEDULE_AFTER_IN_MINS);

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Job Created for Gstr7 Get as {} ", job);
					}
				}

				JsonObject json = new JsonObject();
				json.addProperty("gstin", gstin);
				json.addProperty("msg", "Get GSTR7  Request initiated Successfully");
				respBody.add(json);

			}
			if (apiResp == null) {
				apiResp = APIRespDto.createSuccessResp();
			}
			resp.add("hdr", gson.toJsonTree(apiResp));
			resp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private Gstr7GetInvoicesReqDto createBatchAndSave(String groupCode, Gstr7GetInvoicesReqDto dto) {

		TenantContext.setTenantId(groupCode);
		// InActiveting Previous Batch Records
		batchRepo.softlyDelete(dto.getType().toUpperCase(), APIConstants.GSTR7.toUpperCase(), dto.getGstin(),
				dto.getReturnPeriod());
		GetAnx1BatchEntity batch = batchUtil.makeBatchGstr7(dto, dto.getType());
		// Save new Batch
		batch = batchRepo.save(batch);
		dto.setBatchId(batch.getId());
		return dto;

	}

	private JsonArray getAllActiveGstnList(List<Gstr7GetInvoicesReqDto> dtos) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Getting all the active GSTNs for GSTR7 GET");
		}

		String msg = "";
		List<Gstr7GetInvoicesReqDto> inActiveGstinDtos = new ArrayList<>();
		JsonArray respBody = new JsonArray();
		if (dtos != null && !dtos.isEmpty()) {

			for (Gstr7GetInvoicesReqDto dto : dtos) {
				String gstin = dto.getGstin();
				JsonObject json = new JsonObject();
				String authStatus = authTokenService.getAuthTokenStatusForGstin(gstin);
				dto.getGstr7Sections().replaceAll(String::toUpperCase);
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
	private Boolean isEligible(Boolean isFailedGet, List<String> failedBatchs, List<String> initAndInprogressBatchs,
			String section, List<String> gstr7Sections) {

		if ((isFailedGet == false && !initAndInprogressBatchs.contains(section.toUpperCase())
				&& gstr7Sections.isEmpty())
				|| (isFailedGet == false && !initAndInprogressBatchs.contains(section.toUpperCase())
						&& gstr7Sections.contains(section.toUpperCase()))
				|| (isFailedGet == true && failedBatchs.contains(section.toUpperCase()))) {
			return true;
		} else {
			return false;
		}
	}
}



