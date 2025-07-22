package com.ey.advisory.tasks;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ey.advisory.common.KeyValuePair;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.einv.app.api.APIReqParamConstants;
import com.ey.advisory.einv.app.api.CommonUtil;
import com.ey.advisory.einv.app.api.EINVAPIExecutor;
import com.ey.advisory.ewb.app.api.EWBAPIExecutor;

public class ExecTask implements Runnable {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ExecTask.class);

	private Message message;
	private CountDownLatch latch;
	private String gstin;
	private String groupCode;
	private EWBAPIExecutor ewbExecutor;
	private String apiProvider;
	private EINVAPIExecutor einvExecutor;

	public ExecTask(Message message, String gstin, String groupCode,
			CountDownLatch latch, EWBAPIExecutor ewbExecutor,
			EINVAPIExecutor einvExecutor, String apiProvider) {
		this.message = message;
		this.ewbExecutor = ewbExecutor;
		this.einvExecutor = einvExecutor;
		this.latch = latch;
		this.gstin = gstin;
		this.groupCode = groupCode;
		this.apiProvider = apiProvider;
	}

	public void run() {
		try {
			if (LOGGER.isInfoEnabled()) {
				String msg = String.format(
						"Executing  "
								+ "MultiTenantTaskProcessor for processing "
								+ "message for TaskType: '%s' for group: '%s'",
						message.getMessageType(), groupCode);
				LOGGER.info(msg);
			}
			TenantContext.setTenantId(groupCode);
			if ((APIProviderEnum.EWB.name().equalsIgnoreCase(apiProvider))) {
				KeyValuePair<String, String> keyValuePair = new KeyValuePair<>(
						"gstin", gstin);
				com.ey.advisory.ewb.app.api.APIParams params = new com.ey.advisory.ewb.app.api.APIParams(
						APIProviderEnum.EWB, APIIdentifiers.GET_AUTH_TOKEN,
						keyValuePair);
				com.ey.advisory.ewb.app.api.APIResponse apiResp = ewbExecutor
						.execute(params, "");

				if (apiResp.isSuccess()) {
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Auth Token is Refreshed successfully, Response is '%s' for gstin ",
								apiResp.getResponse());
						LOGGER.debug(msg);
					}
				} else {
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Auth Token is Refresh Failed, Response is '%s'",
								apiResp.getErrors());
						LOGGER.debug(msg);
					}
				}

			} else {
				KeyValuePair<String, String> keyValuePair = new KeyValuePair<>(
						APIReqParamConstants.GSTIN, gstin);
				com.ey.advisory.einv.app.api.APIParams authParams = new com.ey.advisory.einv.app.api.APIParams(
						CommonUtil.getApiProviderEnum(apiProvider),
						com.ey.advisory.einv.app.api.APIIdentifiers.GET_EINV_AUTH_TOKEN,
						keyValuePair);

				com.ey.advisory.einv.app.api.APIResponse apiResp = einvExecutor
						.execute(authParams, "");

				if (apiResp.isSuccess()) {
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Auth Token is Refreshed successfully, Response is '%s'",
								apiResp.getResponse());
						LOGGER.debug(msg);
					}
				} else {
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Auth Token is Refresh Failed, Response is '%s'",
								apiResp.getErrors());
						LOGGER.debug(msg);
					}
				}

			}

			TenantContext.clearTenant();
			if (LOGGER.isInfoEnabled()) {
				String msg = String.format(
						"Executed  "
								+ "MultiTenantTaskProcessor for processing "
								+ "message for TaskType: '%s' for group: '%s'",
						message.getMessageType(), groupCode);
				LOGGER.info(msg);
			}

		} catch (Exception ex) {
			// Set the status of the job as failed in the concurrent hash map.
			// Capture the exception for later logging.
			String msg = String.format("MultiTenantTaskProcessor for "
					+ "processing message for TaskType: '%s' for group: '%s' "
					+ "failed!!", message.getMessageType(), groupCode);
			LOGGER.error(msg, ex);

		} finally {
			latch.countDown();
		}
	}

	public Message getMessage() {
		return message;
	}

}
