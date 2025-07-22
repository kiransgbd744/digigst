package com.ey.advisory.controller;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.SaveRcmOpeningBalDto;
import com.ey.advisory.app.data.entities.client.asprecon.LedgerSaveToGstnRcmEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.LedgerSaveToGstnRcmRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.SaveRcmOpeningBalRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author kiran s
 *
 */

@RestController
@Slf4j
public class LedgerSaveToGstnController {

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	private GSTNDetailRepository gSTNDetailRepository;

	@Autowired
	@Qualifier("SaveRcmOpeningBalRepository")
	private SaveRcmOpeningBalRepository saveRcmOpeningBalRepository;

	@Autowired
	@Qualifier("LedgerSaveToGstnRcmRepository")
	LedgerSaveToGstnRcmRepository ledgerSaveToGstnRcmRepository;

	/*
	 * @Autowired
	 * 
	 * @Qualifier("LedgerSaveToGstnProcessorTest") LedgerSaveToGstnProcessorTest
	 * ledgerSaveToGstnProcessorTest;
	 */

	private JsonArray getAllRcmActiveGstnList(List<String> gstins,
			String taxPeriod, List<String> activeGstnList) {
		JsonArray respBody = new JsonArray();
		try {

			String msg = "";

			if (gstins != null && !gstins.isEmpty()) {

				for (String gstin : gstins) {
					JsonObject json = new JsonObject();
					String authStatus = authTokenService
							.getAuthTokenStatusForGstin(gstin);
					if ("A".equalsIgnoreCase(authStatus)) {
						activeGstnList.add(gstin);
						// msg = "Auth Token Active, Ledger get call
						// successfully initiated";

					} else {
						msg = "Auth Token is Inactive, Please Activate";
						json.addProperty("gstin", gstin);
						json.addProperty("msg", msg);
						respBody.add(json);

					}

				}
			}

		} catch (Exception ex) {
			String msg = String
					.format("Exception while getAllActiveGstnList method");
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
		return respBody;
	}

	//// need to shift this to serviceimpl
	private List<SaveRcmOpeningBalDto> initiateSaveOpnBalForRcmLedgerGetcall(
			List<String> gstins, String ledgerType) {
		List<SaveRcmOpeningBalDto> saveRcmOpeningBalDtoList = new ArrayList<>();
		DateTimeFormatter formatter = DateTimeFormatter
				.ofPattern("dd-MM-yyyy HH:mm:ss");
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"About to get saved values for gstin '%s',",
						gstins);
				LOGGER.debug(msg);
			}
			List<LedgerSaveToGstnRcmEntity> saveRcmOpeningBal = ledgerSaveToGstnRcmRepository
					.findByGstinListAndIsActiveTrueAndLedgerType(gstins,
							ledgerType);

			if (saveRcmOpeningBal == null || saveRcmOpeningBal.isEmpty()) {
				for (String gstin : gstins) {
					SaveRcmOpeningBalDto defaultDto = new SaveRcmOpeningBalDto();

					defaultDto.setGstin(gstin); // Set the current gstin
					defaultDto.setSaveStatus("Not Initiated");
					defaultDto.setIgst(BigDecimal.ZERO);
					defaultDto.setCgst(BigDecimal.ZERO);
					defaultDto.setSgst(BigDecimal.ZERO);
					defaultDto.setCess(BigDecimal.ZERO);
					defaultDto.setErrMsg("No records found");

					saveRcmOpeningBalDtoList.add(defaultDto);
				}
			} else {
				for (LedgerSaveToGstnRcmEntity entity : saveRcmOpeningBal) {
					SaveRcmOpeningBalDto saveRcmOpeningBalDto = new SaveRcmOpeningBalDto();

					saveRcmOpeningBalDto.setGstin(entity.getGstin());
					saveRcmOpeningBalDto
							.setSaveStatus(entity.getStatus());
					saveRcmOpeningBalDto.setIgst(entity.getIgst() != null
							? entity.getIgst()
							: BigDecimal.ZERO);
					saveRcmOpeningBalDto.setCgst(entity.getCgst() != null
							? entity.getCgst()
							: BigDecimal.ZERO);
					saveRcmOpeningBalDto.setSgst(entity.getSgst() != null
							? entity.getSgst()
							: BigDecimal.ZERO);
					saveRcmOpeningBalDto.setCess(entity.getCess() != null
							? entity.getCess()
							: BigDecimal.ZERO);

					saveRcmOpeningBalDto
							.setErrMsg(entity.getErrmsg() != null
									? entity.getErrmsg() : null);
					if (entity.getCompletedOn() != null) {
						LocalDateTime istDateTimeFromUTC = EYDateUtil
								.toISTDateTimeFromUTC(entity.getCompletedOn());
						String formattedDate = istDateTimeFromUTC
								.format(formatter);
						/*
						 * String formattedDate = entity.getCompletedOn()
						 * .format(formatter);
						 */
						saveRcmOpeningBalDto.setUpdatedOn(formattedDate);
					} else {
						saveRcmOpeningBalDto.setUpdatedOn(null); // or set a
																	// default
																	// value if
																	// necessary
					}

					saveRcmOpeningBalDtoList.add(saveRcmOpeningBalDto);
				}
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Initiated initiateSaveOpnBalForRcmLedgerGetcall '%s' ",
						gstins);
				LOGGER.debug(msg);
			}
		} catch (Exception ex) {
			String msg = String.format(
					"Exception while initiateSaveOpnBalForRcmLedgerGetcall");
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
		return saveRcmOpeningBalDtoList;
	}

	@PostMapping(value = "/ui/SaveOpeningBalenceForRcmAndReclaimLedger", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> SaveOpeningBalenceForRcmAndReclaimLedger(
			@RequestBody String jsonReq) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject requestObject = JsonParser.parseString(jsonReq)
					.getAsJsonObject();

			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			gson = GsonUtil.newSAPGsonInstance();
			Long entityId = reqObject.get("entityId").getAsLong();
			String ledgerType = reqObject.get("ledgerType").getAsString();
			List<String> ledgerGstinList = new ArrayList<>();
			if (reqObject.has("gstins")) {
				JsonArray gstinArray = reqObject
						.getAsJsonArray("gstins");
				Type listType = new TypeToken<ArrayList<String>>() {
				}.getType();

				ledgerGstinList = gson.fromJson(gstinArray, listType);
			}
			List<String> activeGstnList = new ArrayList<>();
			JsonArray getRespBody = null;
			List<SaveRcmOpeningBalDto> saveOpnBalForRcm = null;
			if (!ledgerGstinList.isEmpty()) {
				getRespBody = getAllRcmActiveGstnList(ledgerGstinList, null,
						activeGstnList);
			}
			if (getRespBody == null || getRespBody.size() == 0) {
				getRespBody = new JsonArray();
			}
			if (!activeGstnList.isEmpty()) {
				saveOpnBalForRcm = initiateSaveOpnBalForRcmLedgerGetcall(
						activeGstnList, ledgerType);
			}
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("getRespBody", gson.toJsonTree(getRespBody));
			resp.add("saveOpnBalForRcm", gson.toJsonTree(saveOpnBalForRcm));

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String msg = String.format(
					"Error Occured while SaveToGstinRcmLedger '%s'",
					e.getMessage());
			APIRespDto dto = new APIRespDto("Failed", msg);
			JsonElement respBody = gson.toJsonTree(dto);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/SaveToGstinforRcmAndReclaimLedger", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> SaveToGstinforRcmAndReclaimLedger(
			@RequestBody String jsonReq) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<String> softDeleteByGstinList = new ArrayList<>();
		List<LedgerSaveToGstnRcmEntity> SaveToGstnRcmRepositoryList = new ArrayList<>();
		try {

			JsonObject json = JsonParser.parseString(jsonReq).getAsJsonObject();
			String ledgerType = json.getAsJsonObject("req").get("ledgerType")
					.getAsString();
			JsonArray reqDataArray = json.getAsJsonObject("req")
					.getAsJsonArray("reqData");
			Type listType = new TypeToken<List<SaveRcmOpeningBalDto>>() {
			}.getType();
			List<SaveRcmOpeningBalDto> reqDto = gson.fromJson(reqDataArray,
					listType);
			String userName = (SecurityContext.getUser() != null
					&& SecurityContext.getUser()
							.getUserPrincipalName() != null)
									? SecurityContext.getUser()
											.getUserPrincipalName()
									: "SYSTEM";
			// LedgerSaveToGstnRcmEntity entity = null;
			List<LedgerSaveToGstnRcmEntity> entitiesToSave = new ArrayList<>();
			List<AsyncExecJob> jobsList = new ArrayList<>();

			for (SaveRcmOpeningBalDto dto : reqDto) {
				LedgerSaveToGstnRcmEntity entity = new LedgerSaveToGstnRcmEntity();
				entity.setEntityId(dto.getEntityId());
				entity.setGstin(dto.getGstin());
				entity.setIgst(dto.getIgst());
				entity.setCgst(dto.getCgst());
				entity.setSgst(dto.getSgst());
				entity.setCess(dto.getCess());
				entity.setIsAmended(dto.getIsAmended());
				entity.setLedgerType(ledgerType);
				entity.setStatus("INITIATED");
				entity.setIsActive(true);
				entity.setInitiatedBy(userName);
				entity.setInitiatedOn(LocalDateTime.now());

				// Collect GSTINs for soft delete
				softDeleteByGstinList.add(dto.getGstin());

				// Add entity to the list to save later
				entitiesToSave.add(entity);

				// Prepare async job parameters
				JsonObject jsonParams = new JsonObject();
				jsonParams.addProperty("gstin", dto.getGstin());
				jsonParams.addProperty("ledgerType", ledgerType);
				String groupCode = TenantContext.getTenantId();
				AsyncExecJob job = asyncJobsService.createJobAndReturn(
						groupCode,
						JobConstants.SAVE_TO_GSTN_RCM_LEDGER,
						jsonParams.toString(),
						userName, 1L, null, null);
				jobsList.add(job);
			}
			ledgerSaveToGstnRcmRepository.softDeleteByGstinListAndLedgerType(
					softDeleteByGstinList, ledgerType);

			// Save all entities in one go
			ledgerSaveToGstnRcmRepository.saveAll(entitiesToSave);

			// Execute all async jobs in one go
			asyncJobsService.createJobs(jobsList);

			// Prepare success response
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp",
					gson.toJsonTree("Save To GSTN initiated successfully"));

		} catch (Exception e) {
			LOGGER.error(
					"Error occurred while saving to GSTIN for RCM and Reclaim Ledger",
					e);
			APIRespDto dto = new APIRespDto("Failed",
					"Error occurred while saving to GSTIN for RCM and Reclaim Ledger"
							+ e.getMessage());
			resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Error Occured in Async Report Controller";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);

		}
		return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);

	}
/*public static void main(String[] args) {
	DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH:mm:ss");
		LocalDateTime istDateTimeFromUTC = EYDateUtil
				.toISTDateTimeFromUTC(entity.getCompletedOn());
		String formattedDate = istDateTimeFromUTC
				.format(formatter);
		
		 * String formattedDate = entity.getCompletedOn()
		 * .format(formatter);
		 
		saveRcmOpeningBalDto.setUpdatedOn(formattedDate);
	
}*/
}
