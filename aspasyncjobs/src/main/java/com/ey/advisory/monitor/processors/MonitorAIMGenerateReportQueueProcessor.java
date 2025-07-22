/**
 * 
 */
package com.ey.advisory.monitor.processors;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.asprecon.AutoReconRequestEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.AutoReconRequestRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.Group;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Service("MonitorAIMGenerateReportQueueProcessor")
public class MonitorAIMGenerateReportQueueProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("AutoReconRequestRepository")
	private AutoReconRequestRepository autoReconRequestRepo;

	private static List<String> statusList = ImmutableList.of("IN_QUEUE");

	private static List<String> ineligibleStatus = ImmutableList.of("SUBMITTED",
			"INITIATED", "REPORT_GENERATION_INPROGRESS");

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		AutoReconRequestEntity entity = null;
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Inside MonitorAIMGenerateReportQueueProcessor "
								+ "group code {} : ",
						TenantContext.getTenantId());
			}

			int count = autoReconRequestRepo.getCountOfStatus(ineligibleStatus);
			if (count > 0) {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Inside MonitorAIMGenerateReportQueueProcessor "
									+ "group code {} previous "
									+ "report generation is still in progress : ",
							TenantContext.getTenantId());
				}

				return;
			}

			List<AutoReconRequestEntity> entityList = autoReconRequestRepo
					.findByStatusIn(statusList);

			if (entityList.isEmpty()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Inside MonitorAIMGenerateReportQueueProcessor "
									+ "group code {} entityList list is empty "
									+ "no pending recon available to "
									+ "process : ",
							TenantContext.getTenantId());
				}

				return;
			}

			entityList.sort(
					Comparator.comparing(AutoReconRequestEntity::getRequestId));

			entity = entityList.get(0);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Inside MonitorAIMGenerateReportQueueProcessor "
						+ "group code {}, entity for submitting job {}: ",
						TenantContext.getTenantId(), entity);
			}

			// submit job for report generation
			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("id", entity.getRequestId());
			asyncJobsService.createJob(TenantContext.getTenantId(),
					JobConstants.GSTR2A_AUTO_RECON_CLOUD_REPORT,
					jsonParams.toString(), entity.getCreatedBy(), 5L, 0L, 0L);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Inside MonitorAIMGenerateReportQueueProcessor "
						+ "group code {}, job submitted : configId {} : ",
						TenantContext.getTenantId(), entity.getRequestId());
			}
			
			autoReconRequestRepo.updateStatus(
					ReportStatusConstants.REPORT_GENERATION_INPROGRESS,
					LocalDateTime.now(), entity.getRequestId(), null, null);

		} catch (Exception e) {
			Long configId = (entity != null) ? entity.getRequestId() : 0L;
			LOGGER.error(
					"Error occured Inside MonitorAIMGenerateReportQueueProcessor "
							+ "group code {}, while recon job submitting : "
							+ "configId {} : ",
					TenantContext.getTenantId(), configId);
			throw new AppException(e);

		}

	}

}
