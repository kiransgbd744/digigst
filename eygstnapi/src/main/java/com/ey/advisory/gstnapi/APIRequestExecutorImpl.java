package com.ey.advisory.gstnapi;

import java.time.LocalDateTime;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.JsonUtil;
import com.ey.advisory.common.PerformRetryException;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.impl.APIError;
import com.ey.advisory.core.async.domain.master.GSTNAPICountConfig;
import com.ey.advisory.core.async.repositories.master.EYConfigRepository;
import com.ey.advisory.core.async.repositories.master.GSTNAPICountConfigRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sai.Pakanati This Class is Responsible for performing a retry for few
 *         Error Codes received either from GSTN or Gateway
 */
@Slf4j
@Component
public class APIRequestExecutorImpl implements APIRequestExecutor {

	private static final int RETRY_COUNT = 2;

	@Autowired
	@Qualifier("GSTNHttpClient")
	private HttpClient httpClient;

	@Autowired
	@Qualifier("InternalHttpClient")
	private HttpClient internalHttpClient;

	@Autowired
	private GSTNAPICountConfigRepository gstnCountRepo;

	@Autowired
	private Environment env;

	@Autowired
	private EYConfigRepository eyConfigRepo;

	@Override
	public Pair<String, Integer> execute(HttpUriRequest req,
			Map<String, Object> context) throws Exception {
		int retryCount = 0;
		Pair<String, Integer> pair = null;
		String reqUrl = req.getURI().toString();
		while (true) {
			try {
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("Hitting GSTN API {} for {} time", reqUrl,
							(retryCount + 1));
				return executeApi(req, context);
			} catch (PerformRetryException ex) {
				retryCount++;
				pair = ex.getRespPair();
				LOGGER.error(
						"We have received Invalid Response from GSTN,"
								+ " Response is {},"
								+ " Hence About retry GSTN API {} for {} time",
						pair.getValue0(), reqUrl, (retryCount + 1));

				/*
				 * if (pair.getValue0().contains("API plan limit exceeded")) {
				 * 
				 * eyConfigRepo.updateValueOnKey(
				 * PublicApiConstants.END_POINT_KEY,
				 * PublicApiConstants.ALANKIT);
				 * 
				 * }
				 */
				if (pair.getValue0().contains("API plan limit exceeded")
						|| retryCount >= RETRY_COUNT) {
					break;
				}
			}
		}
		return pair;
	}

	private Pair<String, Integer> executeApi(HttpUriRequest req,
			Map<String, Object> context) throws Exception {
		HttpResponse resp = null;
		try {
			resp = httpClient.execute(req);
			if (context.containsKey(PublicApiConstants.APIIDENTIFER)) {
				logGSTNApiRequest(context);
			}
		} catch (Exception ex) {
			LOGGER.error("Exception while invoking GSP Gateway", ex);
			throw new PerformRetryException(new Pair<>(ex.getMessage(), 500));
		}

		Integer httpStatusCd = resp.getStatusLine().getStatusCode();
		String apiResp = EntityUtils.toString(resp.getEntity());
		Pair<String, Integer> pair = new Pair<>(apiResp, httpStatusCd);
		boolean isRetryable = isRetryable(apiResp, context);
		if (isRetryable)
			throw new PerformRetryException(pair);
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("GSTN has Returned Valid Response - {}", apiResp);
		return pair;
	}

	/*
	 * Retry is enabled for the following response
	 * {"status_cd":"0","error":{"error_cd":"INVJ01",
	 * "message":"Invalid response from GSTIN"}} {"gsp_status_cd"
	 * :"0","gsp_message":"Unable to Process Request"} This Method will return
	 * true for above Responses . Hence we are retrying the api invocation
	 */
	private boolean isRetryable(String apiResp, Map<String, Object> context) {
		JsonObject jsonObject = null;
		try {
			jsonObject = (JsonObject) new JsonParser().parse(apiResp);
		} catch (JsonParseException e) {
			LOGGER.error(
					"We have received Invalid Response from GSTN, response is {}",
					apiResp);
			return true;
		} catch (Exception e){
			LOGGER.error(
					"We have received Invalid Response from GSTN, response is {}",
					apiResp);
			throw new InvalidAPIResponseException("Recieved Invalid Response from GSTN");
		}
		if (jsonObject.has("status_cd")) {
			// Logging the gstn request
			String apiStatus = jsonObject.get("status_cd").getAsString().trim();
			if ("1".equals(apiStatus) || "2".equals(apiStatus)) {
				return false;
			} else if ("0".equals(apiStatus)) {
				String errorJson = jsonObject.get("error").toString();
				Gson gson = JsonUtil.newGsonInstance();
				APIError error = gson.fromJson(errorJson, APIError.class);
				String errCode = error.getErrorCode();
				return "INVJ01".equals(errCode);
			}
		} else {
			if (context.containsKey(PublicApiConstants.APIIDENTIFER)) {
				decrementPublicApiUsage(context);
			}

			return jsonObject.has("gsp_status_cd");
		}
		return false;
	}

	/*
	 * This method is responsible to log API Requests in Master DB. Currently
	 * only Public API requests invocations are logged for a group and API
	 * Type.Going forward based these counts will be looked up and put the
	 * limits for each group. This will be enhanced to define PUSH Type as well
	 */
	private void logGSTNApiRequest(Map<String, Object> context) {

		String gstin = PublicApiContext
				.getContextMap(PublicApiConstants.GSTIN) != null
						? PublicApiContext
								.getContextMap(PublicApiConstants.GSTIN)
								.toString()
						: PublicApiConstants.UNKNOWN;

		String fy = PublicApiContext
				.getContextMap(PublicApiConstants.FY) != null
						? PublicApiContext.getContextMap(PublicApiConstants.FY)
								.toString()
						: null;

		String endPoint = PublicApiContext
				.getContextMap(PublicApiConstants.END_POINT) != null
						? (String) PublicApiContext
								.getContextMap(PublicApiConstants.END_POINT)
						: PublicApiConstants.UNKNOWN;

		String source = PublicApiContext
				.getContextMap(PublicApiConstants.SOURCE) != null
						? PublicApiContext
								.getContextMap(PublicApiConstants.SOURCE)
								.toString()
						: PublicApiConstants.UNKNOWN;

		String apiType = (String) context.get(PublicApiConstants.APIIDENTIFER);
		LocalDateTime utcDatetime = LocalDateTime.now();
		LocalDateTime istDatetime = EYDateUtil
				.toISTDateTimeFromUTC(utcDatetime);
		GSTNAPICountConfig config = new GSTNAPICountConfig();
		config.setGroupCode(TenantContext.getTenantId());
		config.setSummaryDate(istDatetime.toLocalDate());
		config.setApiType(apiType);
		config.setGstin(gstin);
		config.setFy(fy);
		config.setEndPoint(endPoint);
		config.setSource(source);
		config.setCreatedOn(LocalDateTime.now());
		try {
			gstnCountRepo.save(config);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Logged Req Identifier-{} and Group{}", apiType,
						TenantContext.getTenantId());
		} catch (Exception e) {
			LOGGER.error(
					"Error while Logging the Gstn Public Api Counts for Group {}",
					TenantContext.getTenantId(), e);
		}
	}

	private void decrementPublicApiUsage(Map<String, Object> context) {

		if (!context.containsKey(PublicApiConstants.APIIDENTIFER)) {
			return;
		}
		try {

			String url = env.getProperty("public.api.counter.api");
			if (url == null) {
				LOGGER.error("Counter app get Usage & limit count URL in "
						+ "Application.properties is empty");
				return;
			}

			HttpGet httpGet = new HttpGet(url + "decrementUsageCount?groupCode="
					+ TenantContext.getTenantId());

			HttpResponse resp = internalHttpClient.execute(httpGet);
			Integer httpStatusCd = resp.getStatusLine().getStatusCode();
			String apiResp = EntityUtils.toString(resp.getEntity());
			if (httpStatusCd == 200) {
				JsonObject reqObject = (new JsonParser()).parse(apiResp)
						.getAsJsonObject();
				String respStatus = reqObject.get("hdr").getAsString();

				if ("S".equalsIgnoreCase(respStatus)) {
					if (LOGGER.isDebugEnabled())
						LOGGER.debug(
								"Decrementing the Gstn Public Api usage Counts for"
										+ " Group {} is succesfull {}",
								TenantContext.getTenantId(),
								reqObject.get("resp").getAsString());

				} else {
					LOGGER.error(
							"Error while decrementing the Gstn Public Api usage"
									+ " Counts for Group {}",
							TenantContext.getTenantId(),
							reqObject.get("resp").getAsString());
				}
			} else {
				LOGGER.error(
						"Error while invoking the counter App "
								+ "Counts for Group {} with status {}",
						TenantContext.getTenantId(), httpStatusCd);

			}
		} catch (Exception e) {
			LOGGER.error(
					"Error while decrementing the"
							+ " Gstn Public Api usage Counts for Group {}",
					TenantContext.getTenantId(), e);
		}

	}
}