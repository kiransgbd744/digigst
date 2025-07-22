/**
 * 
 */
package com.ey.advisory.monitor.processors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.JsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.PollingMessage;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.domain.client.Gstr2bRegenerateSaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr2bRegenerateBatchRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@Slf4j
@Service("MonitorGstr2BPollingReadinessProcessor")
public class MonitorGstr2BPollingReadinessProcessor
		extends DefaultMultiTenantTaskProcessor {
	
	@Autowired
	private Gstr2bRegenerateBatchRepository gstr2bBatchRepository;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		try {
			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Executing Monitoring"
								+ " MonitorGstr2BPollingReadinessProcessor"
								+ ".executeForGroup()  method for group: '%s'",
						group.getGroupCode());
				LOGGER.debug(logMsg);
			}

			createGstr2BPollingTasks(message, group.getGroupCode());

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Completed one cycle of periodic Monitoring"
								+ " job for GstrGstr2BPolling group '%s'",
						group.getGroupCode());
				LOGGER.debug(logMsg);
			}

		} catch (Exception ex) {
			// Throw the App Exception here. If the exception obtained is
			// AppException, then propagate it. Otherwise, create a new
			// app exception. This particular constructor of the AppException
			// will extract the nested exception and attach the message to the
			// specified string. (This way the person who monitors the
			// EY_JOB_DETAILS database will come to know the root cause of the
			// exception).
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);

		}
	}

	@Transactional(value = "clientTransactionManager")
	public void createGstr2BPollingTasks(Message message, String groupCode) {

		List<Gstr2bRegenerateSaveBatchEntity> gstinListEligibleForPolling = null;
		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format("Fetching Gstr2bRegenerateSaveBatchEntity "
					+ "but Polling yet to happen");
			LOGGER.debug(logMsg);
		}
		TenantContext.setTenantId(groupCode);
		gstinListEligibleForPolling = gstr2bBatchRepository
				.findReferenceIdForPooling("GSTR2B");

		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format("Fetched gstr2bBatchRepository list"
					+ " for which Polling job will be created");
			LOGGER.debug(logMsg);
		}
		if (gstinListEligibleForPolling != null
				&& !gstinListEligibleForPolling.isEmpty()) {
			List<Long> saveStatusList = gstinListEligibleForPolling.stream()
					.map(Gstr2bRegenerateSaveBatchEntity::getId)
					.collect(Collectors.toList());
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			gstr2bBatchRepository.updateRefIdStatusForList(saveStatusList,
					APIConstants.POLLING_INPROGRESS, now);
			gstinListEligibleForPolling.forEach(entity -> {
				if (LOGGER.isDebugEnabled()) {
					String logMsg = String.format(
							"Gstr2bPolling Job is created for combination:'%s'",
							entity);
					LOGGER.debug(logMsg);
				}
				postGstr2bPollingJob(message.getId(), entity, groupCode);
			});
		} else {
			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"There are no eligible Gstr2bGenerateSaveBatchEntity for Polling");
				LOGGER.debug(logMsg);
			}
		}
	}

	private void postGstr2bPollingJob(Long curJobId,
			Gstr2bRegenerateSaveBatchEntity saveStatusEntity, String groupCode) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Posting Polling Job job for -> GSTIN: '%s', "
							+ "GroupCode: '%s'", saveStatusEntity, groupCode);
			LOGGER.debug(msg);
		}
		Long saveEntityId = saveStatusEntity.getId();
		String gstin = saveStatusEntity.getSupplierGstin();
		String taxPeriod = saveStatusEntity.getReturnPeriod();
		String refId = saveStatusEntity.getGstnSaveRefId();
		String returnType = saveStatusEntity.getReturnType();
		Long retryCount = saveStatusEntity.getRetryCount();
		
		// Create the Job Params Json for the Calculation job.
		PollingMessage calMessage = new PollingMessage(gstin, taxPeriod, refId,
				saveEntityId, returnType, "SAVE", 0L, retryCount,"POOLING");
		
		String jobParams = JsonUtil.newGsonInstance().toJson(calMessage);
		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Created JobParams JSON for Calculation Job Task"
							+ "for -> GSTIN: '%s'", gstin);
			LOGGER.debug(msg);
		}
		// Post a job to the job table.
			AsyncExecJob execJob = asyncJobsService.createJob(groupCode,
					JobConstants.GSTR2B_RETURNSTATUS, jobParams,
					JobConstants.SYSTEM.toUpperCase(), 5L, curJobId, 0L);
		
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Created new Gstr2bRegenerateSaveBatchEntity Job with Job Id = '%d' "
								+ "for -> jobparams: '%s',",
						execJob.getJobId(), jobParams);
				LOGGER.debug(msg);
			}

	}

}
