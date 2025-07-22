package com.ey.advisory.tasks;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;

public class ExecTask implements Runnable {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ExecTask.class);

	private Message message;
	private CountDownLatch latch;
	private String gstin;
	private String groupCode;
	private APIExecutor apiExecutor;

	public ExecTask(Message message, String gstin, String groupCode,
			CountDownLatch latch, APIExecutor apiExecutor) {
		this.message = message;
		this.apiExecutor = apiExecutor;
		this.latch = latch;
		this.gstin = gstin;
		this.groupCode = groupCode;
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
			APIParam param = new APIParam("gstin", gstin);
			APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.REFRESH_AUTH_TOKEN, param);
			TenantContext.setTenantId(groupCode);
			APIResponse apiResp = apiExecutor.execute(params, "");
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
