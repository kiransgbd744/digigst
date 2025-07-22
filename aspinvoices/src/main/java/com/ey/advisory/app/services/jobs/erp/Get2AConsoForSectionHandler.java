package com.ey.advisory.app.services.jobs.erp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.admin.data.entities.client.AnxErpSFTPBatchIdLogEntity;
import com.ey.advisory.admin.data.entities.client.ErpEventsScenarioPermissionEntity;
import com.ey.advisory.admin.data.entities.client.ErpInfoEntity;
import com.ey.advisory.admin.data.entities.client.ErpScenarioPermissionEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.AnxErpBatchRepository;
import com.ey.advisory.admin.data.repositories.client.AnxErpSFTPBatchIdLogRepository;
import com.ey.advisory.admin.data.repositories.client.ErpEventsScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.ErpInfoEntityRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioMasterRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.GetGstr2aB2bInvoicesHeaderRepository;
import com.ey.advisory.app.data.repositories.client.GetGstr2aB2baInvoicesHeaderRepository;
import com.ey.advisory.app.data.repositories.client.GetGstr2aCdnInvoicesHeaderRepository;
import com.ey.advisory.app.data.repositories.client.GetGstr2aCdnaInvoicesHeaderRepository;
import com.ey.advisory.app.data.repositories.client.GetGstr2aISDInvoicesHeaderRepository;
import com.ey.advisory.app.data.repositories.client.GetGstr2aImpgInvoicesHeaderRepository;
import com.ey.advisory.app.data.repositories.client.GetGstr2aImpgSezInvoicesHeaderRepository;
import com.ey.advisory.app.data.views.client.ConsolidatedGstr2ADto;
import com.ey.advisory.app.docs.dto.erp.Get2AConsoForSectionFinalDto;
import com.ey.advisory.app.docs.dto.erp.Get2ARevIntReqDto;
import com.ey.advisory.common.AnxErpBatchHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DestinationConnectivity;
import com.ey.advisory.common.ERPConstants;
import com.ey.advisory.common.FailedBatchAlertUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.gson.Gson;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

/**
 * 
 * @author Umesha.M
 *
 */
@Service("Get2AConsoForSectionHandler")
public class Get2AConsoForSectionHandler {

	@Autowired
	@Qualifier("Get2AConsoForSectionServiceImpl")
	private Get2AConsoForSectionServiceImpl consoForSectionServiceImpl;

	@Autowired
	private DestinationConnectivity connectivity;

	@Autowired
	@Qualifier("ErpEventsScenarioPermissionRepository")
	private ErpEventsScenarioPermissionRepository erpEventsScenPermiRep;

	@Autowired
	@Qualifier("ErpScenarioMasterRepository")
	private ErpScenarioMasterRepository erpScenarioMasterRepository;

	@Autowired
	private GSTNDetailRepository gstnRepo;

	@Autowired
	private ErpScenarioPermissionRepository erpScenPermissionRepo;

	@Autowired
	private AnxErpBatchHandler erpBatchHandler;

	@Autowired
	@Qualifier("GetGstr2aB2bInvoicesHeaderRepository")
	private GetGstr2aB2bInvoicesHeaderRepository b2bRepository;

	@Autowired
	@Qualifier("GetGstr2aB2baInvoicesHeaderRepository")
	private GetGstr2aB2baInvoicesHeaderRepository b2baRepository;

	@Autowired
	@Qualifier("GetGstr2aCdnInvoicesHeaderRepository")
	private GetGstr2aCdnInvoicesHeaderRepository cdnRepository;

	@Autowired
	@Qualifier("GetGstr2aCdnaInvoicesHeaderRepository")
	private GetGstr2aCdnaInvoicesHeaderRepository cdnaRepository;

	@Autowired
	@Qualifier("GetGstr2aISDInvoicesHeaderRepository")
	private GetGstr2aISDInvoicesHeaderRepository isdRepository;

	@Autowired
	@Qualifier("GetGstr2aImpgInvoicesHeaderRepository")
	private GetGstr2aImpgInvoicesHeaderRepository impgRepository;

	@Autowired
	@Qualifier("GetGstr2aImpgSezInvoicesHeaderRepository")
	private GetGstr2aImpgSezInvoicesHeaderRepository impgSezRepository;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Get2AConsoForSectionHandler.class);

	@Autowired
	private AsyncJobsService asyncJobsService;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	private ErpInfoEntityRepository erpInfoRepo;

	@Autowired
	private AnxErpBatchRepository batchRepo;

	@Autowired
	@Qualifier("Get2ASFTPConsoForSectionServiceImpl")
	private Get2ASFTPConsoForSectionService sftpService;

	@Autowired
	@Qualifier("Get2ASFTPConsoForSectionDaoImpl")
	private Get2ASFTPConsoForSectionDao sftpDaoImpl;

	@Autowired
	private GetAnx1BatchRepository anx1BatchRepo;

	@Autowired
	private AnxErpSFTPBatchIdLogRepository anx1ErpSftpBatchRepo;

	@Autowired
	FailedBatchAlertUtility failedBatAltUtility;
	
	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	private static final String B2B = "B2B";
	private static final String B2BA = "B2BA";
	private static final String CDN = "CDN";
	private static final String CDNA = "CDNA";
	private static final String ISD = "ISD";
	private static final String IMPG = "IMPG";
	private static final String IMPGSEZ = "IMPGSEZ";
	private static final String ECOM = "ECOM";
	private static final String ECOMA = "ECOMA";

	/**
	 * Whenever calling procedure we are getting gstin,Return Period,
	 * section,batchId and erpId
	 * 
	 * @param dto
	 * @return
	 */
	public Integer erpToGet2AConsoForSection(Get2ARevIntReqDto dto) {
		Integer respCode = 0;
		String scenarioName = null;
		Long batchId = null;
		try {
			String groupCode = dto.getGroupCode();
			scenarioName = dto.getScenarioName();
			String gstin = dto.getGstin();
			String retPeriod = dto.getRetPeriod();
			String section = dto.getSection();
			batchId = dto.getBatchId();
			Long erpId = dto.getErpId() != null ? dto.getErpId() : 1;
			Long jobId = dto.getJobId();

			TenantContext.setTenantId(groupCode);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Tenant Id: {}", TenantContext.getTenantId());
			}
			/*
			 * Getting Scenario Id based scenario name
			 * 
			 */
			Long scenarioId = erpScenarioMasterRepository
					.findSceIdOnScenarioName(scenarioName);

			/**
			 * Getting destination name for based on Scenario Id and ERP Id
			 * 
			 */
			ErpEventsScenarioPermissionEntity permEntity = erpEventsScenPermiRep
					.getEntityByScenarioIdAndErpId(scenarioId, erpId);

			/*
			 * Procedure call for chunking the ERP Batch Id into header tables
			 * because chunking will be applied to result set with max volume
			 * 
			 */

			// stamp the data if erp batch ids are not present.
			stampChunkId(groupCode, batchId, section);

			/**
			 * List of distinct ERP Batch IDs from header table based on
			 * GSTIN,Return Period, Section and Batch Id
			 */
			List<Long> erpBatchIds = consoForSectionServiceImpl
					.findErpBatchIds(gstin, retPeriod, section, batchId);

			int chunkNo = 0;
			if (scenarioName
					.equalsIgnoreCase("Get2AConsoForSectionSFTPRevIntg")) {

				if (!erpBatchIds.isEmpty()) {
					anx1ErpSftpBatchRepo.softDeleteBatchId(batchId);

					int fileIndex = 1;

					List<AnxErpSFTPBatchIdLogEntity> sftpStatusEntity = new ArrayList<>();

					for (Long erpBatchId : erpBatchIds) {
						AnxErpSFTPBatchIdLogEntity entity = new AnxErpSFTPBatchIdLogEntity();
						entity.setErpBatchId(erpBatchId);
						entity.setBatchId(batchId);
						entity.setGstin(gstin);
						entity.setTaxPeriod(retPeriod);
						entity.setSection(section);
						entity.setFileIndex(String.valueOf(fileIndex));
						entity.setSftpPushStatus("NOT_INITIATED");
						entity.setCreatedOn(LocalDateTime.now());
						sftpStatusEntity.add(entity);
						fileIndex++;
					}
					anx1ErpSftpBatchRepo.saveAll(sftpStatusEntity);
				}
			}

			if (erpBatchIds.isEmpty()) {
				String errMsg = String.format(
						"ERP Batch Id is empty for gstin %s section %s and groupCode %s",
						gstin, section, groupCode);
				LOGGER.error(errMsg);
			}
			/**
			 * Each ERP batch id/chunking id we need push the data to ERP.
			 */

			if (scenarioName
					.equalsIgnoreCase("Get2AConsoForSectionSFTPRevIntg")) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Inside SFTP push block for section %s and groupCode %s",
							section, groupCode);
				}
				int index = 1;
				Long erpBatchIdSftp = 0L;

				try {

					for (Long erpBatchId : erpBatchIds) {
						List<Object[]> objs = sftpDaoImpl
								.findSFTPGet2AConsoForSection(gstin, retPeriod,
										section, batchId, erpBatchId);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("erpbatchIds %s and groupCode %s",
									erpBatchIds.toString(), groupCode);
						}
						erpBatchIdSftp = erpBatchId;
						if (objs != null && !objs.isEmpty()) {
							List<ConsolidatedGstr2ADto> finalDto = sftpService
									.getConsolidatedItemList(section, objs);
							// currentBatchSi
							String fileName = sftpService.createSftpFileName(
									gstin, String.valueOf(index),
									String.valueOf(erpBatchIds.size()),
									retPeriod, section) + ".csv";

							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug(
										"Reverse push started for SFTP {} "
												+ "and erpId {} and itemSize {}",
										fileName, erpBatchId, finalDto.size());
							}

							respCode = sftpService.uploadToSftpServer(fileName,
									finalDto);

							++index;
						}

						anx1ErpSftpBatchRepo.updateSftpPushStatus(erpBatchId,
								"SUCCESS", batchId);

					}
					String status = (index - 1) + "/" + (erpBatchIds.size());
					anx1BatchRepo.updateSftpStatus(status, batchId);

				} catch (Exception ex) {
					failedBatAltUtility.prepareAndTriggerAlert(
							String.valueOf(batchId), "Get2A SFTP Rev Integ",
							String.format("Exception is %s", ex.getMessage()));
					anx1BatchRepo.updateSftpErrorStatus(
							(index - 1) + "/" + (erpBatchIds.size()), batchId,
							StringUtils.truncate(ex.getMessage(), 2048));
					anx1ErpSftpBatchRepo.updateSftpPushStatus(erpBatchIdSftp,
							"FAILED", batchId);

					LOGGER.error("Exception Occured: {} ", ex);
					throw new AppException("Exception Occured: {}", ex);

				}
			} else {
				for (Long erpBatchId : erpBatchIds) {
					
					chunkNo++;
					
					String chunkStatus = chunkNo + "/" + erpBatchIds.size();
					/**
					 * List of information for each section based GSTIN,Return
					 * Period, section
					 */
					Get2AConsoForSectionFinalDto finalDto = consoForSectionServiceImpl
							.findERPGet2AConsoForSection(gstin, retPeriod,
									section, batchId, erpBatchId);

					long currentBatchSize = 0;
					if (finalDto.getGet2AConsSecDtos() != null
							&& !finalDto.getGet2AConsSecDtos().isEmpty()) {
						currentBatchSize = finalDto.getGet2AConsSecDtos()
								.size();
						AnxErpBatchEntity batch = erpBatchHandler
								.createErpBatch(groupCode, null, gstin,
										permEntity.getDestName(), scenarioId,
										currentBatchSize, null,
										ERPConstants.EVENT_BASED_JOB,
										permEntity.getErpId(), null,
										APIConstants.SYSTEM.toUpperCase(),
										section.toUpperCase(), jobId, chunkStatus);

						// Setting chunk id to updated the records.
						batch.setId(erpBatchId);

						// http Status code assigningn 0 values
						batch.setHttpCode(0);

						// Setting batch
						batch.setSection(section.toUpperCase());

						// updating the batch table
						batch = batchRepo.save(batch);

						respCode = connectivity.pushToErp(finalDto,
								"Get2AConsoForSectionFinalDto", batch);

						// update the Is Sent ERP Date column as a current date
						// and
						// Is Sent ERP Flag as a true into header table for
						// section.
						LocalDate date = LocalDate.now();
						updateSentERP(respCode, gstin, retPeriod, section,
								batchId, permEntity, erpBatchId, date);
					}
				}
				// Create a another procedure call for meta data
				// information as a Group Code, Batch Id and ERP Id as a input
				// parameter.
				if (respCode == 200) {
					Long scenariosId = erpScenarioMasterRepository
							.findSceIdOnScenarioName(
									JobConstants.NEW_GSTR2A_GET_REV_INTG);
					// Assuming it as Event based job
					GSTNDetailEntity gstinInfo = gstnRepo
							.findByGstinAndIsDeleteFalse(gstin);
					Long gstinId = gstinInfo.getId();

					List<ErpScenarioPermissionEntity> scenarioPermisionList = erpScenPermissionRepo
							.findByScenarioIdAndGstinIdAndIsDeleteFalse(
									scenariosId, gstinId);
					if (!scenarioPermisionList.isEmpty()) {
						asyncCall(groupCode, batchId, erpId);
					}
				}
			}
		} catch (Exception e) {
			String errMsg = String.format(
					"Gstr2A Rev Integ Failed for batch id %s for group code %s",
					batchId, TenantContext.getTenantId());
			failedBatAltUtility.prepareAndTriggerAlert(
					String.valueOf(batchId), "Get2A Rev Integ",
					String.format("Exception is %s", e.getMessage()));
			LOGGER.error(errMsg, e);
			throw new AppException(errMsg, e);
		}
		return respCode;
	}

	private void updateSentERP(Integer respCode, String gstin, String retPeriod,
			String section, Long batchId,
			ErpEventsScenarioPermissionEntity permEntity, Long erpBatchId,
			LocalDate date) {
		if (respCode == 200 && B2B.equalsIgnoreCase(section)) {
			b2bRepository.updateSentErp(gstin, batchId, retPeriod, date,
					erpBatchId);
		}

		if (respCode == 200 && B2BA.equalsIgnoreCase(section)) {
			b2baRepository.updateSentErp(gstin, batchId, retPeriod, date,
					erpBatchId);
		}

		if (respCode == 200 && CDN.equalsIgnoreCase(section)) {
			cdnRepository.updateSentErp(gstin, batchId, retPeriod, date,
					erpBatchId);
		}

		if (respCode == 200 && CDNA.equalsIgnoreCase(section)) {
			cdnaRepository.updateSentErp(gstin, batchId, retPeriod, date,
					erpBatchId);
		}

		if (respCode == 200 && ISD.equalsIgnoreCase(section)) {
			isdRepository.updateSentErp(gstin, batchId, retPeriod, date,
					erpBatchId);
		}
		if (respCode == 200 && IMPG.equalsIgnoreCase(section)) {
			impgRepository.updateSentErp(gstin, batchId, retPeriod, date,
					erpBatchId);
		}
		if (respCode == 200 && IMPGSEZ.equalsIgnoreCase(section)) {
			impgSezRepository.updateSentErp(gstin, batchId, retPeriod, date,
					erpBatchId);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Destination: {}", permEntity.getDestName());
			LOGGER.debug("Response Code:{}", respCode);
		}
	}

	private AsyncExecJob asyncCall(String groupCode, final Long batchId,
			final Long erpId) {
		Get2ARevIntReqDto get2aRev = new Get2ARevIntReqDto();
		get2aRev.setBatchId(batchId);
		ErpInfoEntity erpInfoEntity = erpInfoRepo.getEntityByErpId(erpId);
		get2aRev.setSourceId(erpInfoEntity.getSystemId());
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		String jsonParam = gson.toJson(get2aRev);
		return asyncJobsService.createJob(groupCode,
				JobConstants.GET2A_METADATA_REV_PROC, jsonParam,
				JobConstants.SYSTEM, JobConstants.PRIORITY,
				JobConstants.PARENT_JOB_ID,
				JobConstants.SCHEDULE_AFTER_IN_MINS);
	}

	private int stampChunkId(final String groupCode, final Long getBatchId,
			String section) {

		int count = 0;
		String procedureName = null;
		if (B2B.equalsIgnoreCase(section)) {
			procedureName = "USP_CHUNK_GETGSTR2A_B2B";
		} else if (B2BA.equalsIgnoreCase(section)) {
			procedureName = "USP_CHUNK_GETGSTR2A_B2BA";
		} else if (CDN.equalsIgnoreCase(section)) {
			procedureName = "USP_CHUNK_GETGSTR2A_CDN";
		} else if (CDNA.equalsIgnoreCase(section)) {
			procedureName = "USP_CHUNK_GETGSTR2A_CDNA";
		} else if (ISD.equalsIgnoreCase(section)) {
			procedureName = "USP_CHUNK_GETGSTR2A_ISD";
		} else if (IMPG.equalsIgnoreCase(section)) {
			procedureName = "USP_CHUNK_GETGSTR2A_IMPG";
		} else if (ECOM.equalsIgnoreCase(section)) {
			procedureName = "USP_CHUNK_GETGSTR2A_ECOM";
		} else if (ECOMA.equalsIgnoreCase(section)) {
			procedureName = "USP_CHUNK_GETGSTR2A_ECOMA";
		} else {
			procedureName = "USP_CHUNK_GETGSTR2A_IMPGSEZ";
		}
		// As a part of--136789--US--begin
		Map<String, Config> configMap = configManager
				.getConfigs("GET2A2BREVINTEG", "get2a.rev.intg.chunk.limit");

		String chunkSizenum = configMap
				.get("get2a.rev.intg.chunk.limit") == null ? "1000"
						: String.valueOf(configMap
								.get("get2a.rev.intg.chunk.limit").getValue());
		int chunkSize = Integer.parseInt(chunkSizenum);
		// As a part of--136789--US--end
		StoredProcedureQuery storedProcQuery = entityManager
				.createStoredProcedureQuery(procedureName);
		storedProcQuery.registerStoredProcedureParameter("IP_GROUP_CODE",
				String.class, ParameterMode.IN);
		storedProcQuery.registerStoredProcedureParameter("IP_GET_BATCH_ID",
				Long.class, ParameterMode.IN);
		
		storedProcQuery.setParameter("IP_GROUP_CODE", groupCode);
		storedProcQuery.setParameter("IP_GET_BATCH_ID", getBatchId);
		// As a part of--136789--US Begin
		storedProcQuery.registerStoredProcedureParameter("IP_CHUNK_SPILIT_VAL",
				Integer.class, ParameterMode.IN);
		storedProcQuery.setParameter("IP_CHUNK_SPILIT_VAL", chunkSize);
		// As a part of--136789--US--end
		storedProcQuery.execute();
		count = 1 + count;
		return count;
	}
}
