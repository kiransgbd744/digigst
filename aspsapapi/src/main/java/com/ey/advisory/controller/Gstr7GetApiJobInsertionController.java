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

import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.app.util.GstnUserRequestUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr7GetInvoicesReqDto;
import com.ey.advisory.gstnapi.GstinGetStatusService;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
public class Gstr7GetApiJobInsertionController {

	private static final List<String> listOfStatus = ImmutableList.of(
			APIConstants.INITIATED.toUpperCase(),
			JobStatusConstants.IN_PROGRESS.toUpperCase());

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

	@PostMapping(value = "/ui/Gstr7GstnGetSection", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> createGstr7GstnGetJob(
			@RequestBody String request) {

		JsonObject resp = new JsonObject();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr7GstnGetSection Request received from UI as {} ",
						request);
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject requestObject = (new JsonParser()).parse(request)
					.getAsJsonObject();
			JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();
			Type listType = new TypeToken<List<Gstr7GetInvoicesReqDto>>() {
			}.getType();
			List<Gstr7GetInvoicesReqDto> dtos = gson.fromJson(asJsonArray,
					listType);
			return createGstr7Gstn(dtos, false);
		} catch (Exception ex) {
			String msg = "Unexpected error while creating async jobs for GSTR7 Get ";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<String> createGstr7Gstn(
			List<Gstr7GetInvoicesReqDto> dtos, boolean isTransactional) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		String userName = SecurityContext.getUser().getUserPrincipalName();
		try {
			// Dto will contain only active GSTIN's and
			// respBody contains invalid GSTIN's list with messages
			JsonArray respBody = getAllActiveGstnList(dtos);
			APIRespDto apiResp = null;
			if (respBody != null && !respBody.equals(new JsonArray())) {
				apiResp = APIRespDto.creatErrorResp();
			}
			String jsonParam = null;
			AsyncExecJob job = null;
			for (Gstr7GetInvoicesReqDto dto : dtos) {
				String gstin = dto.getGstin();
				String taxPeriod = dto.getReturnPeriod();
				String groupCode = TenantContext.getTenantId();
				List<String> initAndInprogressBatchs = new ArrayList<>();

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("GSTR7 All GET Request received");
				}
				initAndInprogressBatchs = batchRepo.findBatchByStatus(
						dto.getGstin(), dto.getReturnPeriod(),
						APIConstants.GSTR7.toUpperCase(), listOfStatus);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("GSTR7 All GET Not eligible sections are {} ",
							listOfStatus);
				}

				if (isEligible(initAndInprogressBatchs)) {
					Long userRequestId = gstnUserRequestUtil
							.createGstnUserRequest(gstin, taxPeriod,
									APIConstants.GET,
									APIConstants.GSTR7.toUpperCase(), groupCode,
									userName, false, false, false);
					dto.setUserRequestId(userRequestId);
					
					dto.setType(isTransactional 
						    ? APIConstants.TRANSACTIONAL.toUpperCase() 
						    : APIConstants.TDS.toUpperCase());

					dto = createBatchAndSave(groupCode, dto);
					jsonParam = gson.toJson(dto);
					job = asyncJobsService.createJob(groupCode,
							JobConstants.GSTR7_GSTN_GET_SECTION, jsonParam,
							userName, JobConstants.PRIORITY,
							JobConstants.PARENT_JOB_ID,
							JobConstants.SCHEDULE_AFTER_IN_MINS);

					initateGetCallStatus(gstin, taxPeriod, groupCode, gson);

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Job Created for Gstr7 Get as {} ", job);
					}

				}
				JsonObject json = new JsonObject();
				json.addProperty("gstin", gstin);
				json.addProperty("msg",
						"Get GSTR7  Request Initiated Successfully");
				respBody.add(json);
			}
			if (apiResp == null) {
				apiResp = APIRespDto.createSuccessResp();
			}
			resp.add("hdr", gson.toJsonTree(apiResp));
			resp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			String msg = "Unexpected error while creating async jobs for GSTR7 Get ";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	private Gstr7GetInvoicesReqDto createBatchAndSave(String groupCode,
			Gstr7GetInvoicesReqDto dto) {

		TenantContext.setTenantId(groupCode);
		// InActiveting Previous Batch Records
		batchRepo.softlyDelete(dto.getType().toUpperCase(),
				APIConstants.GSTR7.toUpperCase(), dto.getGstin(),
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
				String authStatus = authTokenService
						.getAuthTokenStatusForGstin(gstin);
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
	private Boolean isEligible(List<String> initAndInprogressBatchs) {

		return initAndInprogressBatchs.isEmpty();
	}

	private void initateGetCallStatus(String gstin, String retPeriod,
			String groupCode, Gson gson) {
		APIParam param1 = new APIParam("gstin", gstin);
		APIParam param2 = new APIParam("ret_period", retPeriod);

		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.GSTR7_GET_TDS, param1, param2);
		JsonElement apiParams = gson.toJsonTree(params);

		gstinGetStatusService.saveOrUpdateGSTNGetStatus(apiParams.toString(),
				"INITIATED", null);
	}
}
