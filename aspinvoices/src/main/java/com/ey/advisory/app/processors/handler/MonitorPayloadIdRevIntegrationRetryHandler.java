/**
 * 
 */
package com.ey.advisory.app.processors.handler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.InwardPayloadRepository;
import com.ey.advisory.app.data.repositories.client.OutwardPayloadRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("MonitorPayloadIdRevIntegrationRetryHandler")
public class MonitorPayloadIdRevIntegrationRetryHandler {

	@Autowired
	private OutwardPayloadRepository outwardpayload;

	@Autowired
	private InwardPayloadRepository inwardpayload;

	@Autowired
	private AsyncJobsService asyncJobsService;

	// @Transactional(value = "clientTransactionManager")
	public void execute(String groupCode) {

		try {
			TenantContext.setTenantId(groupCode);

			List<String[]> outwardFailedPayloadIds = outwardpayload
					.findPayloadIdForFailedErpPush();

			if (outwardFailedPayloadIds != null) {
				outwardFailedPayloadIds.forEach(payloadIdUserName -> {
					String payloadId = payloadIdUserName[0];
					String userName = payloadIdUserName[1];
					if (payloadId != null) {
						createOutwardJob(payloadId, groupCode, userName);
					}

				});
			}
			List<String[]> inwardFailedPayloadIds = inwardpayload
					.findPayloadIdForFailedErpPush();

			if (inwardFailedPayloadIds != null) {
				inwardFailedPayloadIds.forEach(payloadIdUserName -> {
					String payloadId = payloadIdUserName[0];
					String userName = payloadIdUserName[1];
					if (payloadId != null) {
						createInwardJob(payloadId, groupCode, userName);
					}

				});
			}

		} catch (Exception e) {
			LOGGER.error("Unexpected Error", e);
			throw new AppException(e.getMessage(), e);
		}
	}

	private void createOutwardJob(String payloadId, String groupCode,
			String userName) {

		JsonObject metaDataRevIntjobjsonParams = new JsonObject();

		metaDataRevIntjobjsonParams.addProperty("payloadId", payloadId);
		metaDataRevIntjobjsonParams.addProperty("scenarioName",
				APIConstants.OUTWARD_PAYLOAD_METADATA_REV_INTG);

		asyncJobsService.createJob(groupCode,
				APIConstants.OUTWARD_PAYLOAD_METADATA_REV_INTG,
				metaDataRevIntjobjsonParams.toString(), userName,
				JobConstants.PRIORITY, JobConstants.PARENT_JOB_ID,
				JobConstants.SCHEDULE_AFTER_IN_MINS);

	}

	private void createInwardJob(String payloadId, String groupCode,
			String userName) {

		JsonObject metaDataRevIntjobjsonParams = new JsonObject();

		metaDataRevIntjobjsonParams.addProperty("payloadId", payloadId);
		metaDataRevIntjobjsonParams.addProperty("scenarioName",
				APIConstants.INWARD_PAYLOAD_METADATA_REV_INTG);

		asyncJobsService.createJob(groupCode,
				APIConstants.INWARD_PAYLOAD_METADATA_REV_INTG,
				metaDataRevIntjobjsonParams.toString(), userName,
				JobConstants.PRIORITY, JobConstants.PARENT_JOB_ID,
				JobConstants.SCHEDULE_AFTER_IN_MINS);

	}
}
