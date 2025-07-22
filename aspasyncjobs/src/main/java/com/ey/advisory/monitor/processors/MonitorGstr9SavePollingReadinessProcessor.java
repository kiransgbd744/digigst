package com.ey.advisory.monitor.processors;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.gstr9.Gstr9SaveStatusEntity;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9SaveStatusRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.JsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.PollingMessage;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.async.domain.master.Group;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("MonitorGstr9SavePollingReadinessProcessor")
public class MonitorGstr9SavePollingReadinessProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	private Gstr9SaveStatusRepository gstr9SaveStatusRepository;

	@Autowired
	private AsyncJobsService asyncJobsService;

	final static List<String> STATUS_LIST = ImmutableList.of(
			APIConstants.SAVE_INITIATED, APIConstants.POLLING_FAILED,
			APIConstants.POLLING_INPROGRESS);

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		try {
			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Executing Monitoring"
								+ " MonitorGSTR9PollingReadinessProcessor"
								+ ".executeForGroup()  method for group: '%s'",
						group.getGroupCode());
				LOGGER.debug(logMsg);
			}

			createGSTR9PollingTasks(message, group.getGroupCode());

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Completed one cycle of periodic Monitoring"
								+ " job for GSTR9Polling group '%s'",
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

	private void createGSTR9PollingTasks(Message message, String groupCode) {

		List<Gstr9SaveStatusEntity> gstinListEligibleForPolling = null;
		if (LOGGER.isDebugEnabled()) {
			String logMsg = String
					.format("Fetching Gstr9 saveStatus entities with status %s "
							+ "but Polling yet to happen", STATUS_LIST);
			LOGGER.debug(logMsg);
		}
		gstinListEligibleForPolling = gstr9SaveStatusRepository
				.findByStatusInAndRefIdIsNotNull(STATUS_LIST);

		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format(
					"Fetched Gstr9 SaveStatus entity list"
							+ " for status %s which Polling job will be created",
					STATUS_LIST);
			LOGGER.debug(logMsg);
		}
		if (gstinListEligibleForPolling != null
				&& !gstinListEligibleForPolling.isEmpty()) {

			List<Long> saveStatusList = gstinListEligibleForPolling.stream()
					.map(Gstr9SaveStatusEntity::getId)
					.collect(Collectors.toList());

			gstr9SaveStatusRepository.updateRefIdStatusForList(saveStatusList,
					APIConstants.POLLING_INITIATED);

			gstinListEligibleForPolling.forEach(entity -> {
				if (LOGGER.isDebugEnabled()) {
					String logMsg = String.format(
							"GSTR9Polling Job is created for combination:'%s'",
							entity);
					LOGGER.debug(logMsg);
				}
				postGSTR9PollingJob(message.getId(), entity, groupCode);
			});
		} else {
			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"There are no eligible Save Status entities for Polling");
				LOGGER.debug(logMsg);
			}
		}
	}

	private void postGSTR9PollingJob(Long curJobId,
			Gstr9SaveStatusEntity saveStatusEntity, String groupCode) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Posting Polling Job job for -> GSTIN: '%s', "
							+ "GroupCode: '%s'", saveStatusEntity, groupCode);
			LOGGER.debug(msg);
		}
		String gstin = saveStatusEntity.getGstin();
		String taxPeriod = saveStatusEntity.getTaxPeriod();
		String refId = saveStatusEntity.getRefId();
		// Create the Job Params Json for the Calculation job.
		PollingMessage calMessage = new PollingMessage(gstin, taxPeriod, refId);
		String jobParams = JsonUtil.newGsonInstance().toJson(calMessage);
		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Created JobParams JSON for Calculation Job Task"
							+ "for -> GSTIN: '%s'", gstin);
			LOGGER.debug(msg);
		}
		// Post a job to the job table.
		AsyncExecJob execJob = asyncJobsService.createJob(groupCode,
				"PollGstr9", jobParams, "SYSTEM", 5L, curJobId, 0L);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Created new Calculation Job with Job Id = '%d' "
							+ "for -> jobparams: '%s',",
					execJob.getJobId(), jobParams);
			LOGGER.debug(msg);
		}
	}
}
