/**
 * 
 */
package com.ey.advisory.monitor.processors;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.ErpEventsScenarioPermissionEntity;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.ErpEventsScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.ErpInfoEntityRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioMasterRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.GstinSourceInfoRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.InwardEInvoiceERPRequestRepository;
import com.ey.advisory.app.inward.einvoice.InwardEInvoiceERPRequestEntity;
import com.ey.advisory.app.inward.einvoice.InwardEInvoiceRevIntgReqDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.core.config.ConfigManager;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Service("MonitorInwardEInvoiceRevIntProcessor")
public class MonitorInwardEInvoiceRevIntProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	@Qualifier("InwardEInvoiceERPRequestRepository")
	InwardEInvoiceERPRequestRepository revIntCheckRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("ErpInfoEntityRepository")
	private ErpInfoEntityRepository erpInfoEntityRepository;

	@Autowired
	private ErpScenarioMasterRepository scenarioMasterRepo;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepo;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository onboardingRepo;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	private ErpEventsScenarioPermissionRepository evenErpScenPermissionRepo;
	
	@Autowired
	@Qualifier("GstinSourceInfoRepository")
	private GstinSourceInfoRepository gstinSourceInfoRepository;

	/*
	 * private static final String CONF_KEY = "inward.eInvoice.erp.sftp.revInt";
	 * private static final String CONF_CATEG = "Inward_EInvoice_Push";
	 */

	private static List<String> statusList = Arrays.asList("FAILED",
			"JOB_POSTED", "INITIATED", "INPROGRESS");

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {

		Long batchId = null;
		String gstins = null;
		InwardEInvoiceERPRequestEntity entity = null;
		boolean submitReverseIntgJob  = false;

		try {

			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug(
						"inside MonitorInwardEInvoiceRevIntProcessor "
								+ "group code {} : ",
						TenantContext.getTenantId());
			}

			/*
			 * if (!isErpRevIntSelected(TenantContext.getTenantId())) {
			 * 
			 * LOGGER.error("inside MonitorInwardEInvoiceRevIntProcessor " +
			 * "Inward EInvoice revere integration not selected for , " +
			 * "group code {} : ", TenantContext.getTenantId());
			 * 
			 * return; }
			 */

			String gstin = null;
			List<InwardEInvoiceERPRequestEntity> listToProceed = revIntCheckRepo
					.findByStatusIn(statusList);

			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug(
						"inside MonitorInwardEInvoiceRevIntProcessor "
								+ "group code {}  and listToProceed {}: ",
						TenantContext.getTenantId(), listToProceed);
			}

			Map<String, List<InwardEInvoiceERPRequestEntity>> gstinStatusMap = listToProceed
					.stream()
					.collect(Collectors.groupingBy(o -> o.getGstin() + "_"
							+ o.getSupplyType() + "_" + o.getTaxPeriod()));

			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug(
						"MonitorInwardEInvoiceRevIntProcessor "
								+ "group code {}  and gstinStatusMap {}: ",
						TenantContext.getTenantId(), gstinStatusMap);
			}

			for (String eligCombination : gstinStatusMap.keySet()) {

				String combKey[] = eligCombination.split("_");

				gstin = combKey[0];

				gstins = gstin;

				List<InwardEInvoiceERPRequestEntity> sortedList = gstinStatusMap
						.get(eligCombination);

				if (LOGGER.isDebugEnabled()) {

					LOGGER.debug(
							"MonitorInwardEInvoiceRevIntProcessor "
									+ "group code {}  and before sorting {}: ",
							TenantContext.getTenantId(), sortedList);
				}

				sortedList.sort(Comparator
						.comparing(InwardEInvoiceERPRequestEntity::getBatchId));

				if (LOGGER.isDebugEnabled()) {
					String msg = String
							.format("sortedList :%s for reverse Intg Job for "
									+ " GSTIN :%s,", sortedList, gstin);
					LOGGER.debug(msg);
				}

				entity = sortedList.get(0);

				if (entity.getStatus().equalsIgnoreCase("JOB_POSTED")
						|| entity.getStatus().equalsIgnoreCase("INPROGRESS")) {

					if (LOGGER.isDebugEnabled()) {
						String msg = String.format("Inward EInvoice "
								+ "Reverse Integartion is either in"
								+ " porogress or job posted for gstin %s, "
								+ "SuppyType %s, taxPeriod %s and "
								+ "batchId %d Hence skipping the "
								+ "reverse integation.", entity.getGstin(),
								entity.getSupplyType(), entity.getTaxPeriod(),
								entity.getBatchId());
						LOGGER.debug(msg);
					}
					continue;
				}

				batchId = entity.getBatchId();

				if (entity.getStatus().equalsIgnoreCase("FAILED")
						|| entity.getStatus().equalsIgnoreCase("INITIATED")) {

					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Submitting reverse Intg Job for  GSTIN :%s,"
										+ "and configId :%d",
								gstin, batchId);
						LOGGER.debug(msg);
					}

					Long entityId = gstinInfoRepo
							.findEntityIdByGstin(entity.getGstin());

					String answer = getOptedAnser(entityId);
					if (answer.equalsIgnoreCase("A")) {

						LOGGER.error(
								"inside MonitorInwardEInvoiceRevIntProcessor "
										+ "Inward EInvoice revere integration "
										+ "not selected for , "
										+ "group code {} : ",
								TenantContext.getTenantId());

						return;
					} else if (answer.equalsIgnoreCase("B")) {

						 submitReverseIntgJob = submitReverseIntgJob(batchId, gstin);
						
						if (LOGGER.isDebugEnabled()) {
							String msg = String.format(
									"Submitted reverse Intg Job for  GSTIN :%s,"
											+ "and batchId :%d",
									gstin, batchId);
							LOGGER.debug(msg);
						}
					} else {

						submitReverseIntgJob = submitSFTPJob(batchId, gstin);
						
						if (LOGGER.isDebugEnabled()) {
							String msg = String.format(
									"Submitted SFTP Job for  GSTIN :%s,"
											+ "and batchId :%d",
									gstin, batchId);
							LOGGER.debug(msg);
						}
					}

					// adding new status for posted job not to pick again
					if(submitReverseIntgJob)
					revIntCheckRepo.updateStatus(batchId, gstin, "JOB_POSTED");

					

				} else {
					LOGGER.error("MonitorInwardEInvoiceRevIntProcessor one of "
							+ "the prior ERP reverse integration "
							+ "is inprogress, for gstin %s and batchId %d"
							+ " Hence not scheduling furthur "
							+ "revere integration", gstin, batchId);
				}

			}
		} catch (Exception e) {

			String msg = String.format("MonitorInwardEInvoiceRevIntProcessor"
					+ " - error occured  gstin {}, "
					+ "Hence not scheduling furthur revere integration " + ": ",
					gstins);

			LOGGER.error(msg, e);
			revIntCheckRepo.updateStatus(batchId, gstins, entity.getStatus());
			throw new AppException(e);

		}

	}

	private boolean submitSFTPJob(Long batchId, String gstin) {

		/*
		 * Check for SFTP Push on-board
		 */
		Long scenarioId = scenarioMasterRepo.findSceIdOnScenarioName(
				JobConstants.INWARD_IENVOICE_SFTP_PUSH);

		if (scenarioId == null) {

			LOGGER.error("Scenario {} is not configured for group {},"
					+ "Hence reverse SFTP Push job is not posted for {}",
					JobConstants.INWARD_IENVOICE_SFTP_PUSH,
					TenantContext.getTenantId(), gstin);

			return false;
		}
		ErpEventsScenarioPermissionEntity scenarioPermision = 
				evenErpScenPermissionRepo
				.findByScenarioIdAndIsDeleteFalse(scenarioId);

		if (scenarioPermision == null) {

			LOGGER.error("SFTP permission {} is not configured for group {},"
					+ "Hence SFTP reverse push job is " + "not posted for {}",
					JobConstants.INWARD_IENVOICE_SFTP_PUSH,
					TenantContext.getTenantId(), gstin);

			return false;
		}

		
		Gson gson = GsonUtil.newSAPGsonInstance();
		InwardEInvoiceRevIntgReqDto erpReqDto = new InwardEInvoiceRevIntgReqDto();
		erpReqDto.setGstin(gstin);
		erpReqDto.setBatchId(batchId);
		erpReqDto
				.setScenarioName(JobConstants.INWARD_IENVOICE_SFTP_PUSH);
		erpReqDto.setScenarioId(scenarioId);
		erpReqDto.setErpId(scenarioPermision.getErpId());
		erpReqDto
				.setDestinationName(scenarioPermision.getDestName());
		erpReqDto.setDestinationType("SAP");
		String erpReqJson = gson.toJson(erpReqDto);

		asyncJobsService.createJob(TenantContext.getTenantId(),
				JobConstants.INWARD_IENVOICE_SFTP_PUSH, erpReqJson,
				JobConstants.SYSTEM, JobConstants.PRIORITY,
				JobConstants.PARENT_JOB_ID,
				JobConstants.SCHEDULE_AFTER_IN_MINS);
		
		return true;

	}

	private boolean submitReverseIntgJob(Long batchId, String gstin) {

		try {

			// Check for Reverse integration on-board

			Long scenarioId = scenarioMasterRepo.findSceIdOnScenarioName(
					JobConstants.INWARD_EINVOICE_REV_INTG);

			if (scenarioId == null) {

				LOGGER.error("Scenario {} is not configured for group {},"
						+ "Hence reverse integartion job is not posted for {}",
						JobConstants.INWARD_EINVOICE_REV_INTG,
						TenantContext.getTenantId(), gstin);

				return false;
			}
			
			String sourceId = gstinSourceInfoRepository.findByGstin(gstin);

			if (sourceId == null) {

				LOGGER.error("sourceId {} is not configured for group {},"
						+ "Hence reverse integartion job is not posted for {}",
						JobConstants.INWARD_EINVOICE_REV_INTG,
						TenantContext.getTenantId(), gstin);

				return false;
			}

			Long erpId = erpInfoEntityRepository.getErpId(sourceId);

			if (erpId == null) {

				LOGGER.error("erpId {} is not configured for group {},"
						+ "Hence reverse integartion job is not posted for {}",
						JobConstants.INWARD_EINVOICE_REV_INTG,
						TenantContext.getTenantId(), gstin);

				return false;
			}

			ErpEventsScenarioPermissionEntity scenarioPermisionObj = 
					evenErpScenPermissionRepo
					.findByScenarioIdAndErpIdAndIsDeleteFalse(scenarioId,
							erpId);


			// -- eventScenarioPermission Table need to check with scenario id
			// and is delete is false

			if (scenarioPermisionObj == null) {

				LOGGER.error("Event Scenario permission {} is not configured "
						+ "for group {},Hence reverse integartion job is "
						+ "not posted for {}",
						JobConstants.INWARD_EINVOICE_REV_INTG,
						TenantContext.getTenantId(), gstin);

				return false;
			}

			/*
			 * Submit Async job only if the Reverse integration is on-boarded -
			 * Client level
			 */

			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Submitting reverse Intg Job for  GSTIN :%s,"
								+ "and batchId :%d", gstin, batchId);
				LOGGER.debug(msg);
			}

			if (scenarioPermisionObj != null) {

				Gson gson = GsonUtil.newSAPGsonInstance();
				InwardEInvoiceRevIntgReqDto erpReqDto = new InwardEInvoiceRevIntgReqDto();
				erpReqDto.setGstin(gstin);
				erpReqDto.setBatchId(batchId);
				erpReqDto
						.setScenarioName(JobConstants.INWARD_EINVOICE_REV_INTG);
				erpReqDto.setScenarioId(scenarioId);
				erpReqDto.setErpId(scenarioPermisionObj.getErpId());
				erpReqDto
						.setDestinationName(scenarioPermisionObj.getDestName());
				erpReqDto.setDestinationType("SAP");
				String erpReqJson = gson.toJson(erpReqDto);

				asyncJobsService.createJob(TenantContext.getTenantId(),
						JobConstants.INWARD_EINVOICE_REV_INTG, erpReqJson,
						JobConstants.SYSTEM, JobConstants.PRIORITY,
						JobConstants.PARENT_JOB_ID,
						JobConstants.SCHEDULE_AFTER_IN_MINS);
			}

			return true;
		} catch (Exception ex) {
			String msg = String
					.format("Exception while submitting reverse Intg Job for "
							+ " GSTIN :%s,and batchId :%d", gstin, batchId);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	/*
	 * private boolean isErpRevIntSelected(String groupCode) { Config config =
	 * configManager.getConfig(CONF_CATEG, CONF_KEY, groupCode); if (config !=
	 * null && config.getValue().equalsIgnoreCase("false")) {
	 * 
	 * if (LOGGER.isDebugEnabled()) { String msg =
	 * String.format("isErpRevIntSelected config :%s", config.getValue());
	 * LOGGER.debug(msg); }
	 * 
	 * return false; } else { return true; } }
	 */

	private String getOptedAnser(Long entityId) {

		String answer = onboardingRepo.findByEntityAutoInitiateGetCall(entityId,
				"Whether reverse feed of Inward E-invoice data is required?",
				"I51");
		return answer;
	}

}
