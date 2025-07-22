package com.ey.advisory.monitor.processors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GstnSubmitEntity;
import com.ey.advisory.app.data.repositories.client.GstnSubmitRepository;
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

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author SriBhavya
 *
 */
@Slf4j
@Service("MonitorGstr6SubmitPollingReadinessProcessor")
public class MonitorGstr6SubmitPollingReadinessProcessor extends DefaultMultiTenantTaskProcessor {

	@Autowired
	private GstnSubmitRepository gstr6SubmitRepo;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Override
	public void executeForGroup(Group group, Message message, AppExecContext ctx) {
		try {
			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format("Executing Monitoring" 
						+ " MonitorGstr6SubmitPollingReadinessProcessor"
						+ ".executeForGroup()  method for group: '%s'", group.getGroupCode());
				LOGGER.debug(logMsg);
			}

			createGstr6PollingTasks(message, group.getGroupCode());

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Completed one cycle of periodic Monitoring" 	
								+ " job for Submit Gstr6Polling group '%s'",
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
			throw new AppException(ex, ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);

		}

	}

	@Transactional(value = "clientTransactionManager")
	public void createGstr6PollingTasks(Message message, String groupCode) {
		List<GstnSubmitEntity> gstinListEligibleForPolling = null;
		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format("Fetching GstnSubmitEntity for GSTR6 " 
					+ "but Polling yet to happen");
			LOGGER.debug(logMsg);
		}
		TenantContext.setTenantId(groupCode);
		gstinListEligibleForPolling = gstr6SubmitRepo.findReferenceIdForPooling(APIConstants.GSTR6.toUpperCase());

		if (LOGGER.isDebugEnabled()) {
			String logMsg = String
					.format("Fetched GstnSubmitEntity list for GSTR6 " 
							+ " for which Polling job will be created");
			LOGGER.debug(logMsg);
		}
		if (gstinListEligibleForPolling != null && !gstinListEligibleForPolling.isEmpty()) {
			List<Long> saveStatusList = gstinListEligibleForPolling.stream().map(GstnSubmitEntity::getId)
					.collect(Collectors.toList());
			LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
			gstr6SubmitRepo.updateRefIdStatusForList(saveStatusList, APIConstants.POLLING_INPROGRESS, now);
			gstinListEligibleForPolling.forEach(entity -> {
				if (LOGGER.isDebugEnabled()) {
					String logMsg = String.format("GSTR6_SUBMIT Gstr6Polling Job is created for combination:'%s'",
							entity);
					LOGGER.debug(logMsg);
				}
				postGstr6PollingJob(message.getId(), entity, groupCode);
			});
		} else {
			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format("There are no eligible GstnSubmitEntity for GSTR6_SUBMIT Polling");
				LOGGER.debug(logMsg);
			}
		}
	}

	private void postGstr6PollingJob(Long curJobId, GstnSubmitEntity saveStatusEntity, String groupCode) {
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("GSTR6_SUBMIT Posting Polling Job job for -> GSTIN: '%s', " + "GroupCode: '%s'",
					saveStatusEntity, groupCode);
			LOGGER.debug(msg);
		}
		Long batchId = saveStatusEntity.getId();
		String gstin = saveStatusEntity.getGstin();
		String taxPeriod = saveStatusEntity.getRetPeriod();
		String refId = saveStatusEntity.getRefId();
		String returnType = saveStatusEntity.getReturnType();

		if (APIConstants.ALREADY_SUBMITTED.equals(refId)) {
			LOGGER.error("already submitted is a dummy ref_id inserted.");
			return;
		}

		// Create the Job Params Json for the Calculation job.
		PollingMessage calMessage = new PollingMessage(gstin, taxPeriod, refId, batchId, returnType);
		String jobParams = JsonUtil.newGsonInstance().toJson(calMessage);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"GSTR6_SUBMIT Created JobParams JSON for Calculation Job Task" 
							+ "for -> GSTIN: '%s'", gstin);
			LOGGER.debug(msg);
		}
		// Post a job to the job table.
		AsyncExecJob execJob = asyncJobsService.createJob(groupCode, JobConstants.GSTR6_SUBMIT_RETURNSTATUS, jobParams,
				JobConstants.SYSTEM.toUpperCase(), 5L, curJobId, 0L);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Created new Gstr6SubmitReturnStatus Job with Job Id = '%d' " 
							+ "for -> jobparams: '%s',",
					execJob.getJobId(), jobParams);
			LOGGER.debug(msg);
		}

	}

}
