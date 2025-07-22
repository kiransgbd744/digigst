/**
 * 
 */
package com.ey.advisory.app.services.jobs.gstr2a;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.ey.advisory.app.data.entities.client.EinvGstinClientEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aB2bInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aB2baInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aErpB2bInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aErpB2baInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingB2bInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingB2baInvoicesHeaderEntity;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.EinvClientGstinRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2B2bInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2B2baInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2ErpB2bInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2ErpB2baInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2StagingB2bInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2StagingB2baInvoicesRepository;
import com.ey.advisory.app.docs.dto.erp.Get2AConsolidatedRevIntgDto;
import com.ey.advisory.app.docs.dto.erp.Get2ARevIntReqDto;
import com.ey.advisory.app.gstr2b.Gstr2BTaggingServiceImpl;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.gstnapi.APIInvokerUtil;
import com.ey.advisory.gstnapi.SuccessHandler;
import com.ey.advisory.gstnapi.SuccessResult;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mahesh.Golla
 *
 */
@Service("Gstr2aGetB2bSuccessHandler")
@Slf4j
public class Gstr2aGetB2bSuccessHandler implements SuccessHandler {

	@Autowired
	@Qualifier("gstr2aB2bB2baDataParserImpl")
	private Gstr2aB2bB2baDataParser gstr2aDataParser;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	private GetGstr2B2bInvoicesRepository getGstr2B2bInvoicesRepository;

	@Autowired
	private GetGstr2B2baInvoicesRepository getGstr2B2baInvoicesRepository;

	@Autowired
	private GetGstr2ErpB2bInvoicesRepository getGstr2ErpB2bInvoicesRepository;

	@Autowired
	private GetGstr2ErpB2baInvoicesRepository getGstr2ErpB2baInvoicesRepository;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Autowired
	private GetGstr2StagingB2bInvoicesRepository getGstr2StagingB2bInvoicesRepository;

	@Autowired
	private GetGstr2StagingB2baInvoicesRepository getGstr2StagingB2baInvoicesRepository;

	@Autowired
	private ErpEventsScenarioPermissionRepository erpEventsScenPermissionRepo;

	@Autowired
	private ErpScenarioMasterRepository scenarioMasterRepo;

	@Autowired
	private EinvClientGstinRepository einvClientGstinRepo;

	@Autowired
	private ErpScenarioPermissionRepository erpScenPermissionRepo;

	@Autowired
	private GSTNDetailRepository gstinDetailRepo;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	@Qualifier("Gstr2BTaggingServiceImpl")
	private Gstr2BTaggingServiceImpl gstr2BTaggingServiceImpl;

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
				LOGGER.debug("GET Call is Success for the batchId {} inside "
						+ "Anx2GetB2bSuccessHandler.java", batchId);
			}

			if (APIConstants.GSTR2A.equalsIgnoreCase(apiSection)) {
				boolean isFDeltaGetData = true;
				if (!isFDeltaGetData) {
					handleUserGetGstr2a(resultIds, dto, batchId);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"inside if block !isFDeltaGetData for batchId{}",
								batchId);
					}

				} else {
					handleStagingGetGstr2a(resultIds, dto, batchId);

					// Proc call to insert the Delta data into original tables
					docRepository.getGstr2aProcCall(dto.getGstin(),
							dto.getReturnPeriod(), dto.getType(), batchId,
							dto.getFromTime() != null ? true : false);

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"inside handleStagingGetGstr2a executed for batchId{}",
								batchId);
					}
					if ("sp0023"
							.equalsIgnoreCase(TenantContext.getTenantId())) {
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

						List<ErpScenarioPermissionEntity> scenarioPermisionList = erpScenPermissionRepo
								.findByScenarioIdAndGstinIdAndIsDeleteFalse(
										scenarioId, gstinId);
						if (!scenarioPermisionList.isEmpty()) {
							for (ErpScenarioPermissionEntity entity : scenarioPermisionList) {
								Get2AConsolidatedRevIntgDto consDto = new Get2AConsolidatedRevIntgDto();
								consDto.setDestinationName(
										entity.getDestName());
								consDto.setGstin(dto.getGstin());
								consDto.setGroupcode(
										TenantContext.getTenantId());
								consDto.setScenarioId(entity.getErpId());
								consDto.setBatchId(batchId);
								consDto.setErpId(entity.getErpId());
								consDto.setDestinationName(
										entity.getDestName());
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
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("groupcodes ofted for SFTP push {}",
									groupCodesOptedSFTPpush);
						}
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
									erpReqDto.setScenarioName(
											JobConstants.NEW_GSTR2A_GET_REV_INTG);
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
			if (resultIds.size() > 1) {
				isTokenResp = true;
			}
			
			gstr2BTaggingServiceImpl.callGstr2a2bMonitorProc(gstin,
					dto.getReturnPeriod(), 0L,
					APIConstants.GSTR2A.toUpperCase(),dto.getType().toUpperCase());
			
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
			if (APIConstants.B2B.equalsIgnoreCase(dto.getType())) {
				getGstr2B2bInvoicesRepository.softlyDeleteB2bHeader(
						dto.getGstin(), dto.getReturnPeriod(), now);
			} else {
				getGstr2B2baInvoicesRepository.softlyDeleteB2baHeader(
						dto.getGstin(), dto.getReturnPeriod(), now);
			}
		}

		resultIds.forEach(id -> {
			String apiResp = APIInvokerUtil.getResultById(id);
			// Long batchId = null;
			if (APIConstants.B2B.equalsIgnoreCase(dto.getType())) {
				List<GetGstr2aStagingB2bInvoicesHeaderEntity> atEnties = gstr2aDataParser
						.parseB2bData(dto, apiResp, dto.getType(), batchId,
								now);
				Type listType = new TypeToken<List<GetGstr2aB2bInvoicesHeaderEntity>>() {
				}.getType();
				ModelMapper modelmapper = new ModelMapper();
				List<GetGstr2aB2bInvoicesHeaderEntity> postDtoList = modelmapper
						.map(atEnties, listType);
				postDtoList.forEach(postDto -> {
					postDto.setDeltaInStatus(APIConstants.N);
				});
				if (!atEnties.isEmpty()) {
					getGstr2B2bInvoicesRepository.saveAll(postDtoList);
					// saveEinvClientData(atEnties);
				}
			} else {
				List<GetGstr2aStagingB2baInvoicesHeaderEntity> ataEnties = gstr2aDataParser
						.parseB2baData(dto, apiResp, dto.getType(), batchId);
				Type listType = new TypeToken<List<GetGstr2aB2baInvoicesHeaderEntity>>() {
				}.getType();
				ModelMapper modelmapper = new ModelMapper();
				List<GetGstr2aB2baInvoicesHeaderEntity> postDtoList = modelmapper
						.map(ataEnties, listType);
				postDtoList.forEach(postDto -> {
					postDto.setDeltaInStatus(APIConstants.N);
				});
				if (!ataEnties.isEmpty()) {
					getGstr2B2baInvoicesRepository.saveAll(postDtoList);
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
			if (APIConstants.B2B.equalsIgnoreCase(dto.getType())) {
				getGstr2ErpB2bInvoicesRepository.softlyDeleteB2bHeader(
						dto.getGstin(), dto.getReturnPeriod(), now);
			} else {
				getGstr2ErpB2baInvoicesRepository.softlyDeleteB2baHeader(
						dto.getGstin(), dto.getReturnPeriod(), now);
			}
		}

		resultIds.forEach(id -> {
			String apiResp = APIInvokerUtil.getResultById(id);
			// Long batchId = null;
			if (APIConstants.B2B.equalsIgnoreCase(dto.getType())) {
				List<GetGstr2aStagingB2bInvoicesHeaderEntity> atEnties = gstr2aDataParser
						.parseB2bData(dto, apiResp, dto.getType(), batchId,
								now);
				Type listType = new TypeToken<List<GetGstr2aErpB2bInvoicesHeaderEntity>>() {
				}.getType();
				ModelMapper modelmapper = new ModelMapper();
				List<GetGstr2aErpB2bInvoicesHeaderEntity> postDtoList = modelmapper
						.map(atEnties, listType);

				postDtoList.forEach(postDto -> {
					postDto.setGetBatchId(batchId);
				});

				if (!atEnties.isEmpty()) {
					getGstr2ErpB2bInvoicesRepository.saveAll(postDtoList);
				}
			} else {
				List<GetGstr2aStagingB2baInvoicesHeaderEntity> ataEnties = gstr2aDataParser
						.parseB2baData(dto, apiResp, dto.getType(), batchId);

				Type listType = new TypeToken<List<GetGstr2aErpB2baInvoicesHeaderEntity>>() {
				}.getType();
				ModelMapper modelmapper = new ModelMapper();
				List<GetGstr2aErpB2baInvoicesHeaderEntity> postDtoList = modelmapper
						.map(ataEnties, listType);
				postDtoList.forEach(postDto -> {
					postDto.setGetBatchId(batchId);
				});

				if (!ataEnties.isEmpty()) {

					getGstr2ErpB2baInvoicesRepository.saveAll(postDtoList);
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
			if (APIConstants.B2B.equalsIgnoreCase(dto.getType())) {
				getGstr2StagingB2bInvoicesRepository.softlyDeleteB2bHeader(
						dto.getGstin(), dto.getReturnPeriod(), now);
			} else {
				getGstr2StagingB2baInvoicesRepository.softlyDeleteB2baHeader(
						dto.getGstin(), dto.getReturnPeriod(), now);
			}
		}

		resultIds.forEach(id -> {
			String apiResp = APIInvokerUtil.getResultById(id);
			// Long batchId = null;
			if (APIConstants.B2B.equalsIgnoreCase(dto.getType())) {
				List<GetGstr2aStagingB2bInvoicesHeaderEntity> atEnties = gstr2aDataParser
						.parseB2bData(dto, apiResp, dto.getType(), batchId,
								now);
				if (!atEnties.isEmpty()) {
					getGstr2StagingB2bInvoicesRepository.saveAll(atEnties);
					// saveEinvClientData(atEnties);
				}
			} else {
				List<GetGstr2aStagingB2baInvoicesHeaderEntity> ataEnties = gstr2aDataParser
						.parseB2baData(dto, apiResp, dto.getType(), batchId);
				if (!ataEnties.isEmpty()) {

					getGstr2StagingB2baInvoicesRepository.saveAll(ataEnties);
				}
			}

		});
	}

	private void saveEinvClientData(
			List<GetGstr2aStagingB2bInvoicesHeaderEntity> atEnties) {

		List<EinvGstinClientEntity> einvEligiblegstins = new ArrayList<>();
		Set<String> einvEliGstins = new HashSet<>();

		atEnties.forEach(e -> {
			String cancelDate = e.getCancelDate() + "";
			if (!Strings.isNullOrEmpty(e.getIrnNum())
					&& Strings.isNullOrEmpty(cancelDate)) {

				if (einvEliGstins.add(e.getCgstin())) {
					EinvGstinClientEntity entity = new EinvGstinClientEntity();
					entity.setGstin(e.getCgstin());
					entity.setCreatedDate(LocalDateTime.now());
					entity.setSource("2A");
					entity.setCancelledDate(e.getCancelDate() + "");
					entity.setPan(e.getCgstin().substring(2, 12));
					einvEligiblegstins.add(entity);
				}
			} else if ((!Strings.isNullOrEmpty(e.getIrnNum())
					&& !Strings.isNullOrEmpty(cancelDate))) {
				einvClientGstinRepo.updateCancellationDate(e.getCgstin(),
						cancelDate, LocalDateTime.now());
			}
		});

		if (!einvEligiblegstins.isEmpty())
			einvClientGstinRepo.saveAll(einvEligiblegstins);

	}

	private void checkForSFTPReverseIntegration(Gstr1GetInvoicesReqDto dto,
			Long batchId) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("inside SFTP job push block");
		}
		Long scenarioId = scenarioMasterRepo.findSceIdOnScenarioName(
				JobConstants.NEW_GSTR2A_GET_REV_INTG_SFTP);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("scenario id {}", scenarioId.toString());
		}
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

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("scenario permission {}", scenarioPermision);
		}

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