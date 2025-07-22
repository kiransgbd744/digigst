/**
 * 
 */
package com.ey.advisory.monitor.processors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("MonitorGstr1PollingReadinessProcessor")
public class MonitorGstr1PollingReadinessProcessor
		extends DefaultMultiTenantTaskProcessor {

	private static final List<String> returnTypes = ImmutableList.of(
			APIConstants.GSTR1.toUpperCase(),
			APIConstants.GSTR1A.toUpperCase());

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository gstr1BatchRepository;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		try {
			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Executing Monitoring"
								+ " MonitorGstr1PollingReadinessProcessor"
								+ ".executeForGroup()  method for group: '%s'",
						group.getGroupCode());
				LOGGER.debug(logMsg);
			}

			createGstr1PollingTasks(message, group.getGroupCode());

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Completed one cycle of periodic Monitoring"
								+ " job for Gstr1Polling group '%s'",
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
	public void createGstr1PollingTasks(Message message, String groupCode) {

		List<Gstr1SaveBatchEntity> gstinListEligibleForPolling = null;
		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format("Fetching Gstr1SaveBatchEntity "
					+ "but Polling yet to happen");
			LOGGER.debug(logMsg);
		}
		TenantContext.setTenantId(groupCode);
		gstinListEligibleForPolling = gstr1BatchRepository
				.findReferenceIdForPooling(returnTypes);

		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format("Fetched Gstr1SaveBatchEntity list"
					+ " for which Polling job will be created");
			LOGGER.debug(logMsg);
		}
		if (gstinListEligibleForPolling != null
				&& !gstinListEligibleForPolling.isEmpty()) {
			List<Long> saveStatusList = gstinListEligibleForPolling.stream()
					.map(Gstr1SaveBatchEntity::getId)
					.collect(Collectors.toList());
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			gstr1BatchRepository.updateRefIdStatusForList(saveStatusList,
					APIConstants.POLLING_INPROGRESS, now);
			gstinListEligibleForPolling.forEach(entity -> {
				if (LOGGER.isDebugEnabled()) {
					String logMsg = String.format(
							"Gstr1Polling Job is created for combination:'%s'",
							entity);
					LOGGER.debug(logMsg);
				}
				postGstr1PollingJob(message.getId(), entity, groupCode);
			});
		} else {
			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"There are no eligible Gstr1SaveBatchEntity for Polling");
				LOGGER.debug(logMsg);
			}
		}
	}

	private void postGstr1PollingJob(Long curJobId,
			Gstr1SaveBatchEntity saveStatusEntity, String groupCode) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Posting Polling Job job for -> GSTIN: '%s', "
							+ "GroupCode: '%s'", saveStatusEntity, groupCode);
			LOGGER.debug(msg);
		}
		Long batchId = saveStatusEntity.getId();
		String gstin = saveStatusEntity.getSgstin();
		String taxPeriod = saveStatusEntity.getReturnPeriod();
		String refId = saveStatusEntity.getRefId();
		String section = saveStatusEntity.getSection();
		String returnType = saveStatusEntity.getReturnType();
		Long userRequestId = saveStatusEntity.getUserRequestId();
		Long retryCount = saveStatusEntity.getRetryCount();
		String operationType = saveStatusEntity.getOperationType();
		if (APIConstants.DELETE_FULL_DATA.equalsIgnoreCase(operationType)) {
			section = "DELETE";
		}
		// Create the Job Params Json for the Calculation job.
		PollingMessage calMessage = new PollingMessage(gstin, taxPeriod, refId,
				batchId, returnType, section, userRequestId, retryCount,
				operationType);
		String jobParams = JsonUtil.newGsonInstance().toJson(calMessage);
		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Created JobParams JSON for Calculation Job Task"
							+ "for -> GSTIN: '%s'", gstin);
			LOGGER.debug(msg);
		}
		// Post a job to the job table.
		if (APIConstants.GSTR1A.equalsIgnoreCase(returnType)) {
			AsyncExecJob execJob = asyncJobsService.createJob(groupCode,
					JobConstants.GSTR1A_RETURNSTATUS, jobParams,
					JobConstants.SYSTEM.toUpperCase(), 5L, curJobId, 0L);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Created new Gstr1ReturnStatus Job with Job Id = '%d' "
								+ "for -> jobparams: '%s',",
						execJob.getJobId(), jobParams);
				LOGGER.debug(msg);
			}

		} else {
			AsyncExecJob execJob = asyncJobsService.createJob(groupCode,
					JobConstants.GSTR1_RETURNSTATUS, jobParams,
					JobConstants.SYSTEM.toUpperCase(), 5L, curJobId, 0L);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Created new Gstr1ReturnStatus Job with Job Id = '%d' "
								+ "for -> jobparams: '%s',",
						execJob.getJobId(), jobParams);
				LOGGER.debug(msg);
			}
		}

	}

}
