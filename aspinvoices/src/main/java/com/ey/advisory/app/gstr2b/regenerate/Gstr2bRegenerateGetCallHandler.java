package com.ey.advisory.app.gstr2b.regenerate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.SaveToGstnEventStatus;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.domain.client.Gstr2bRegenerateSaveBatchEntity;
import com.ey.advisory.gstnapi.GstinGetStatusService;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.repositories.client.Gstr2bRegenerateBatchRepository;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */

@Service("Gstr2bRegenerateGetCallHandler")
@Slf4j
public class Gstr2bRegenerateGetCallHandler {

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	private Gstr2bRegenerateBatchRepository gstr2bRegRepo;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	@Autowired
	private GstrReturnStatusRepository gstrReturnStatusRepository;

	@Autowired
	@Qualifier("saveToGstnEventStatusImpl")
	private SaveToGstnEventStatus saveToGstnEventStatus;

	@Autowired
	@Qualifier("GstinGetStatusServiceImpl")
	private GstinGetStatusService gstinGetStatusService;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gSTNDetailRepository;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	public ResponseEntity<String> getGstr2bRegenerateCall(
			List<Gstr1GetInvoicesReqDto> dtos, List<AsyncExecJob> jobList) {

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
			List<String> taxPeriodList = null;

			String userName = SecurityContext.getUser().getUserPrincipalName();

			String groupCode = TenantContext.getTenantId();

			for (Gstr1GetInvoicesReqDto dto : dtos) {
				dto.setType("SAVE");
				boolean isAllsectionsGetInProgress = true;

				String fy = dto.getFinYear();
				if ((dto.getMonth().size() > 0)) {
					taxPeriodList = dto.getMonth().stream()
							.map(o -> getRetPeriod(o, fy))
							.collect(Collectors.toList());
					LOGGER.error("taxPeriodList1 {} ", taxPeriodList);
				}
				List<Pair<String, String>> activePairs = new ArrayList<>();

				for (String taxPeriod : taxPeriodList) {
					if (GenUtil.isValidTaxPeriodForCurrentFy(taxPeriod))
						activePairs.add(new Pair<>(dto.getGstin(), taxPeriod));
					else {
						LOGGER.warn(
								"Requested TaxPeriod {} is Future TaxPeriod, "
										+ "Hence skipping this Get2B ",
								taxPeriod);
					}
				}
				for (int i = 0; i < activePairs.size(); i++) {

					Pair<String, String> pair = activePairs.get(i);

					GstrReturnStatusEntity entity = gstrReturnStatusRepository
							.findByGstinAndTaxPeriodAndReturnTypeAndIsCounterPartyGstin(
									pair.getValue0(), pair.getValue1(),
									"GSTR3B", false);

					if (entity != null) {

						JsonObject json = new JsonObject();
						json.addProperty("gstin", pair.getValue0());
						json.addProperty("msg",
								"GSTR3B is Already Filed,New request Cannot be taken.");
						respBody.add(json);
						continue;
					}

					List<String> initAndInProgressStatus = new ArrayList<>();
					List<String> initAndInprogressBatchs = new ArrayList<>();

					initAndInProgressStatus
							.add(APIConstants.INITIATED.toUpperCase());
					initAndInProgressStatus
							.add(JobStatusConstants.IN_PROGRESS.toUpperCase());
					initAndInprogressBatchs = batchRepo.findBatchByStatus(
							pair.getValue0(), pair.getValue1(),
							APIConstants.GSTR2B_REGENERATE.toUpperCase(),
							initAndInProgressStatus);

					dto.setApiSection(
							APIIdentifiers.GSTR2B_REGENERATE.toUpperCase());

					if (initAndInprogressBatchs.isEmpty()) {
						
						dto.setReturnPeriod(pair.getValue1());
						dto.setType("SAVE");
						isAllsectionsGetInProgress = false;
						dto = createBatchAndSave(groupCode, dto);

						gstr2bRegRepo.updatepreviousBatch(pair.getValue0(),
								pair.getValue1());

						// soft delete previous one
						Gstr2bRegenerateSaveBatchEntity saveEntity = new Gstr2bRegenerateSaveBatchEntity();
						saveEntity.setBatchId(dto.getBatchId());
						saveEntity.setSupplierGstin(pair.getValue0());
						saveEntity.setReturnPeriod(pair.getValue1());
						saveEntity.setCreatedOn(LocalDateTime.now());
						saveEntity.setDerivedRetPeriod(GenUtil
								.convertTaxPeriodToInt(pair.getValue1()));
						saveEntity.setStatus("SAVE_INITIATED");
						saveEntity.setSupplierGstin(pair.getValue0());
						saveEntity.setDelete(false);
						saveEntity.setReturnType("GSTR2B");
						saveEntity = gstr2bRegRepo.save(saveEntity);
						
						dto.setSaveId(saveEntity.getId());

						LOGGER.debug("Dto after creating batch:: {}",
								dto.toString());
						jsonParam = gson.toJson(dto);

						jobList.add(asyncJobsService.createJobAndReturn(
								groupCode, JobConstants.GSTR2B_REGENERATE_JOB,
								jsonParam.toString(), userName,
								JobConstants.PRIORITY,
								JobConstants.PARENT_JOB_ID,
								JobConstants.SCHEDULE_AFTER_IN_MINS));
					}
					// status code 10 says status as USER REQUEST INITIATED


				}
				

				if (isAllsectionsGetInProgress) {
					JsonObject json = new JsonObject();
					json.addProperty("gstin", dto.getGstin());
					json.addProperty("msg",
							"GSTR2B Regenerate is Already Inprogress, New Request can't be taken");

					respBody.add(json);
				} else {
					JsonObject json = new JsonObject();
					json.addProperty("gstin", dto.getGstin());
					json.addProperty("msg",
							"GSTR2B Regenerate Request Submitted Successfully");
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
			String msg = "Unexpected error while creating async jobs for Gstr2b regenerate ";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
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

	private Gstr1GetInvoicesReqDto createBatchAndSave(String groupCode,
			Gstr1GetInvoicesReqDto dto) {
		TenantContext.setTenantId(groupCode);
		// InActiveting Previous Batch Records
		batchRepo.softlyDelete(dto.getType().toUpperCase(),
				APIConstants.GSTR2B_REGENERATE, dto.getGstin(),
				dto.getReturnPeriod());
		GetAnx1BatchEntity batch = batchUtil.makeBatchGstr1(dto, dto.getType(),
				APIConstants.GSTR2B_REGENERATE);
		// Save new Batch
		batch = batchRepo.save(batch);
		dto.setBatchId(batch.getId());
		return dto;

	}
}
