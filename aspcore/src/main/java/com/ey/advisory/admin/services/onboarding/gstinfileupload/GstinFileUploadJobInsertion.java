package com.ey.advisory.admin.services.onboarding.gstinfileupload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.services.gstin.jobs.GstinJobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;

@Component("GstinFileUploadJobInsertion")
public class GstinFileUploadJobInsertion {

	@Autowired
	private AsyncJobsService asyncJobsService;

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultGstinFileUploadService.class);

	public void elRegitrationFileUpload(String jsonParam, String jobCategory) {
		try {
			String groupCode = TenantContext.getTenantId();
			AsyncExecJob job = asyncJobsService.createJob(groupCode, jobCategory, jsonParam, "system",
					GstinJobConstants.PRIORITY, GstinJobConstants.PARANT_JOB_ID, GstinJobConstants.SCHEDULE_AFTER_IN_MINS);

		} catch (Exception ex) {
			LOGGER.error("Unexpected error while saving documents");

		}
	}
	
	public void elEntitlement1FileUpload(String jsonParam, String jobCategory) {
		try {
			String groupCode = TenantContext.getTenantId();
			AsyncExecJob job = asyncJobsService.createJob(groupCode, jobCategory, jsonParam, "system",
					GstinJobConstants.PRIORITY, GstinJobConstants.PARANT_JOB_ID, GstinJobConstants.SCHEDULE_AFTER_IN_MINS);

		} catch (Exception ex) {
			LOGGER.error("Unexpected error while saving documents");

		}
	}
	
	
}
