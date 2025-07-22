package com.ey.advisory.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.ey.advisory.common.GenUtil;
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
import com.ey.advisory.core.dto.Itc04GetInvoicesReqDto;
import com.ey.advisory.gstnapi.GstinGetStatusService;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
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
public class Itc04GetApiJobInsertionController {
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

	@PostMapping(value = "/ui/Itc04GstnGetSection", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> createItc04GstnGetJob(
			@RequestBody String request) {

		JsonObject resp = new JsonObject();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Itc04GstnGetSection Request received from UI as {} ",
						request);
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject requestObject = (new JsonParser()).parse(request)
					.getAsJsonObject();
			JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();
			Type listType = new TypeToken<List<Itc04GetInvoicesReqDto>>() {
			}.getType();
			List<Itc04GetInvoicesReqDto> dtos = gson.fromJson(asJsonArray,
					listType);

			return createItc04Gstn(dtos);
		} catch (Exception ex) {
			String msg = "Unexpected error while creating async jobs for ITC04 GET";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<String> createItc04Gstn(
			List<Itc04GetInvoicesReqDto> dtos) {
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
			Set<String> uniqueGstins = new HashSet<>();
			for (Itc04GetInvoicesReqDto dto : dtos) {

				String gstin = dto.getGstin();
				String taxPeriod = dto.getReturnPeriod();

				/**
				 * If taxPeriod is future period then continue next iteration;
				 */
				if (!GenUtil.isValidQuarterForCurrentFy(taxPeriod)) {
					LOGGER.error(
							"Requested TaxPeriod {} is Future TaxPeriod, Hence skipping {} ",
							taxPeriod, APIConstants.ITC04.toUpperCase());

					if (apiResp == null) {
						apiResp = APIRespDto.creatErrorResp();
					}
					JsonObject json = new JsonObject();
					msg = "Requested Future Quarter is not valid";
					json.addProperty("gstin", gstin);
					json.addProperty("msg", msg);
					respBody.add(json);
					continue;
				}

				List<String> itc04Sections = dto.getItc04Sections();
				String groupCode = TenantContext.getTenantId();

				List<String> initAndInprogressBatchs = new ArrayList<>();

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("ITC04 All GET Request received");
				}
				List<String> initAndInProgressStatus = new ArrayList<>();
				initAndInProgressStatus
						.add(APIConstants.INITIATED.toUpperCase());
				initAndInProgressStatus
						.add(JobStatusConstants.IN_PROGRESS.toUpperCase());
				initAndInprogressBatchs = batchRepo.findBatchByStatus(
						dto.getGstin(), dto.getReturnPeriod(),
						APIConstants.ITC04.toUpperCase(),
						initAndInProgressStatus);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("ITC04 All GET Not eligible sections are {} ",
							initAndInProgressStatus);
				}

				String userName = SecurityContext.getUser()
						.getUserPrincipalName();
				Long userRequestId = gstnUserRequestUtil.createGstnUserRequest(
						gstin, taxPeriod, APIConstants.GET,
						APIConstants.ITC04.toUpperCase(), groupCode, userName,
						false, false, false);
				dto.setUserRequestId(userRequestId);

				if (isEligible(initAndInprogressBatchs, APIConstants.GET,
						itc04Sections, gstin, taxPeriod)) {
					dto.setType(APIConstants.GET.toUpperCase());
					dto = createBatchAndSave(groupCode, dto);
					jsonParam = gson.toJson(dto);
					job = asyncJobsService.createJob(groupCode,
							JobConstants.ITC04_GSTN_GET_SECTION, jsonParam,
							userName, JobConstants.PRIORITY,
							JobConstants.PARENT_JOB_ID,
							JobConstants.SCHEDULE_AFTER_IN_MINS);

					APIParam param1 = new APIParam("gstin", dto.getGstin());
					APIParam param2 = new APIParam("ret_period", dto.getReturnPeriod());

					APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
							APIIdentifiers.ITC04_GET_INVOICES, param1, param2);
					JsonElement apiParams =  gson.toJsonTree(params);
					
					gstinGetStatusService.saveOrUpdateGSTNGetStatus(
							apiParams.toString(), "INITIATED", null);
					
					
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Job Created for Itc04 Get as {} ", job);
					}
				}
				if (!uniqueGstins.contains(gstin)) {
					JsonObject json = new JsonObject();
					json.addProperty("gstin", gstin);
					json.addProperty("msg",
							"Get ITC04  Request initiated Successfully");
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
			String msg = "Unexpected error while creating async jobs for ITC04 GET";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private Itc04GetInvoicesReqDto createBatchAndSave(String groupCode,
			Itc04GetInvoicesReqDto dto) {

		TenantContext.setTenantId(groupCode);
		// InActivating Previous Batch Records
		batchRepo.softlyDelete(dto.getType().toUpperCase(),
				APIConstants.ITC04.toUpperCase(), dto.getGstin(),
				dto.getReturnPeriod());
		GetAnx1BatchEntity batch = batchUtil.makeBatchItc04(dto, dto.getType());
		// Save new Batch
		batch = batchRepo.save(batch);
		dto.setBatchId(batch.getId());
		return dto;

	}

	private JsonArray getAllActiveGstnList(List<Itc04GetInvoicesReqDto> dtos) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Getting all the active GSTNs for ITC04 GET");
		}

		String msg = "";
		List<Itc04GetInvoicesReqDto> inActiveGstinDtos = new ArrayList<>();
		JsonArray respBody = new JsonArray();
		if (dtos != null && !dtos.isEmpty()) {

			for (Itc04GetInvoicesReqDto dto : dtos) {
				String gstin = dto.getGstin();
				JsonObject json = new JsonObject();
				String authStatus = authTokenService
						.getAuthTokenStatusForGstin(gstin);
				dto.getItc04Sections().replaceAll(String::toUpperCase);
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
	private Boolean isEligible(List<String> initAndInprogressBatchs,
			String section, List<String> gstr1Sections, String gstin,
			String taxPeriod) {

		String previousGetFiledStatus = batchRepo.findByGstinAndTaxPeriod(gstin,
				taxPeriod, APIConstants.ITC04.toUpperCase(),
				section.toUpperCase());
		if (APIConstants.Y.equalsIgnoreCase(previousGetFiledStatus)) {
			LOGGER.error(
					"Itc04 get invoices {} section for the given gstin {} and taxperiod {} is already filed",
					section, gstin, taxPeriod);
			return false;
		}
		if ((!initAndInprogressBatchs.contains(section.toUpperCase())
				&& gstr1Sections.isEmpty())
				|| (!initAndInprogressBatchs.contains(section.toUpperCase())
						&& gstr1Sections.contains(section.toUpperCase()))) {
			return true;
		} else {
			return false;
		}
	}

}
