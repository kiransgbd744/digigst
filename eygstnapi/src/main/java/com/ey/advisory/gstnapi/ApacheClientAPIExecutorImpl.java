package com.ey.advisory.gstnapi;

import java.util.Date;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.GSTNAPIUtil;
import com.ey.advisory.core.api.impl.APIAuthInfo;
import com.ey.advisory.core.api.impl.APIConfig;
import com.ey.advisory.core.api.impl.APIExecParties;
import com.ey.advisory.core.api.impl.APIReqParts;
import com.ey.advisory.core.api.impl.APIVersionConfig;
import com.ey.advisory.core.api.impl.ProviderAPIExecutor;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("GSTNAPIApacheClientExecutorImpl")
public class ApacheClientAPIExecutorImpl implements ProviderAPIExecutor {

	private static final FastDateFormat DATE_FORMAT = FastDateFormat
			.getInstance("yyyy-mm-dd'T'HH:mm:ss");

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	private int connectionTimeout;

	private int readTimeout;

	private int connectionManagerTimeout;

	@Autowired
	private APIRequestExecutor apiReqExec;

	@PostConstruct
	public void init() {
		Map<String, Config> configMap = configManager.getConfigs("GSTNAPI",
				"api");
		connectionTimeout = configMap
				.containsKey("api.gstn.global.conn_timeout")
						? Integer.valueOf(configMap
								.get("api.gstn.global.conn_timeout").getValue())
						: 5000;
		readTimeout = configMap.containsKey("api.gstn.global.read_timeout")
				? Integer.valueOf(configMap.get("api.gstn.global.read_timeout")
						.getValue())
				: 5000;
		connectionManagerTimeout = configMap.containsKey(
				"api.gstn.global.conn_mngr_timeout")
						? Integer.valueOf(configMap
								.get("api.gstn.global.conn_mngr_timeout")
								.getValue())
						: 5000;
	}

	@Override
	public String execute(APIParams params, APIConfig apiconfig,
			APIExecParties parties, APIReqParts reqParts, APIAuthInfo authInfo,
			Map<String, Object> context) {

		Pair<String, Integer> response = null;
		String respString = null;
		APIVersionConfig versionConfig = apiconfig
				.getConfigForVersion(params.getApiVersion());
		String httpMethod = versionConfig.getHttpMethod();
		String apiIdentifer = params.getApiIdentifier();
		RequestConfig config = RequestConfig.custom()
				.setConnectTimeout(connectionTimeout)
				.setConnectionRequestTimeout(connectionManagerTimeout)
				.setSocketTimeout(readTimeout).build();
		JsonObject logJson = new JsonObject();
		long stTime = 0L;
		int statusCode = 0;
		try {

			StringBuilder resource = resolveResourceUrl(versionConfig,
					apiIdentifer, context);

			Map<String, String> queryParamsMap = reqParts.getQueryParams();
			int count = 1;
			for (Map.Entry<String, String> entry : queryParamsMap.entrySet()) {
				if (count == 1) {
					resource.append(
							"?" + entry.getKey() + "=" + entry.getValue());
				} else {
					resource.append(
							"&" + entry.getKey() + "=" + entry.getValue());
				}
				count++;
			}
			Map<String, String> reqHeadersMap = reqParts.getHeaders();
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("About to execute GSTN API and URL is {}",
						resource.toString());
			}
			logJson.addProperty("method", httpMethod);
			logJson.addProperty("url", resource.toString());
			logJson.addProperty("action", apiconfig.getAction());
			stTime = System.currentTimeMillis();
			logJson.addProperty("stTime", formatDateTime(stTime));
			if ("POST".equals(httpMethod)) {
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("Post request Json Body is :"
							+ reqParts.getReqData());
				}
				HttpPost httpPost = new HttpPost(resource.toString());
				for (Map.Entry<String, String> entry : reqHeadersMap
						.entrySet()) {
					httpPost.setHeader(entry.getKey(), entry.getValue());
				}
				httpPost.setHeader("Content-Type", "application/json");
				httpPost.setConfig(config);
				StringEntity entity = new StringEntity(reqParts.getReqData());
				httpPost.setEntity(entity);
				response = apiReqExec.execute(httpPost, context);
			} else if ("PUT".equals(httpMethod)) {

				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("PUT request Json Body is :"
							+ reqParts.getReqData());
				}
				HttpPut httpPut = new HttpPut(resource.toString());
				for (Map.Entry<String, String> entry : reqHeadersMap
						.entrySet()) {
					httpPut.setHeader(entry.getKey(), entry.getValue());
				}
				httpPut.setHeader("Content-Type", "application/json");
				httpPut.setConfig(config);
				StringEntity entity = new StringEntity(reqParts.getReqData());
				httpPut.setEntity(entity);
				response = apiReqExec.execute(httpPut, context);
			} else {
				HttpGet httpGet = new HttpGet(resource.toString() + "&action="
						+ apiconfig.getAction());
				for (Map.Entry<String, String> entry : reqHeadersMap
						.entrySet()) {
					httpGet.setHeader(entry.getKey(), entry.getValue());
				}
				httpGet.setConfig(config);
				response = apiReqExec.execute(httpGet, context);
			}
			statusCode = response.getValue1();
			respString = response.getValue0();
		} catch (Exception e) {
			String msg = "Error while executing GSTN API";
			LOGGER.error(msg, e);
			throw new APIException(msg, e);
		} finally {
			long endTime = System.currentTimeMillis();
			long timeDiff = (endTime - stTime);
			logJson.addProperty("endTime", formatDateTime(endTime));
			logJson.addProperty("timeDiff", timeDiff + " ms");
			logJson.addProperty("httpStatus", statusCode);
		}
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Response is : {}", respString);
		}
		return respString;
	}

	/*
	 * This method resolves the base URL for Public APIs and taxpayer APIs. And
	 * also resolves EY internal or Alankit external URL in case of Public API
	 */
	private StringBuilder resolveResourceUrl(APIVersionConfig versionConfig,
			String apiIdentifer, Map<String, Object> context) {

		StringBuilder resource = new StringBuilder();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("About to assign API url in resolveResourceUrl");
		}

		if (!GSTNAPIUtil.isPublicApiRelatedRequest(apiIdentifer)) {
			return resource.append(versionConfig.getBaseUrl());
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Checking Endpoint for public apiIdentifer {} ",
					apiIdentifer);
		}

		context.put(PublicApiConstants.APIIDENTIFER, apiIdentifer);

		if (PublicApiConstants.EY.equalsIgnoreCase(PublicApiContext
				.getContextMap(PublicApiConstants.END_POINT).toString())) {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Assigning EY Endpoint {} for public apiIdentifer {} ",
						versionConfig.getBaseUrl(), apiIdentifer);
			}

			resource.append(versionConfig.getBaseUrl());

		} else {
			String url = PublicApiContext
					.getContextMap(PublicApiConstants.URL) == null
							? versionConfig.getBaseUrl()
							: PublicApiContext
									.getContextMap(PublicApiConstants.URL)
									.toString();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Assigning ext Endpoint {} for public apiIdentifer {} ",
						url, apiIdentifer);
			}

			resource.append(url);
		}
		return resource;
	}

	private String formatDateTime(long millis) {
		return DATE_FORMAT.format(new Date(millis));
	}
}
