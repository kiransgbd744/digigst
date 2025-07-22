package com.ey.advisory.monitor.processors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.JsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.PollingMessage;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */
@Slf4j
@Component("MonitorGSTR1GetSummaryPollingProcessor")
public class MonitorGSTR1GetSummaryPollingProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository saveStatusRepo;

	@Autowired
	private AsyncJobsService asyncJobsService;

	final static List<String> STATUS_LIST = ImmutableList.of(
			APIConstants.POLLING_FAILED, APIConstants.POLLING_INPROGRESS,
			APIConstants.GET_INITIATED);

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		try {
			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Executing Monitoring"
								+ " MonitorGSTR1GetSummaryPollingProcessor"
								+ ".executeForGroup()  method for group: '%s'",
						group.getGroupCode());
				LOGGER.debug(logMsg);
			}

			createGSTR1PollingTasks(message, group.getGroupCode());

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Completed one cycle of periodic Monitoring"
								+ " job for GSTR1Polling group '%s'",
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

	private void createGSTR1PollingTasks(Message message, String groupCode) {
		List<Gstr1SaveBatchEntity> gstinListEligibleForPolling = null;
		if (LOGGER.isDebugEnabled()) {
			String logMsg = String
					.format("Fetching saveStatus entities with status %s "
							+ "but Polling yet to happen", STATUS_LIST);
			LOGGER.debug(logMsg);
		}
		gstinListEligibleForPolling = saveStatusRepo
				.findGstr1PollingRefIds(STATUS_LIST);

		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format(
					"Fetched SaveStatus entity list"
							+ " for status %s which Polling job will be created",
					STATUS_LIST);
			LOGGER.debug(logMsg);
		}
		if (!gstinListEligibleForPolling.isEmpty()) {
			List<Long> saveStatusList = gstinListEligibleForPolling.stream()
					.map(Gstr1SaveBatchEntity::getId)
					.collect(Collectors.toList());

			saveStatusRepo.updateRefIdStatusForList(saveStatusList,
					APIConstants.POLLING_INITIATED, LocalDateTime.now());

			gstinListEligibleForPolling.forEach(entity -> {
				if (LOGGER.isDebugEnabled()) {
					String logMsg = String
							.format("GSTR1 GET Summary Polling Job is created for"
									+ " combination:'%s'", entity);
					LOGGER.debug(logMsg);
				}
				postGSTR1PollingJob(message.getId(), entity, groupCode);
			});
		} else {
			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"There are no eligible Save Status entities for Polling");
				LOGGER.debug(logMsg);
			}
		}
	}

	private void postGSTR1PollingJob(Long curJobId,
			Gstr1SaveBatchEntity saveStatusEntity, String groupCode) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Posting Polling Job job for -> GSTIN: '%s', "
							+ "GroupCode: '%s'", saveStatusEntity, groupCode);
			LOGGER.debug(msg);
		}
		String gstin = saveStatusEntity.getSgstin();
		String taxPeriod = saveStatusEntity.getReturnPeriod();
		String refId = saveStatusEntity.getRefId();
		// Create the Job Params Json for the Calculation job.
		PollingMessage calMessage = new PollingMessage(gstin, taxPeriod, refId);
		calMessage.setReturnType(
				Strings.isNullOrEmpty(saveStatusEntity.getReturnType())
						? APIConstants.GSTR1.toUpperCase()
						: saveStatusEntity.getReturnType().toUpperCase());
		String jobParams = JsonUtil.newGsonInstance().toJson(calMessage);
		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Created JobParams JSON for Calculation Job Task"
							+ "for -> GSTIN: '%s'", gstin);
			LOGGER.debug(msg);
		}
		// Post a job to the job table.
		if (saveStatusEntity.getReturnType()
				.equalsIgnoreCase(APIConstants.GSTR1A)) {

			AsyncExecJob execJob = asyncJobsService.createJob(groupCode,
					JobConstants.GSTR1A_GET_SUMMARY_POLLING, jobParams, "SYSTEM",
					5L, curJobId, 0L);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Created new Calculation Job with Job Id = '%d' "
								+ "for -> jobparams: '%s',",
						execJob.getJobId(), jobParams);
				LOGGER.debug(msg);
			}
		} else {
			AsyncExecJob execJob = asyncJobsService.createJob(groupCode,
					JobConstants.GSTR1_GET_SUMMARY_POLLING, jobParams, "SYSTEM",
					5L, curJobId, 0L);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Created new Calculation Job with Job Id = '%d' "
								+ "for -> jobparams: '%s',",
						execJob.getJobId(), jobParams);
				LOGGER.debug(msg);
			}
		}

	}
}
