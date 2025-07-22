/**
 * 
 */
package com.ey.advisory.monitor.processors;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.Gstr2BMonitorTagging2ARepository;
import com.ey.advisory.app.gstr2b.Gstr2BMonitorTagging2AEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.Group;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */
@Slf4j
@Service("MonitorGstr2BTaggingProcessor")
public class MonitorGstr2BTaggingProcessor
		extends DefaultMultiTenantTaskProcessor {

	private static final List<String> STATUS = ImmutableList
			.of(APIConstants.INPROGRESS, "JOB_POSTED");

	@Autowired
	private Gstr2BMonitorTagging2ARepository monitorTaggingRepo;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		JsonObject jsonParams = new JsonObject();
		try {
			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Executing Monitoring"
								+ " MonitorGstr2BTaggingProcessor"
								+ ".executeForGroup()  method for group: '%s'",
						group.getGroupCode());
				LOGGER.debug(logMsg);
			}

			List<Gstr2BMonitorTagging2AEntity> inProgressEntries = monitorTaggingRepo
					.findByStatusInAndIsActiveTrue(STATUS);

			if (!inProgressEntries.isEmpty()) {
				LOGGER.error(
						"Tagging is in InProgress, Hence skipping the job. Gstin {} TaxPeriod {} ",
						inProgressEntries.get(0).getGstin(),
						inProgressEntries.get(0).getTaxPeriod());
				return;
			}

			// Gstr2BMonitorTagging2AEntity taggedEntry = monitorTaggingRepo
			// .findTop1ByStatusInAndIsActiveTrueOrderByIdAsc(
			// Arrays.asList(APIConstants.INITIATED));

			List<Gstr2BMonitorTagging2AEntity> taggedEntites = monitorTaggingRepo
					.findByStatusInAndIsActiveTrueOrderByIdAsc(
							Arrays.asList(APIConstants.INITIATED));

			Map<String, List<Gstr2BMonitorTagging2AEntity>> groupBySections = taggedEntites
					.stream().collect(Collectors.groupingBy(
							Gstr2BMonitorTagging2AEntity::getSection));

			groupBySections.keySet().forEach(section -> {
				List<Gstr2BMonitorTagging2AEntity> taggedEntries = groupBySections
						.get(section);

				if (taggedEntries != null && taggedEntries.size() > 0) {
					Gstr2BMonitorTagging2AEntity taggedEntry = taggedEntries
							.get(0);// optimize code

					Long id = taggedEntry.getId();
					String gstin = taggedEntry.getGstin();
					String taxPeriod = taggedEntry.getTaxPeriod();
					Long invocationId = taggedEntry.getInvocationId();
					jsonParams.addProperty("id", id);
					jsonParams.addProperty("gstin", gstin);
					jsonParams.addProperty("taxPeriod", taxPeriod);
					jsonParams.addProperty("invocationId", invocationId);
					jsonParams.addProperty("section", section);
					jsonParams.addProperty("source", taggedEntry.getSource());
					monitorTaggingRepo.updateStatus("JOB_POSTED", null,id);
					// change update query based on iD
					asyncJobsService.createJob(TenantContext.getTenantId(),
							JobConstants.GSTR2B_GET_TAGGING,
							jsonParams.toString(), "SYSTEM", 1L, null, null);

					if (LOGGER.isDebugEnabled()) {
						String logMsg = String.format(
								"Completed one cycle of periodic Monitoring"
										+ " job for Gstr1Polling group '%s'",
								group.getGroupCode());
						LOGGER.debug(logMsg);
					}
				} else {
					String logMsg = String
							.format("No Entries available for tagging "
									+ " for group '%s'", group.getGroupCode());
					LOGGER.debug(logMsg);
				}

			});

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
}
