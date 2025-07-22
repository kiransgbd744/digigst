package com.ey.advisory.controller;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import com.ey.advisory.app.data.entities.client.GstnSubmitEntity;
import com.ey.advisory.app.data.repositories.client.GstnSubmitRepository;
import com.ey.advisory.app.docs.dto.Gstr1SubmitGstnDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;
/**
 * 
 * @author SriBhavya
 *
 */
@RestController
@Slf4j
public class Gstr6SubmitApiJobInsertionController {
	
	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	@Autowired
	private GstnSubmitRepository gstr6SubmitRepo;
	
	@PostMapping(value = "/ui/Gstr6GstnSubmit", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> createGstr6GstnSubmitJob(
			@RequestBody String request) {
		JsonObject resp = new JsonObject();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6GstnSubmit Request received from UI as {} ",
						request);
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject requestObject = (new JsonParser()).parse(request)
					.getAsJsonObject();
			JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();
			Type listType = new TypeToken<List<Gstr1SubmitGstnDto>>() {
			}.getType();
			List<Gstr1SubmitGstnDto> dtos = gson.fromJson(asJsonArray,
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
			for (Gstr1SubmitGstnDto dto : dtos) {

				try {

					String gstin = dto.getGstin();
					String taxPeriod = dto.getRet_period();
					// Boolean isFailedGet = dto.getIsFailed();
					// List<String> gstr2aSections = dto.getGstr2aSections();
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

					List<GstnSubmitEntity> activeSubmitRequests = gstr6SubmitRepo
							.findByGstinAndRetPeriodAndGstnStatusNotAndIsDeleteFalse(
									dto.getGstin(), dto.getRet_period(),
									APIConstants.FAILED.toUpperCase());

					// No previous active requests for GSTR1 SUBMIT.
					if (activeSubmitRequests == null
							|| activeSubmitRequests.isEmpty()) {
						String userName = SecurityContext.getUser().getUserPrincipalName();
						dto = createBatchAndSave(groupCode, dto, userName);

						jsonParam = gson.toJson(dto);
						job = asyncJobsService.createJob(groupCode,
								JobConstants.GSTR6_GSTN_SUBMIT, jsonParam,
								userName, JobConstants.PRIORITY,
								JobConstants.PARENT_JOB_ID,
								JobConstants.SCHEDULE_AFTER_IN_MINS);

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Job Created for Gstr1 Submit as {} ",
									job);
						}
					} else {
						JsonObject json = new JsonObject();
						msg = "Gstr6 has Already submitted.";
						json.addProperty("gstin", gstin);
						json.addProperty("msg", msg);
						respBody.add(json);
						LOGGER.error(msg + " for {} ", gstin);
						continue;
					}
					JsonObject json = new JsonObject();
					json.addProperty("gstin", gstin);
					json.addProperty("msg",
							"SUBMIT GSTR6  Request initiated Successfully");
					respBody.add(json);

				} catch (Exception e) {
					LOGGER.error(e.getMessage());
					throw new AppException(e.getMessage(), e);

				}
			}
			if (apiResp == null) {
				apiResp = APIRespDto.createSuccessResp();
			}
			resp.add("hdr", gson.toJsonTree(apiResp));
			resp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			String msg = "Unexpected error while saving documents";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	private Gstr1SubmitGstnDto createBatchAndSave(String groupCode,
			Gstr1SubmitGstnDto dto, String userName) {

		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		TenantContext.setTenantId(groupCode);

		GstnSubmitEntity entity = new GstnSubmitEntity(); 
		entity.setGstin(dto.getGstin());
		entity.setRetPeriod(dto.getRet_period());
		entity.setReturnType(APIConstants.GSTR6.toUpperCase());
		entity.setStatus(APIConstants.INITIATED.toUpperCase());
		entity.setCreatedBy(userName);
		entity.setCreatedOn(now);
		// Save new Batch
		entity = gstr6SubmitRepo.save(entity);
		dto.setBatchId(entity.getId());
		return dto;

	}
	private JsonArray getAllActiveGstnList(List<Gstr1SubmitGstnDto> dtos) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Getting all the active GSTNs for GSTR1 SUBMIT");
		}

		String msg = "";
		List<Gstr1SubmitGstnDto> inActiveGstinDtos = new ArrayList<>();
		JsonArray respBody = new JsonArray();
		if (dtos != null && !dtos.isEmpty()) {

			for (Gstr1SubmitGstnDto dto : dtos) {
				String gstin = dto.getGstin();
				JsonObject json = new JsonObject();
				String authStatus = authTokenService
						.getAuthTokenStatusForGstin(gstin);

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

}
