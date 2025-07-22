package com.ey.advisory.app.ims.handlers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.asprecon.ControlGstnGetIrnStatusEntity;
import com.ey.advisory.app.data.entities.client.asprecon.MonitorGstnGetIrnStatusEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ControlGstnGetIrnStatusRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.InwardEInvoiceERPRequestRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.MonitorGstnGetIrnStatusRepository;
import com.ey.advisory.app.inward.einvoice.InwardEInvoiceERPRequestEntity;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@Slf4j
@Service("GetIrnMonitorControlServiceImpl")
public class GetIrnMonitorControlServiceImpl
		implements GetIrnMonitorControlService {

	@Autowired
	@Qualifier("ControlGstnGetIrnStatusRepository")
	private ControlGstnGetIrnStatusRepository controlGstnGetIrnStatusRepo;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;
	@Autowired
	@Qualifier("MonitorGstnGetIrnStatusRepository")
	private MonitorGstnGetIrnStatusRepository monitorGstnGetIrnStatusRepo;
	
	@Autowired
	private InwardEInvoiceERPRequestRepository erpRepo;

	
	@Autowired
	AsyncJobsService asyncJobsService;

	@Override
	public void monitorControlEntries(
			List<MonitorGstnGetIrnStatusEntity> monitorEntities) {

		String gstin = null;
		String taxPeriod = null;
		Long invocationId = null;
		Long monitorId = null;
		boolean isAuto = false;
		String groupCode = TenantContext.getTenantId();
		for (MonitorGstnGetIrnStatusEntity monitorEntity : monitorEntities) {

			try {
				gstin = monitorEntity.getGstin();
				taxPeriod = monitorEntity.getTaxPeriod();
				invocationId = monitorEntity.getInvocationId();
				monitorId = monitorEntity.getMonitorId();
				isAuto = monitorEntity.isAutoRequest();

				List<ControlGstnGetIrnStatusEntity> controlEntities = controlGstnGetIrnStatusRepo
						.findByMonitorId(monitorId);

				if (LOGGER.isDebugEnabled()) {
					String logMsg = String.format(
							"There are %s records in Control table for "
									+ "group: '%s' with monitorId: %s",
							controlEntities.size(), monitorId, groupCode);
					LOGGER.debug(logMsg);
				}
				Set<String> jobStatuses = filterOnlyJobStatus(controlEntities);
				if (jobStatuses.contains("FAILED")) {
					// update only batch as failed and continue
					String logMsg = String.format(
							"There are 'FAILED' records in Control table for"
									+ " group: '%s' with monitorId: %s ,"
									+ " gstin: %s and taxPeriod: %s ."
									+ " Hence marking Failed in Batch Table",
							monitorId, groupCode, gstin, taxPeriod);
					LOGGER.error(logMsg);
					
					batchRepo.updateGstr2bStatus(APIConstants.FAILED,
							monitorEntity.getInvocationId(), logMsg,
							"ER-1000");

					monitorGstnGetIrnStatusRepo.updateStatus(monitorId,
							LocalDateTime.now(), true);

				}
				if (jobStatuses.contains("INPROGRESS")
						|| jobStatuses.contains("INITIATED")) {
					continue;
				} else {
					batchRepo.updateGstr2bStatus(APIConstants.SUCCESS,
							monitorEntity.getInvocationId(),null,null);
					
					InwardEInvoiceERPRequestEntity erpEntity = new InwardEInvoiceERPRequestEntity();

					erpEntity.setBatchId(monitorEntity.getInvocationId());
					erpEntity.setSupplyType(monitorEntity.getSection());
					erpEntity.setGstin(monitorEntity.getGstin());
					erpEntity.setTaxPeriod(monitorEntity.getTaxPeriod());
					erpEntity.setCreatedOn(LocalDateTime.now());
					erpEntity.setStatus("INITIATED");

					erpRepo.save(erpEntity);
					
						monitorGstnGetIrnStatusRepo.updateStatus(monitorId,
								LocalDateTime.now(), true);

						if (LOGGER.isDebugEnabled())
							LOGGER.debug(
									"GETIRN Success Handler, About to post Report Job - {}",
									groupCode);
					} 				
			} catch (Exception ee) {
				String msg1 = String.format(
						"Exception occured while monitoring control entries. In %s",
						monitorEntity.toString());
				LOGGER.error("Exception while handling success "
						+ "GetIrnMonitorControlServiceImpl ", ee);
				String msg = ee.getMessage().length() > 500
						? ee.getMessage().substring(0, 498) : ee.getMessage();
						batchRepo.updateGstr2bStatus(APIConstants.FAILED,
								monitorEntity.getInvocationId(), msg1,
								"ER-1000");
				monitorGstnGetIrnStatusRepo.updateStatus(monitorId,
						LocalDateTime.now(), true);

				LOGGER.error(msg);
			}
		}
	}



	private Set<String> filterOnlyJobStatus(
			List<ControlGstnGetIrnStatusEntity> controlEntities) {
		return controlEntities.stream().distinct()
				.map(ControlGstnGetIrnStatusEntity::getJobStatus)
				.collect(Collectors.toSet());
	}

}
