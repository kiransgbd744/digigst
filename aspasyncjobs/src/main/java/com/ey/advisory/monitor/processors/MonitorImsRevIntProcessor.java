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
import com.ey.advisory.app.data.repositories.client.asprecon.ImsERPRequestRepository;
import com.ey.advisory.app.ims.handlers.ImsERPRequestEntity;
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
 * @author Ravindra V S
 *
 */
@Slf4j
@Service("MonitorImsRevIntProcessor")
public class MonitorImsRevIntProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	@Qualifier("imsERPRequestRepository")
	private ImsERPRequestRepository revIntCheckRepo;

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

	private static List<String> statusList = Arrays.asList("FAILED",
			"JOB_POSTED", "INITIATED", "INPROGRESS");

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {

		Long batchId = null;
		String gstins = null;
		ImsERPRequestEntity entity = null;

		try {

			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug(
						"inside MonitorImsRevIntProcessor "
								+ "group code {} : ",
						TenantContext.getTenantId());
			}


			String gstin = null;
			List<ImsERPRequestEntity> listToProceed = revIntCheckRepo
					.findByStatusIn(statusList);

			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug(
						"inside MonitorImsRevIntProcessor "
								+ "group code {}  and listToProceed {}: ",
						TenantContext.getTenantId(), listToProceed);
			}

			Map<String, List<ImsERPRequestEntity>> gstinStatusMap = listToProceed
					.stream()
					.collect(Collectors.groupingBy(o -> o.getGstin() + "_"
							+ o.getSupplyType()));

			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug(
						"MonitorImsRevIntProcessor "
								+ "group code {}  and gstinStatusMap {}: ",
						TenantContext.getTenantId(), gstinStatusMap);
			}

			for (String eligCombination : gstinStatusMap.keySet()) {

				String combKey[] = eligCombination.split("_");

				gstin = combKey[0];

				gstins = gstin;

				List<ImsERPRequestEntity> sortedList = gstinStatusMap
						.get(eligCombination);

				if (LOGGER.isDebugEnabled()) {

					LOGGER.debug(
							"MonitorImsRevIntProcessor "
									+ "group code {}  and before sorting {}: ",
							TenantContext.getTenantId(), sortedList);
				}

				sortedList.sort(Comparator
						.comparing(ImsERPRequestEntity::getBatchId));

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
						String msg = String.format("Ims "
								+ "Reverse Integartion is either in"
								+ " porogress or job posted for gstin %s, "
								+ "SuppyType %s, "
								+ "batchId %d Hence skipping the "
								+ "reverse integation.", entity.getGstin(),
								entity.getSupplyType(),
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

					
					submitReverseIntgJob(batchId, gstin);
					
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Submitted reverse Intg Job for  GSTIN :%s,"
										+ "and batchId :%d",
								gstin, batchId);
						LOGGER.debug(msg);
					}

					// adding new status for posted job not to pick again
					revIntCheckRepo.updateStatus(batchId, gstin, "JOB_POSTED");

					

				} else {
					LOGGER.error("MonitorImsRevIntProcessor one of "
							+ "the prior ERP reverse integration "
							+ "is inprogress, for gstin %s and batchId %d"
							+ " Hence not scheduling furthur "
							+ "revere integration", gstin, batchId);
				}

			}
		} catch (Exception e) {

			String msg = String.format("MonitorImsRevIntProcessor"
					+ " - error occured  gstin {}, "
					+ "Hence not scheduling furthur revere integration " + ": ",
					gstins);

			LOGGER.error(msg, e);
			revIntCheckRepo.updateStatus(batchId, gstins, entity.getStatus());
			throw new AppException(e);

		}

	}

	

	private boolean submitReverseIntgJob(Long batchId, String gstin) {

		try {

			Long scenarioId = scenarioMasterRepo.findSceIdOnScenarioName(
					JobConstants.IMS_REV_INTG);

			if (scenarioId == null) {

				LOGGER.error("Scenario {} is not configured for group {},"
						+ "Hence reverse integartion job is not posted for {}",
						JobConstants.IMS_REV_INTG,
						TenantContext.getTenantId(), gstin);

				return false;
			}

			ErpEventsScenarioPermissionEntity scenarioPermisionObj = evenErpScenPermissionRepo
					.findByScenarioIdAndIsDeleteFalse(scenarioId);


			if (scenarioPermisionObj == null) {

				LOGGER.error("Event Scenario permission {} is not configured "
						+ "for group {},Hence reverse integartion job is "
						+ "not posted for {}",
						JobConstants.IMS_REV_INTG,
						TenantContext.getTenantId(), gstin);

				return false;
			}


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
						.setScenarioName(JobConstants.IMS_REV_INTG);
				erpReqDto.setScenarioId(scenarioId);
				erpReqDto.setErpId(scenarioPermisionObj.getErpId());
				erpReqDto
						.setDestinationName(scenarioPermisionObj.getDestName());
				erpReqDto.setDestinationType("SAP");
				String erpReqJson = gson.toJson(erpReqDto);

				asyncJobsService.createJob(TenantContext.getTenantId(),
						JobConstants.IMS_REV_INTG, erpReqJson,
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


}
