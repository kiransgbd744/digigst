package com.ey.advisory.common.async;

import java.net.SocketTimeoutException;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import com.ey.advisory.common.JsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.repositories.master.AsyncExecJobRepository;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;

import lombok.extern.slf4j.Slf4j;

/**
 * This class is the actual 'Runnable' task that's submitted to the thread from
 * the 'Executor Thread Pool'. This class takes the 'TaskProcessor' instance
 * where the actual job is implemented, as a parameter. The run method of this
 * class wraps the common activities that needs to be done prior to and after
 * the Task execution. The job Id is set to Log4J MDC context, so that within
 * the custom TaskProcessor, every logger message will be tagged with the
 * respective Job Id, for easier tracking of errors in the production
 * environment. Also, after the execution of a Job, if the status is not already
 * set by some other component on the way, then this class takes care of marking
 * the job as 'Completed' or 'Failed' so that the Jobs won't remain in an
 * 'InProgress' status, forever.
 * 
 * @author Sai.Pakanati
 *
 */
@Slf4j
public class RunnableTaskProcessor implements Runnable {

	private HttpClient httpClient;

	protected Message message;

	private JobUrlResolver jobUrlResolver;

	private ConfigManager configManager;

	public RunnableTaskProcessor(Message message, HttpClient httpClient,
			JobUrlResolver jobUrlResolver, ConfigManager configManager) {
		this.httpClient = httpClient;
		this.message = message;
		this.jobUrlResolver = jobUrlResolver;
		this.configManager = configManager;
	}

	@Override
	public void run() {
		try {
			String jsonMsg = JsonUtil.newGsonInstance(false).toJson(message);
			String resource = jobUrlResolver.resolveJobUrl(message);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"About to post the Job %s to asyncjobs url %s",
						message.getMessageType(), resource);
				LOGGER.debug(msg);
			}
			HttpPost httpPost = new HttpPost(resource);
			httpPost.setHeader("Content-Type", "application/json");
			RequestConfig config = getRequestCOnfig();
			httpPost.setConfig(config);
			StringEntity entity = new StringEntity(jsonMsg);
			httpPost.setEntity(entity);
			HttpResponse response = httpClient.execute(httpPost);
			if (response == null)
				return;
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode > 400 && statusCode < 600) {
				String msg = String.format(
						"Something Failed, AsyncJob Server responded with %d",
						statusCode);
				LOGGER.error(msg);
			}
			String respString = EntityUtils.toString(response.getEntity());
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Job has returned %s with status code %d", respString,
						statusCode);
				LOGGER.debug(msg);
			}

		} catch (SocketTimeoutException ex) {
			LOGGER.error("SocketException occured while invoking asyncjobs",
					ex);
		} catch (Exception ex) {
			LOGGER.error("Exception occured while invoking asyncjobs", ex);
			updateStatusAndErrorMsg(message, ex);
		} finally {
			TenantContext.clearTenant();
		}
	}

	private RequestConfig getRequestCOnfig() {

		Map<String, Config> configMap = configManager.getConfigs("EYInternal",
				"timeout");
		int connectionTimeout = configMap.containsKey(
				"timeout.internal.conn_timeout")
						? Integer.valueOf(configMap
								.get("timeout.internal.conn_timeout")
								.getValue())
						: 5000;
		int readTimeout = configMap.containsKey("timeout.internal.read_timeout")
				? Integer.valueOf(configMap.get("timeout.internal.read_timeout")
						.getValue())
				: 5000;
		int connectionManagerTimeout = configMap.containsKey(
				"timeout.internal.conn_mngr_timeout")
						? Integer.valueOf(
								configMap.get(
										"timeout.internal.conn_mngr_timeout")
										.getValue())
						: 5000;
		return RequestConfig.custom().setConnectTimeout(connectionTimeout)
				.setConnectionRequestTimeout(connectionManagerTimeout)
				.setSocketTimeout(readTimeout).build();
	}

	private void updateStatusAndErrorMsg(Message msg, Exception ex) {
		AsyncExecJobRepository repo = StaticContextHolder.getBean(
				"asyncExecJobRepository", AsyncExecJobRepository.class);
		Long jobId = msg.getId();
		String errReason = String.format("Failed to dispatch the Job, Id %d",
				msg.getId());
		LOGGER.error(errReason, ex);
		int cnt = repo.updateErrorMsgForPickedJobs(jobId, errReason);
		if (cnt == 0) {
			String errMsg = String.format(
					"The Dispatcher is disconnected and can no"
							+ " longer monitor the execution of the job Id %d",
					jobId);
			LOGGER.error(errMsg);
		}

	}

}
