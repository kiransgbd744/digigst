package com.ey.advisory.app.gstr2jsonupload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Component("Gstr2JsonUploadJobInsertion")
public class Gstr2JsonUploadJobInsertion {
	
		@Autowired
		private AsyncJobsService asyncJobsService;

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
