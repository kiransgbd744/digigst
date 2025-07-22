package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.processors.handler.Anx1SaveToGstnJobHandler;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Service("Anx1SaveToGstnProcessor")
@Slf4j
public class Anx1SaveToGstnProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Anx1SaveToGstnJobHandler")
	private Anx1SaveToGstnJobHandler anx1JobHandler;
	
	@Autowired
	private AsyncJobsService asyncJobsService;

	@Override
	public void execute(Message message, AppExecContext context) {
		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Anx1 SaveToGstn Data Execute method is ON with "
							+ "groupcode {} and params {}",
					groupCode, jsonString);
		}
		if (jsonString != null && groupCode != null) {
			try {
				anx1JobHandler.saveCancelledInvoices(jsonString, groupCode);
				anx1JobHandler.saveActiveInvoices(jsonString, groupCode);
				LOGGER.info("Anx1 SaveToGstn Processed with args {} ",
						jsonString);

				AsyncExecJob job = asyncJobsService.createJob(groupCode,
						JobConstants.ANX1_RETURNSTATUS, jsonString,
						JobConstants.SYSTEM, JobConstants.PRIORITY,
						JobConstants.PARENT_JOB_ID,
						JobConstants.SCHEDULE_AFTER_IN_MINS);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Async job is created with job {}", job);
				}

			} catch (Exception ex) {
				String msg = "Error while executing Anx1 SaveToGstn";
				LOGGER.error(msg, ex);
				throw new APIException(msg, ex);
			}
		}

	}

}
