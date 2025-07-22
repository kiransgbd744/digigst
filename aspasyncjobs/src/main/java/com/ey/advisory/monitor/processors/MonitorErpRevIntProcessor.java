/**
 * 
 */
package com.ey.advisory.monitor.processors;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.ErpEventsScenarioPermissionEntity;
import com.ey.advisory.admin.data.repositories.client.ErpEventsScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.ErpInfoEntityRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioMasterRepository;
import com.ey.advisory.app.data.entities.client.asprecon.AutoRecon2AERPRequestEntity;
import com.ey.advisory.app.data.repositories.client.AutoReconStatusRepository;
import com.ey.advisory.app.data.repositories.client.GstinSourceInfoRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.AutoRecon2AERPRequestRepository;
import com.ey.advisory.app.services.asprecon.gstr2a.autorecon.GSTR2aAutoReconRevIntgReqDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.FailedBatchAlertUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Service("MonitorErpRevIntProcessor")
public class MonitorErpRevIntProcessor extends DefaultMultiTenantTaskProcessor {

	@Autowired
	AutoReconStatusRepository autoReconStatusRepo;

	@Autowired
	@Qualifier("AutoRecon2AERPRequestRepository")
	AutoRecon2AERPRequestRepository revIntCheckRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("GstinSourceInfoRepository")
	private GstinSourceInfoRepository gstinSourceInfoRepository;

	@Autowired
	@Qualifier("ErpInfoEntityRepository")
	private ErpInfoEntityRepository erpInfoEntityRepository;

	@Autowired
	private ErpEventsScenarioPermissionRepository erpEventsScenPermissionRepo;

	@Autowired
	private ErpScenarioMasterRepository scenarioMasterRepo;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	private static final String CONF_KEY = "gstr2.recon.erp.sftp.revInt";
	private static final String CONF_CATEG = "Recon_Reverese_Push";

	private static List<String> statusList = Arrays.asList("FAILED", "WAITING",
			"INITIATED", "Job_Posted");

	@Autowired
	FailedBatchAlertUtility failedBatAltUtility;
	
	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {

		Long configId = null;
		String gstins = null;
		AutoRecon2AERPRequestEntity entity = null;

		try {

			if (!isErpRevIntSelected(TenantContext.getTenantId())) {

				LOGGER.error(
						"inside MonitorErpRevIntProcessor 2APR -not "
						+ "selected for revere integration, "
								+ "group code {} : ",
						TenantContext.getTenantId());

				return;
			}

			List<AutoRecon2AERPRequestEntity> listToProceed = revIntCheckRepo
					.findByStatusIn(statusList);

			if (listToProceed.isEmpty()) {
				LOGGER.debug(
						"MonitorErpRevIntProcessor 2APR ERP reverse "
						+ " integration nothing is available Hence not "
								+ "scheduling furthur "
								+ "revere integration");

				return;
			}

			Map<String, List<AutoRecon2AERPRequestEntity>> gstinStatusMap = 
					listToProceed
					.stream().collect(Collectors.groupingBy(o -> o.getGstin()));

			for (String gstin : gstinStatusMap.keySet()) {
				gstins = gstin;

				List<AutoRecon2AERPRequestEntity> sortedList = gstinStatusMap
						.get(gstin);

				sortedList.sort(Comparator.comparing(
						AutoRecon2AERPRequestEntity::getReconConfigID));

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"sortedList :%s for reverse Intg Job for  GSTIN"
							+ ":%s, and configId :%d",
							sortedList, gstin, configId);
					LOGGER.debug(msg);
				}

				entity = sortedList.get(0);

				configId = entity.getReconConfigID();

				// checking for job posted
				boolean revIntEligible = isRevIntEligible(configId, gstins);

				if (revIntEligible) {
					
					//checking for stuck status 

					if (entity.getStatus().equalsIgnoreCase("Job_Posted")
							&& entity.getUpdatedOn() != null) {
						
						LocalDateTime fourHoursAgo = LocalDateTime.now()
								.minus(4, ChronoUnit.HOURS);
						if (entity.getUpdatedOn().isBefore(fourHoursAgo)) {
							
							failedBatAltUtility.prepareAndTriggerAlert(
									String.valueOf(entity.getReconConfigID()),
									"Auto Recon 2A-RevIntg", "job has Stuck for long");
						}
					}
					
					if (entity.getStatus().equalsIgnoreCase("FAILED")
							|| entity.getStatus().equalsIgnoreCase("WAITING")
							|| entity.getStatus()
									.equalsIgnoreCase("INITIATED")) {

						if (LOGGER.isDebugEnabled()) {
							String msg = String.format(
									"Submitting reverse Intg Job for  GSTIN :%s,"
											+ "and configId :%d",
									gstin, configId);
							LOGGER.debug(msg);
						}

						boolean submitReverseIntgJob = submitReverseIntgJob(
								configId, gstin, null);

						// adding new status for posted job not to pick again
						if (submitReverseIntgJob)
							revIntCheckRepo.updateStatus(configId, gstin,
									"Job_Posted");

						if (LOGGER.isDebugEnabled()) {
							String msg = String.format(
									"Submitted reverse Intg Job for "
									+ " GSTIN :%s, and configId :%d",
									gstin, configId);
							LOGGER.debug(msg);
						}
					}

				} else {
					LOGGER.error("MonitorErpRevIntProcessor 2APR - one of "
							+ "the prior ERP reverse integration "
							+ "is inprogress for configId {}, gstin {}, "
							+ "Hence not scheduling furthur "
							+ "revere integration", configId.toString(), gstin);
				}

			}
		} catch (Exception e) {

			LOGGER.error(
					"2APR - erro occured  gstin {}, "
							+ "Hence not scheduling furthur revere integration",
					gstins);
			String status = (entity != null && entity.getStatus() != null) ? entity.getStatus() : "";
			revIntCheckRepo.updateStatus(configId, gstins, status);
			
			throw new AppException(e);

		}

	}

	private boolean submitReverseIntgJob(Long configId, String gstin,
			Long autoReconId) {

		try {

			/*
			 * Check for Reverse integration on-board
			 */
			Long scenarioId = scenarioMasterRepo.findSceIdOnScenarioName(
					JobConstants.GSTR2A_AUTO_RECON_REV_INTG);

			if (scenarioId == null) {

				LOGGER.error("Scenario {} is not configured for group {},"
						+ "Hence reverse integartion job is not posted for {}",
						JobConstants.GSTR2A_AUTO_RECON_REV_INTG,
						TenantContext.getTenantId(), gstin);

				return false;
			}

			// look up into new table with gstin ,
			// if no record is there log the error and return
			// take source id, look up based on system_ID on erp_info table take
			// Id
			// pass this id instead of hardcoded 1L in next method.

			String sourceId = gstinSourceInfoRepository.findByGstin(gstin);

			if (sourceId == null) {

				LOGGER.error("sourceId {} is not configured for group {},"
						+ "Hence reverse integartion job is not posted for {}",
						JobConstants.GSTR2A_AUTO_RECON_REV_INTG,
						TenantContext.getTenantId(), gstin);

				return false;
			}

			Long erpId = erpInfoEntityRepository.getErpId(sourceId);

			if (erpId == null) {

				LOGGER.error("erpId {} is not configured for group {},"
						+ "Hence reverse integartion job is not posted for {}",
						JobConstants.GSTR2A_AUTO_RECON_REV_INTG,
						TenantContext.getTenantId(), gstin);

				return false;
			}

			/*
			 * ErpEventsScenarioPermissionEntity scenarioPermision =
			 * erpEventsScenPermissionRepo
			 * .findByScenarioIdAndErpIdAndIsDeleteFalse(scenarioId, 1l);
			 */

			ErpEventsScenarioPermissionEntity scenarioPermision =
					erpEventsScenPermissionRepo
					.findByScenarioIdAndErpIdAndIsDeleteFalse(scenarioId,
							erpId);

			if (scenarioPermision == null) {

				LOGGER.error(
						"Scenario permission {} is not configured for group {},"
								+ "Hence reverse integartion job is "
								+ "not posted for {}",
						JobConstants.GSTR2A_AUTO_RECON_REV_INTG,
						TenantContext.getTenantId(), gstin);

				return false;
			}

			/*
			 * Submit the Async job only if the Reverse integration is
			 * on-boarded - Client level
			 */

			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Submitting reverse Intg Job for  GSTIN :%s,"
								+ "and autoReconId :%d", gstin, configId);
				LOGGER.debug(msg);
			}

			autoReconStatusRepo.updateERPPushStatus("INITIATED", null,
					autoReconId);

			Gson gson = GsonUtil.newSAPGsonInstance();
			GSTR2aAutoReconRevIntgReqDto erpReqDto = 
					new GSTR2aAutoReconRevIntgReqDto();
			erpReqDto.setGstin(gstin);
			erpReqDto.setConfigId(configId);
			erpReqDto.setAutoReconId(autoReconId);
			erpReqDto.setScenarioName(JobConstants.GSTR2A_AUTO_RECON_REV_INTG);
			erpReqDto.setScenarioId(scenarioId);
			erpReqDto.setErpId(scenarioPermision.getErpId());
			erpReqDto.setDestinationName(scenarioPermision.getDestName());
			erpReqDto.setDestinationType("SAP");
			String erpReqJson = gson.toJson(erpReqDto);

			asyncJobsService.createJob(TenantContext.getTenantId(),
					JobConstants.GSTR2A_AUTO_RECON_REV_INTG, erpReqJson,
					JobConstants.SYSTEM, JobConstants.PRIORITY,
					JobConstants.PARENT_JOB_ID,
					JobConstants.SCHEDULE_AFTER_IN_MINS);
			return true;
		} catch (Exception ex) {
			String msg = String.format(
					"Exception while submitting reverse Intg Job for "
							+ " GSTIN :%s,and autoReconId :%d",
					gstin, configId);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	private boolean isErpRevIntSelected(String groupCode) {
		Config config = configManager.getConfig(CONF_CATEG, CONF_KEY,
				groupCode);
		if (config != null && config.getValue().equalsIgnoreCase("false")) {

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("isErpRevIntSelected config :%s",
						config.getValue());
				LOGGER.debug(msg);
			}

			return false;
		} else {
			return true;
		}
	}

	private boolean isRevIntEligible(Long configId, String gstin) {

		List<String> statusList = Arrays.asList("Job_Posted");
		List<AutoRecon2AERPRequestEntity> list = revIntCheckRepo
				.findByGstinAndReconConfigIDLessThanAndStatusIn(gstin, configId,
						statusList);

		return list.isEmpty();
	}
	
	
	

}
