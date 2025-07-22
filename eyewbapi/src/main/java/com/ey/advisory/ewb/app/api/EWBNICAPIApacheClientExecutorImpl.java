package com.ey.advisory.ewb.app.api;

import java.util.Date;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.service.ERPReqRespLogHelper;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.ewb.app.api.log.NICAPIExecStatisticsLogger;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("EWBNICAPIApacheClientExecutorImpl")
public class EWBNICAPIApacheClientExecutorImpl implements NICAPIExecutor {

	private static final FastDateFormat DATE_FORMAT = FastDateFormat
			.getInstance("yyyy-mm-dd'T'HH:mm:ss");

	private int connectionTimeout;

	private int readTimeout;

	private int connectionManagerTimeout;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	@Autowired
	ERPReqRespLogHelper reqLogHelper;

	@Autowired
	private EWBAPIRequestExecutor ewbapiReqExec;

	@PostConstruct
	public void init() {
		Map<String, Config> configMap = configManager.getConfigs("TIMEOUT",
				"api");
		connectionTimeout = configMap.containsKey("api.nic.conn_timeout")
				? Integer.valueOf(
						configMap.get("api.nic.conn_timeout").getValue())
				: 500000;
		readTimeout = configMap.containsKey("api.nic.read_timeout")
				? Integer.valueOf(
						configMap.get("api.nic.read_timeout").getValue())
				: 500000;
		connectionManagerTimeout = configMap
				.containsKey("api.nic.conn_mngr_timeout")
						? Integer.valueOf(
								configMap.get("api.nic.conn_mngr_timeout")
										.getValue())
						: 500000;
	}

	@Override
	public String execute(APIParams params, String version, APIConfig apiconfig,
			APIExecParties parties, Map<String, Object> context,
			APIReqParts reqParts) {
		Pair<String, Integer> response = null;
		String respString = null;
		APIVersionConfig versionConfig = apiconfig.getConfigForVersion(version);
		boolean isVersionIncludedInUrl = versionConfig.isVersionIncludedInUrl();
		String httpMethod = versionConfig.getHttpMethod();
		RequestConfig config = RequestConfig.custom()
				.setConnectTimeout(connectionTimeout)
				.setConnectionRequestTimeout(connectionManagerTimeout)
				.setSocketTimeout(readTimeout).build();
		JsonObject logJson = new JsonObject();
		long stTime = 0L;
		int statusCode = 0;

		try {
			StringBuilder resource = new StringBuilder();
			if (!params.getApiAction()
					.equalsIgnoreCase(APIIdentifiers.GET_AUTH_TOKEN)) {
				resource.append(versionConfig.baseUrl);
				resource.append(isVersionIncludedInUrl ? "/" + version : "");
			} else {
				resource.append(
						versionConfig.baseUrl.replace("/ewaybillapi", ""));
			}
			resource.append(versionConfig.getUrlSuffix());
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
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("About to execute NIC EWB API - {}",
						params.getApiAction());
			}
			logJson.addProperty("method", httpMethod);
			logJson.addProperty("url", resource.toString());
			logJson.addProperty("action", apiconfig.getAction());
			stTime = System.currentTimeMillis();
			logJson.addProperty("stTime", formatDateTime(stTime));
			reqLogHelper.updateNICReqRespTimeStamp("Request", false);
			if ("POST".equals(httpMethod)) {
				HttpPost httpPost = new HttpPost(resource.toString());
				for (Map.Entry<String, String> entry : reqHeadersMap
						.entrySet()) {
					httpPost.setHeader(entry.getKey(), entry.getValue());
				}
				httpPost.setConfig(config);
				StringEntity entity = new StringEntity(reqParts.getReqData());
				httpPost.setEntity(entity);
				response = ewbapiReqExec.execute(httpPost);
			} else {
				HttpGet httpGet = new HttpGet(resource.toString());
				for (Map.Entry<String, String> entry : reqHeadersMap
						.entrySet()) {
					httpGet.setHeader(entry.getKey(), entry.getValue());
				}
				httpGet.setConfig(config);
				response = ewbapiReqExec.execute(httpGet);
			}
			statusCode = response.getValue1();
			respString = response.getValue0();
			reqLogHelper.updateNICReqRespTimeStamp("Response", false);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Executed NIC EWB API, Encrypted Response is - {} ",
						respString);
			}
		} catch (Exception e) {
			String msg = "Error while executing NIC API";
			LOGGER.error(msg, e);
			throw new APIException(e.getMessage());
		} finally {
			long endTime = System.currentTimeMillis();
			long timeDiff = (endTime - stTime);
			logJson.addProperty("endTime", formatDateTime(endTime));
			logJson.addProperty("timeDiff", timeDiff + " ms");
			logJson.addProperty("httpStatus", statusCode);
			reqLogHelper.updateNICTimeTakenInSec(timeDiff / 1000);
			NICAPIExecStatisticsLogger.printExecStatistics(logJson.toString());
		}
		return respString;
	}

	private String formatDateTime(long millis) {
		return DATE_FORMAT.format(new Date(millis));
	}
}
