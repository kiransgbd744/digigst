package com.ey.advisory.monitor.processors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.ZipGenStatusRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.JsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.processing.messages.MergeCsvFilesMessageMonthwise;

import lombok.extern.slf4j.Slf4j;

@Component("MonitorCombineAndZipMonthwiseProcessor")
@Slf4j
public class MonitorCombineAndZipMonthwiseProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	private AsyncJobsService persistenceMngr;

	@Autowired
	ZipGenStatusRepository zipGenStatusRepo;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		try {
			String groupCode = group.getGroupCode();

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format("Executing Monitoring"
						+ " monthly combine zip job" + ".executeForGroup()"
						+ " method for group: '%s'", groupCode);
				LOGGER.debug(logMsg);
			}

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"About to monitor combineAndZipMonthWise for group '%s' ",
						groupCode);
				LOGGER.debug(logMsg);
			}

			// Update the Gstr1 status.
			List<Object[]> gstinInvoiceTypeCombinations = zipGenStatusRepo
					.fetchGstinAndInvoiceTypeEligibleForZipMonthwise();

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Fetched eligible gstinInvoiceCombinations"
								+ " which are ready for combine and zip,"
								+ " Total No of combinations are %d",
						gstinInvoiceTypeCombinations.size());
				LOGGER.debug(logMsg);
			}
			if (gstinInvoiceTypeCombinations != null
					&& !gstinInvoiceTypeCombinations.isEmpty()) {

				List<Triplet<String, String, String>> zipEligibleRecords = new ArrayList<>();

				List<AsyncExecJob> jobList = new ArrayList<>();
				gstinInvoiceTypeCombinations.forEach(combination -> {
					String gstin = String.valueOf(combination[0]);
					String taxPeriod = String.valueOf(combination[1]);
					String returnType = String.valueOf(combination[2]);

					Triplet<String, String, String> obj = new Triplet<>(gstin,
							taxPeriod, returnType);

					zipEligibleRecords.add(obj);

					if (LOGGER.isDebugEnabled()) {
						String logMsg = String.format(
								"Before creating CombineAndZipMonthwise Job for "
										+ "Gstin: '%s'," + "taxPeriod: '%s'"
										+ " returnType: '%s'",
								gstin, taxPeriod, returnType);
						LOGGER.debug(logMsg);
					}
					AsyncExecJob job = createCombinedAndZipMonthwise(
							message.getId(), gstin, taxPeriod, returnType,
							groupCode);
					if (LOGGER.isDebugEnabled()) {
						String logMsg = String.format(
								"Posted CombineAndZipMonthwise Job for "
										+ "Gstin: '%s'" + " returnType: '%s'",
								gstin, returnType);
						LOGGER.debug(logMsg);
					}
					jobList.add(job);
				});

				persistenceMngr.createJobs(jobList);

				zipEligibleRecords
						.forEach(o -> zipGenStatusRepo.updateJobstatusForList(
								"ZIP_POSTED", o.getValue0(), o.getValue1(),
								o.getValue2(), LocalDateTime.now()));

				if (LOGGER.isDebugEnabled()) {
					String logMsg = String.format("Posted eligible"
							+ " gstinInvoiceCombinations which are ready for"
							+ " combine and zip, Total count posted is '%d'",
							jobList.size());
					LOGGER.debug(logMsg);
				}
			} else {
				if (LOGGER.isDebugEnabled()) {
					String logMsg = String.format("There are No eligible"
							+ " gstinInvoiceCombinations which are ready for"
							+ " combine and zip, Hence no "
							+ "combineZip jobs are posted");
					LOGGER.debug(logMsg);
				}
			}

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Completed one cycle of periodic Computation job"
								+ " for group '%s'",
						group.getGroupCode());
				LOGGER.debug(logMsg);
			}

			// }
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

	private AsyncExecJob createCombinedAndZipMonthwise(Long curJobId,
			String gstin, String taxPeriod, String returnType,
			String groupCode) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Posting CombineAndZipMonthwise Job job for -> GSTIN: '%s', "
							+ "GroupCode: '%s'",
					gstin, groupCode);
			LOGGER.debug(msg);
		}
		// Create the Job Params Json for the Calculation job.
		MergeCsvFilesMessageMonthwise zipMessage = new MergeCsvFilesMessageMonthwise(
				taxPeriod, returnType, gstin);
		String jobParams = JsonUtil.newGsonInstance().toJson(zipMessage);
		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Created JobParams JSON for Calculation Job Task"
							+ "for -> GSTIN: '%s'", gstin);
			LOGGER.debug(msg);
		}

		AsyncExecJob job = new AsyncExecJob();
		job.setGroupCode(groupCode);
		job.setJobCategory("CombineAndZipMonthwise");
		job.setStatus(JobStatusConstants.SUBMITTED);
		job.setMessage(jobParams);
		job.setJobPriority(1L);
		job.setUserName("SYSTEM");
		job.setParentId(curJobId);
		job.setCreatedDate(new Date());
		return job;
	}

}
