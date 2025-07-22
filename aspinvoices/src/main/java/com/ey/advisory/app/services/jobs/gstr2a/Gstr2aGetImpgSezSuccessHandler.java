package com.ey.advisory.app.services.jobs.gstr2a;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.javatuples.Pair;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.ErpEventsScenarioPermissionEntity;
import com.ey.advisory.admin.data.entities.client.ErpScenarioPermissionEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.ErpEventsScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioMasterRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.GetGstr2aImpgSezHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingImpgSezHeaderEntity;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2aImpgSezRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2aStagingImpgSezRepository;
import com.ey.advisory.app.docs.dto.erp.Get2ARevIntReqDto;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.gstnapi.APIInvokerUtil;
import com.ey.advisory.gstnapi.SuccessHandler;
import com.ey.advisory.gstnapi.SuccessResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Service("Gstr2aGetImpgSezSuccessHandler")
@Slf4j
public class Gstr2aGetImpgSezSuccessHandler implements SuccessHandler {

	@Autowired
	@Qualifier("Gstr2aImpgSezDataParserImpl")
	private Gstr2aImpgSezDataParser gstr2aImpgSezDataParser;

	@Autowired
	private GetGstr2aImpgSezRepository getGstr2aImpgSezRepository;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	private GetGstr2aAmdhistUtil amdUtil;

	@Autowired
	private GetGstr2aStagingImpgSezRepository getGstr2aStagingImpgSezRepository;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	private ErpEventsScenarioPermissionRepository erpEventsScenPermissionRepo;

	@Autowired
	private ErpScenarioMasterRepository scenarioMasterRepo;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	private ErpScenarioPermissionRepository erpScenPermissionRepo;

	@Autowired
	private GSTNDetailRepository gstinDetailRepo;

	@Override
	public void handleSuccess(SuccessResult result, String apiParams) {
		Long batchId = null;
		try {
			List<Long> resultIds = result.getSuccessIds();
			String ctxParams = result.getCtxParams();
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject ctxParamsObj = (new JsonParser()).parse(ctxParams)
					.getAsJsonObject();

			Gstr1GetInvoicesReqDto dto = gson.fromJson(ctxParamsObj,
					Gstr1GetInvoicesReqDto.class);
			batchId = dto.getBatchId();
			String gstin = dto.getGstin();
			GSTNDetailEntity gstinInfo = gstinDetailRepo
					.findByGstinAndIsDeleteFalse(gstin);
			Long gstinId = gstinInfo.getId();
			String apiSection = dto.getApiSection();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GET Call is Success for the batchId {} inside Gstr2aGetImpgSezSuccessHandler.class",
						batchId);
			}

			TenantContext.setTenantId(dto.getGroupcode());
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			if (APIConstants.GSTR2A.equalsIgnoreCase(apiSection)) {
				/*
				 * boolean isFDeltaGetData = false; GSTNDetailEntity gstinInfo =
				 * gstnRepo .findByGstinAndIsDeleteFalse(dto.getGstin());
				 * EntityConfigPrmtEntity entityConfig = entityConfigRepo
				 * .findByGroupCodeAndEntityIdAndparamkryId( dto.getGroupcode(),
				 * gstinInfo.getEntityId(), "I25"); String paramValue =
				 * entityConfig!= null ? entityConfig.getParamValue() : "A"; if
				 * ("B".equals(paramValue)) { isFDeltaGetData = true; }
				 */
				boolean isFDeltaGetData = true;
				if (!isFDeltaGetData) {
					handleUserGetGstr2a(resultIds, dto, batchId);
				} else {
					handleStagingGetGstr2a(resultIds, dto, batchId);
					// Proc call to insert the Delta data into original tables
					docRepository.getGstr2aProcCall(dto.getGstin(),
							dto.getReturnPeriod(), dto.getType(), batchId,
							dto.getFromTime() != null ? true : false);

					List<String> uniqueKeys = getGstr2aImpgSezRepository
							.findInvKeyByBatchId(batchId);

					uniqueKeys.forEach(uniqueKey -> {
						amdUtil.createAsyncjob(uniqueKey, APIConstants.IMPGSEZ,
								dto);
					});

					Map<String, Config> configMap = configManager.getConfigs(
							"SFTP_PUSH", "get.gstr2A.sftp.groupCodes");
					String groupCodesOptedSFTPpush = configMap
							.get("get.gstr2A.sftp.groupCodes") != null
									? configMap
											.get("get.gstr2A.sftp.groupCodes")
											.getValue()
									: null;

					List<String> groupCodeList = new ArrayList<>();
					if (groupCodesOptedSFTPpush != null)

					{
						groupCodeList = new ArrayList<String>(Arrays
								.asList(groupCodesOptedSFTPpush.split(",")));

					}

					if (groupCodeList.contains(TenantContext.getTenantId())) {
						checkForSFTPReverseIntegration(dto, batchId);

					} else {

						Long scenarioId = scenarioMasterRepo
								.findSceIdOnScenarioName(
										JobConstants.NEW_GSTR2A_GET_REV_INTG);
						List<ErpScenarioPermissionEntity> scenarioPermisionList = erpScenPermissionRepo
								.findByScenarioIdAndGstinIdAndIsDeleteFalse(
										scenarioId, gstinId);
						if (!scenarioPermisionList.isEmpty()) {
							// Code to generate trigger/ create async job for
							// ERP
							// reverse integration.
							for (ErpScenarioPermissionEntity scenarioPermision : scenarioPermisionList) {
								Get2ARevIntReqDto erpReqDto = new Get2ARevIntReqDto();
								erpReqDto.setGstin(dto.getGstin());
								erpReqDto.setRetPeriod(dto.getReturnPeriod());
								erpReqDto.setSection(dto.getType());
								erpReqDto.setBatchId(batchId);
								erpReqDto.setDestinationName(
										scenarioPermision.getDestName());
								erpReqDto
										.setErpId(scenarioPermision.getErpId());
								String erpReqJson = gson.toJson(erpReqDto);
								asyncJobsService.createJob(
										TenantContext.getTenantId(),
										JobConstants.NEW_GSTR2A_GET_REV_INTG,
										erpReqJson, JobConstants.SYSTEM,
										JobConstants.PRIORITY,
										JobConstants.PARENT_JOB_ID,
										JobConstants.SCHEDULE_AFTER_IN_MINS);
							}
						}
					}

					/*
					 * Long scenarioId = scenarioMasterRepo
					 * .findSceIdOnScenarioName(
					 * JobConstants.NEW_GSTR2A_GET_REV_INTG); // Assuming it as
					 * Event based job ErpEventsScenarioPermissionEntity
					 * scenarioPermision = erpEventsScenPermissionRepo
					 * .findByScenarioIdAndErpIdAndIsDeleteFalse( scenarioId,
					 * 1l); if (scenarioPermision != null) { // Code to generate
					 * trigger/ create async job for // ERP // reverse
					 * integration. Get2ARevIntReqDto erpReqDto = new
					 * Get2ARevIntReqDto(); erpReqDto.setGstin(dto.getGstin());
					 * erpReqDto.setRetPeriod(dto.getReturnPeriod());
					 * erpReqDto.setSection(dto.getType());
					 * erpReqDto.setBatchId(batchId); String erpReqJson =
					 * gson.toJson(erpReqDto); AsyncExecJob job =
					 * asyncJobsService.createJob( TenantContext.getTenantId(),
					 * JobConstants.NEW_GSTR2A_GET_REV_INTG, erpReqJson,
					 * JobConstants.SYSTEM, JobConstants.PRIORITY,
					 * JobConstants.PARENT_JOB_ID,
					 * JobConstants.SCHEDULE_AFTER_IN_MINS); }
					 */ }
			} else {
				// handleErpGetGstr2a(resultIds, dto, batchId);
			}
			boolean isTokenResp = false;
			if (resultIds != null && resultIds.size() > 1) {
				isTokenResp = true;
			}
			batchUtil.updateById(batchId, APIConstants.SUCCESS, null, null,
					isTokenResp);
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			batchUtil.updateById(batchId, APIConstants.FAILED, "CATCH_BLOCK",
					e.getMessage(), false);
			throw new APIException(e.getLocalizedMessage());
		}
	}

	private void handleUserGetGstr2a(List<Long> resultIds,
			Gstr1GetInvoicesReqDto dto, Long batchId) {
		TenantContext.setTenantId(dto.getGroupcode());
		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());

		if (resultIds != null && !resultIds.isEmpty()) {
			if (APIConstants.IMPGSEZ.equalsIgnoreCase(dto.getType())) {
				getGstr2aImpgSezRepository.softlyDeleteByGstnRetPeriod(
						dto.getGstin(), dto.getReturnPeriod(), now);
			}
		}
		resultIds.forEach(id -> {
			String apiResp = APIInvokerUtil.getResultById(id);
			if (APIConstants.IMPGSEZ.equalsIgnoreCase(dto.getType())) {
				Pair<List<GetGstr2aStagingImpgSezHeaderEntity>, Set<String>> parseImpgSezData = gstr2aImpgSezDataParser
						.parseImpgSezData(dto, apiResp, dto.getType(), batchId,
								now);
				List<GetGstr2aStagingImpgSezHeaderEntity> impgSezEntities = parseImpgSezData
						.getValue0();
				Type listType = new TypeToken<List<GetGstr2aImpgSezHeaderEntity>>() {
				}.getType();
				ModelMapper modelmapper = new ModelMapper();
				List<GetGstr2aImpgSezHeaderEntity> postDtoList = modelmapper
						.map(impgSezEntities, listType);
				postDtoList.forEach(postDto -> {
					postDto.setDeltaInStatus(APIConstants.N);
				});
				if (!impgSezEntities.isEmpty()) {
					getGstr2aImpgSezRepository.saveAll(postDtoList);
				}
				Set<String> uniqueKeys = parseImpgSezData.getValue1();
				uniqueKeys.forEach(uniqueKey -> {
					amdUtil.createAsyncjob(uniqueKey, APIConstants.IMPGSEZ,
							dto);
				});
			}

		});

	}

	private void handleStagingGetGstr2a(List<Long> resultIds,
			Gstr1GetInvoicesReqDto dto, Long batchId) {
		TenantContext.setTenantId(dto.getGroupcode());
		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());

		if (resultIds != null && !resultIds.isEmpty()) {
			if (APIConstants.IMPGSEZ.equalsIgnoreCase(dto.getType())) {
				getGstr2aStagingImpgSezRepository.softlyDeleteByGstnRetPeriod(
						dto.getGstin(), dto.getReturnPeriod(), now);
			}
		}
		resultIds.forEach(id -> {
			String apiResp = APIInvokerUtil.getResultById(id);
			if (APIConstants.IMPGSEZ.equalsIgnoreCase(dto.getType())) {
				Pair<List<GetGstr2aStagingImpgSezHeaderEntity>, Set<String>> parseImpgSezData = gstr2aImpgSezDataParser
						.parseImpgSezData(dto, apiResp, dto.getType(), batchId,
								now);
				List<GetGstr2aStagingImpgSezHeaderEntity> impgSezEntities = parseImpgSezData
						.getValue0();
				if (!impgSezEntities.isEmpty()) {
					getGstr2aStagingImpgSezRepository.saveAll(impgSezEntities);
				}
				/*
				 * Set<String> uniqueKeys = parseImpgSezData.getValue1();
				 * uniqueKeys.forEach(uniqueKey -> {
				 * amdUtil.createAsyncjob(uniqueKey, APIConstants.IMPGSEZ, dto);
				 * });
				 */
			}

		});
	}

	private void checkForSFTPReverseIntegration(Gstr1GetInvoicesReqDto dto,
			Long batchId) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		Long scenarioId = scenarioMasterRepo.findSceIdOnScenarioName(
				JobConstants.NEW_GSTR2A_GET_REV_INTG_SFTP);

		if (scenarioId == null) {

			LOGGER.error(
					"Scenario {} is not configured for group {},"
							+ "Hence reverse SFTP Push job is not posted",
					JobConstants.NEW_GSTR2A_GET_REV_INTG_SFTP,
					TenantContext.getTenantId());
			return;
		}

		ErpEventsScenarioPermissionEntity scenarioPermision = erpEventsScenPermissionRepo
				.findByScenarioIdAndIsDeleteFalse(scenarioId);

		if (scenarioPermision == null) {

			LOGGER.error("SFTP permission {} is not configured for group {},"
					+ "Hence SFTP reverse push job is " + "not posted for {}",
					JobConstants.NEW_GSTR2A_GET_REV_INTG_SFTP,
					TenantContext.getTenantId());
			return;
		}

		/*
		 * Submit the Async job only if the Reverse integration is on-boarded -
		 * Client level
		 */

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Submitting SFTP Push Job for groupCode :{}",
					TenantContext.getTenantId());
			LOGGER.debug(msg);
		}

		Get2ARevIntReqDto erpReqDto = new Get2ARevIntReqDto();
		erpReqDto.setGstin(dto.getGstin());
		erpReqDto.setRetPeriod(dto.getReturnPeriod());
		erpReqDto.setSection(dto.getType());
		erpReqDto.setBatchId(batchId);
		erpReqDto.setErpId(scenarioPermision.getErpId());
		erpReqDto.setScenarioName(JobConstants.NEW_GSTR2A_GET_REV_INTG_SFTP);
		String erpReqJson = gson.toJson(erpReqDto);

		asyncJobsService.createJob(TenantContext.getTenantId(),
				JobConstants.NEW_GSTR2A_GET_REV_INTG_SFTP, erpReqJson,
				JobConstants.SYSTEM, JobConstants.PRIORITY,
				JobConstants.PARENT_JOB_ID,
				JobConstants.SCHEDULE_AFTER_IN_MINS);
	}

}
