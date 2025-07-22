package com.ey.advisory.einv.app.api;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.PerformRetryException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sai.Pakanati This Class is Responsible for performing a retry for few
 *         Error Scenarios received either from IRP or Gateway
 */
@Slf4j
@Component
public class EINVAPIRequestExecutorImpl implements EINVAPIRequestExecutor {

	private static final int RETRY_COUNT = 2;

	@Autowired
	@Qualifier("GSTNHttpClient")
	private HttpClient httpClient;

	@Override
	public Pair<String, Integer> execute(HttpUriRequest req) throws Exception {
		int retryCount = 0;
		Pair<String, Integer> pair = null;
		String reqUrl = req.getURI().toString();
		while (true) {
			try {
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("Hitting Einv API {} for {} time", reqUrl,
							(retryCount + 1));
				return executeApi(req);
			} catch (PerformRetryException ex) {
				retryCount++;
				pair = ex.getRespPair();
				LOGGER.error(
						"We have received Invalid Response from EINV API,"
								+ " Response is {},"
								+ " Hence About retry EINV API {} for {} time",
						pair.getValue0(), reqUrl, (retryCount + 1));
				if (retryCount >= RETRY_COUNT)
					break;
			}
		}
		return pair;
	}

	private Pair<String, Integer> executeApi(HttpUriRequest req)
			throws Exception {
		HttpResponse resp = null;
		try {
			resp = httpClient.execute(req);
		} catch (Exception e) {
			LOGGER.error("Error While invoking EINV API,", e);
			Pair<String, Integer> pair = new Pair<>(e.getMessage(), 500);
			throw new PerformRetryException(pair);
		}
		Integer httpStatusCd = resp.getStatusLine().getStatusCode();
		String apiResp = EntityUtils.toString(resp.getEntity());
		Pair<String, Integer> pair = new Pair<>(apiResp, httpStatusCd);
		boolean isRetryable = isRetryable(apiResp);
		if (isRetryable)
			throw new PerformRetryException(pair);
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("EINV has Returned Valid Response - {}", apiResp);
		return pair;
	}

	private static boolean isRetryable(String apiResp) {

		boolean isRetryEnabled = false;
		JsonObject jsonObject = null;
		try {
			jsonObject = (JsonObject) new JsonParser().parse(apiResp);
			if (jsonObject.has("status_cd")) {
				String statusCd = jsonObject.get("status_cd").getAsString()
						.trim();
				if ("0".equals(statusCd)) {
					int errorCode = jsonObject.get("errorCode").getAsInt();
					if (errorCode == 9000)
						isRetryEnabled = true;
				}
			}
		} catch (Exception e) {
			LOGGER.error(
					"We have received Invalid Response from EINV, response is {}",
					apiResp);
			isRetryEnabled = true;
		}
		return isRetryEnabled;
	}
}