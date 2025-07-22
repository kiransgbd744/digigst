package com.ey.advisory.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("Gstr2FileUploadJobInsertion")
public class Gstr2FileUploadJobInsertion {

	@Autowired
	private AsyncJobsService asyncJobsService;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultGstr2FileUploadService.class);

	public void fileUploadJob(String jsonParam, String jobCategory,
			String userName) {
		try {
			String groupCode = TenantContext.getTenantId();
			AsyncExecJob job = asyncJobsService.createJob(groupCode,
					jobCategory, jsonParam, userName, JobConstants.PRIORITY,
					JobConstants.PARENT_JOB_ID,
					JobConstants.SCHEDULE_AFTER_IN_MINS);

		} catch (Exception ex) {
			LOGGER.error("Unexpected error while saving documents");

		}
	}
}
