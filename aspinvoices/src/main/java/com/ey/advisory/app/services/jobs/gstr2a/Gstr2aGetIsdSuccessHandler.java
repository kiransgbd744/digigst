/**
 * 
 */
package com.ey.advisory.app.services.jobs.gstr2a;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
import com.ey.advisory.app.data.entities.client.GetGstr2aErpIsdInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aErpIsdaInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aIsdInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aIsdaInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingIsdInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingIsdaInvoicesHeaderEntity;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2aErpIsdInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2aErpIsdaInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2aStagingIsdInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2aStagingIsdaInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.Gstr2aGetIsdInvoicesAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.Gstr2aGetIsdaInvoicesAtGstnRepository;
import com.ey.advisory.app.docs.dto.erp.Get2AConsolidatedRevIntgDto;
import com.ey.advisory.app.docs.dto.erp.Get2ARevIntReqDto;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
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
 * @author Mahesh.Golla
 *
 */
@Service("Gstr2aGetIsdSuccessHandler")
@Slf4j
public class Gstr2aGetIsdSuccessHandler implements SuccessHandler {

	@Autowired
	@Qualifier("gstr2aIsdIsdaDataParserImpl")
	private Gstr2aIsdIsdaDataParser gstr2aDataParser;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	private Gstr2aGetIsdInvoicesAtGstnRepository gstr2aGetIsdInvoicesAtGstnRepository;

	@Autowired
	private Gstr2aGetIsdaInvoicesAtGstnRepository gstr2aGetIsdaInvoicesAtGstnRepository;

	@Autowired
	private GetGstr2aErpIsdaInvoicesRepository getGstr2aErpIsdaInvoicesRepository;

	@Autowired
	private GetGstr2aErpIsdInvoicesRepository getGstr2aErpIsdInvoicesRepository;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Autowired
	private GetGstr2aStagingIsdaInvoicesRepository getGstr2aStagingIsdaInvoicesRepository;

	@Autowired
	private GetGstr2aStagingIsdInvoicesRepository getGstr2aStagingIsdInvoicesRepository;

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
				LOGGER.debug("GET Call is Success for the batchId {} inside"
						+ " Gstr2aGetIsdSuccessHandler.java", batchId);
			}

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
					if ("sp0023".equalsIgnoreCase(TenantContext.getTenantId())
							&& APIConstants.ISD
									.equalsIgnoreCase(dto.getType())) {
						docRepository.getGstr2AErpProcCall(dto.getGstin(),
								dto.getReturnPeriod(), dto.getType(), batchId);

						Long scenarioId = scenarioMasterRepo
								.findSceIdOnScenarioName(
										JobConstants.OLD_GSTR2A_GET_REV_INTG);

						String groupCode = TenantContext.getTenantId();
						/*
						 * Long gstinId =
						 * gstinDetailRepo.findIdByGstin(dto.getGstin());
						 * ErpScenarioPermissionEntity scenario =
						 * erpScenPermissionRepo
						 * .findByGroupcodeAndIsDeleteAndDestNameAndGstinIdAndscenarioId
						 * (groupCode, false, "NALCOGET2A", gstinId,
						 * scenarioId);
						 */

						List<ErpScenarioPermissionEntity> scenarioList = erpScenPermissionRepo
								.findByScenarioIdAndGstinIdAndIsDeleteFalse(
										scenarioId, gstinId);
						if (!scenarioList.isEmpty()) {
							for (ErpScenarioPermissionEntity scenario : scenarioList) {
								Get2AConsolidatedRevIntgDto consDto = new Get2AConsolidatedRevIntgDto();
								consDto.setDestinationName(
										scenario.getDestName());
								// consDto.setEntityId(scenario.getEntityId());
								consDto.setGstin(dto.getGstin());
								consDto.setGroupcode(
										TenantContext.getTenantId());
								consDto.setScenarioId(scenario.getErpId());
								consDto.setBatchId(batchId);
								consDto.setErpId(scenario.getErpId());
								String json = gson.toJson(dto,
										Get2AConsolidatedRevIntgDto.class);

								asyncJobsService.createJob(groupCode,
										JobConstants.OLD_GSTR2A_GET_REV_INTG,
										json, JobConstants.SYSTEM,
										JobConstants.PRIORITY,
										JobConstants.PARENT_JOB_ID,
										JobConstants.SCHEDULE_AFTER_IN_MINS);
							}
						}
					} else {

						Map<String, Config> configMap = configManager
								.getConfigs("SFTP_PUSH",
										"get.gstr2A.sftp.groupCodes");
						String groupCodesOptedSFTPpush = configMap
								.get("get.gstr2A.sftp.groupCodes") != null
										? configMap
												.get("get.gstr2A.sftp.groupCodes")
												.getValue()
										: null;

						List<String> groupCodeList = new ArrayList<>();
						if (groupCodesOptedSFTPpush != null)

						{
							groupCodeList = new ArrayList<String>(Arrays.asList(
									groupCodesOptedSFTPpush.split(",")));

						}

						if (groupCodeList
								.contains(TenantContext.getTenantId())) {
							checkForSFTPReverseIntegration(dto, batchId);

						} else {

							Long scenarioId = scenarioMasterRepo
									.findSceIdOnScenarioName(
											JobConstants.NEW_GSTR2A_GET_REV_INTG);

							List<ErpScenarioPermissionEntity> scenarioPermisionList = erpScenPermissionRepo
									.findByScenarioIdAndGstinIdAndIsDeleteFalse(
											scenarioId, gstinId);
							if (!scenarioPermisionList.isEmpty()) {
								// Code to generate trigger/ create async job
								// for
								// ERP
								// reverse integration.
								for (ErpScenarioPermissionEntity scenarioPermision : scenarioPermisionList) {
									Get2ARevIntReqDto erpReqDto = new Get2ARevIntReqDto();
									erpReqDto.setGstin(dto.getGstin());
									erpReqDto.setRetPeriod(
											dto.getReturnPeriod());
									erpReqDto.setSection(dto.getType());
									erpReqDto.setBatchId(batchId);
									erpReqDto.setDestinationName(
											scenarioPermision.getDestName());
									erpReqDto.setErpId(
											scenarioPermision.getErpId());
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
					}
				}
			} else {
				handleErpGetGstr2a(resultIds, dto, batchId);
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
			throw new AppException(e);
		}
	}

	/*
	 * private Long createAnx2ReconJob2a(String jsonParam) { String groupCode =
	 * TenantContext.getTenantId();
	 * LOGGER.debug("Anx2 Recon2a job Entry is insrted with param {} .",
	 * jsonParam); AsyncExecJob job = asyncJobsService.createJob(groupCode,
	 * JobConstants.ANX2_RECON_2A, jsonParam, JobConstants.SYSTEM,
	 * JobConstants.PRIORITY, JobConstants.PARENT_JOB_ID,
	 * JobConstants.SCHEDULE_AFTER_IN_MINS); return job.getJobId(); }
	 */

	private void handleUserGetGstr2a(List<Long> resultIds,
			Gstr1GetInvoicesReqDto dto, Long batchId) {
		TenantContext.setTenantId(dto.getGroupcode());
		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());

		if (resultIds != null && !resultIds.isEmpty()) {
			if (APIConstants.ISD.equalsIgnoreCase(dto.getType())) {
				gstr2aGetIsdInvoicesAtGstnRepository.softlyDeleteIsdHeader(
						dto.getGstin(), dto.getReturnPeriod(), now);
			} else {
				gstr2aGetIsdaInvoicesAtGstnRepository.softlyDeleteIsdaHeader(
						dto.getGstin(), dto.getReturnPeriod(), now);
			}
		}
		resultIds.forEach(id -> {
			String apiResp = APIInvokerUtil.getResultById(id);
			// Long batchId = null;
			if (APIConstants.ISD.equalsIgnoreCase(dto.getType())) {
				List<GetGstr2aStagingIsdInvoicesHeaderEntity> atEnties = gstr2aDataParser
						.parseIsdData(dto, apiResp, dto.getType(), batchId);
				Type listType = new TypeToken<List<GetGstr2aIsdInvoicesHeaderEntity>>() {
				}.getType();
				ModelMapper modelmapper = new ModelMapper();
				List<GetGstr2aIsdInvoicesHeaderEntity> postDtoList = modelmapper
						.map(atEnties, listType);
				postDtoList.forEach(postDto -> {
					postDto.setDeltaInStatus(APIConstants.N);
				});
				if (!atEnties.isEmpty()) {
					gstr2aGetIsdInvoicesAtGstnRepository.saveAll(postDtoList);
				}
			} else {
				List<GetGstr2aStagingIsdaInvoicesHeaderEntity> ataEnties = gstr2aDataParser
						.parseIsdaData(dto, apiResp, dto.getType(), batchId);
				Type listType = new TypeToken<List<GetGstr2aIsdaInvoicesHeaderEntity>>() {
				}.getType();
				ModelMapper modelmapper = new ModelMapper();
				List<GetGstr2aIsdaInvoicesHeaderEntity> postDtoList = modelmapper
						.map(ataEnties, listType);
				postDtoList.forEach(postDto -> {
					postDto.setDeltaInStatus(APIConstants.N);
				});
				if (!ataEnties.isEmpty()) {
					gstr2aGetIsdaInvoicesAtGstnRepository.saveAll(postDtoList);
				}
			}

		});

	}

	private void handleErpGetGstr2a(List<Long> resultIds,
			Gstr1GetInvoicesReqDto dto, Long batchId) {
		TenantContext.setTenantId(dto.getGroupcode());
		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());

		if (resultIds != null && !resultIds.isEmpty()) {
			if (APIConstants.ISD.equalsIgnoreCase(dto.getType())) {
				getGstr2aErpIsdInvoicesRepository.softlyDeleteIsdHeader(
						dto.getGstin(), dto.getReturnPeriod(), now);
			} else {
				getGstr2aErpIsdaInvoicesRepository.softlyDeleteIsdaHeader(
						dto.getGstin(), dto.getReturnPeriod(), now);
			}
		}
		resultIds.forEach(id -> {
			String apiResp = APIInvokerUtil.getResultById(id);
			// Long batchId = null;
			if (APIConstants.ISD.equalsIgnoreCase(dto.getType())) {
				List<GetGstr2aStagingIsdInvoicesHeaderEntity> atEnties = gstr2aDataParser
						.parseIsdData(dto, apiResp, dto.getType(), batchId);
				Type listType = new TypeToken<List<GetGstr2aErpIsdInvoicesHeaderEntity>>() {
				}.getType();
				ModelMapper modelmapper = new ModelMapper();
				List<GetGstr2aErpIsdInvoicesHeaderEntity> postDtoList = modelmapper
						.map(atEnties, listType);
				postDtoList.forEach(postDto -> {
					postDto.setGetBatchId(batchId);
				});
				if (!atEnties.isEmpty()) {
					getGstr2aErpIsdInvoicesRepository.saveAll(postDtoList);
				}
			} else {
				List<GetGstr2aStagingIsdaInvoicesHeaderEntity> ataEnties = gstr2aDataParser
						.parseIsdaData(dto, apiResp, dto.getType(), batchId);
				Type listType = new TypeToken<List<GetGstr2aErpIsdaInvoicesHeaderEntity>>() {
				}.getType();
				ModelMapper modelmapper = new ModelMapper();
				List<GetGstr2aErpIsdaInvoicesHeaderEntity> postDtoList = modelmapper
						.map(ataEnties, listType);
				postDtoList.forEach(postDto -> {
					postDto.setGetBatchId(batchId);
				});
				if (!ataEnties.isEmpty()) {
					getGstr2aErpIsdaInvoicesRepository.saveAll(postDtoList);
				}
			}

		});
		// Invoking the Proc to consolidate the GET data for ERP customers.
		docRepository.getGstr2AErpProcCall(dto.getGstin(),
				dto.getReturnPeriod(), dto.getType(), batchId);
	}

	private void handleStagingGetGstr2a(List<Long> resultIds,
			Gstr1GetInvoicesReqDto dto, Long batchId) {
		TenantContext.setTenantId(dto.getGroupcode());
		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());

		if (resultIds != null && !resultIds.isEmpty()) {
			if (APIConstants.ISD.equalsIgnoreCase(dto.getType())) {
				getGstr2aStagingIsdInvoicesRepository.softlyDeleteIsdHeader(
						dto.getGstin(), dto.getReturnPeriod(), now);
			} else {
				getGstr2aStagingIsdaInvoicesRepository.softlyDeleteIsdaHeader(
						dto.getGstin(), dto.getReturnPeriod(), now);
			}
		}
		resultIds.forEach(id -> {
			String apiResp = APIInvokerUtil.getResultById(id);
			// Long batchId = null;
			if (APIConstants.ISD.equalsIgnoreCase(dto.getType())) {
				List<GetGstr2aStagingIsdInvoicesHeaderEntity> atEnties = gstr2aDataParser
						.parseIsdData(dto, apiResp, dto.getType(), batchId);
				if (!atEnties.isEmpty()) {
					getGstr2aStagingIsdInvoicesRepository.saveAll(atEnties);
				}
			} else {
				List<GetGstr2aStagingIsdaInvoicesHeaderEntity> ataEnties = gstr2aDataParser
						.parseIsdaData(dto, apiResp, dto.getType(), batchId);
				if (!ataEnties.isEmpty()) {
					getGstr2aStagingIsdaInvoicesRepository.saveAll(ataEnties);
				}
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