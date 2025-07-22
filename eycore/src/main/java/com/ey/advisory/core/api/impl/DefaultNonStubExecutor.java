package com.ey.advisory.core.api.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.ApiCallLimitExceededException;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Component("DefaultNonStubExecutor")
public class DefaultNonStubExecutor implements APIExecutor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultNonStubExecutor.class);
	@Autowired
	private APIExecComponentsFactory apiExecComponentsFactory;

	@Autowired
	private SaveBatchReqAndRespPayloadDumpHelper saveBatchHelper;

	@Autowired
	@Qualifier("InternalHttpClient")
	private HttpClient httpClient;

	private static final List<String> PUBLIC_API_LIST = ImmutableList.of(
			APIIdentifiers.GET_FILLING_STATUS,
			APIIdentifiers.GET_GSTIN_DETAILS,
			APIIdentifiers.VENDOR_GET_PREFERENCE);

	@Autowired
	private Environment env;

	@Override
	public APIResponse execute(APIParams params, String reqData) {

		if (!isPublicApiAllowed(params)) {

			throw new ApiCallLimitExceededException(
					"Daily GSTN API calls have been exhausted. Please try again later");
		}

		// Saving the Request Payload into batch table if the framework
		// request is for SaveToGstn operation.
		saveBatchHelper.dumpReqJsonPayload(params, reqData);

		// Build the APIConfig and AuthInfo objects depending on the
		// parameters passed by the caller.
		if (LOGGER.isDebugEnabled() && reqData != null && !reqData.isEmpty()) {
			String msg = String.format("Input Json is '%s'", reqData);
			LOGGER.debug(msg);
		}

		APIConfigBuilder apiConfigBuilder = apiExecComponentsFactory
				.getApiConfigBuilder();
		APIConfig apiConfig = apiConfigBuilder.build(params);

		APIExecPartiesBuilder apiPartiesBuilder = apiExecComponentsFactory
				.getApiExecPartiesBuilder();
		APIExecParties parties = apiPartiesBuilder.build(params);

		Map<String, Object> context = new HashMap<>();

		APIVersionCalculator versionCalculator = apiExecComponentsFactory
				.getVersionCalculator();
		// Get the API Version of the specified API.
		String apiVersion = versionCalculator.getAPIVersion(params, apiConfig,
				parties, context);

		// Construct a new APIParams object with the calculated version
		// included. Here, the version cannot be null/empty. Otherwise this
		// constructor will throw an exception.
		APIParams paramsWithVersion = new APIParams(params, apiVersion);

		APIAuthInfoLoader authInfoLoader = apiExecComponentsFactory
				.getAuthInfoLoader();
		// Load the Authentication/Authorization information associated with
		// the End User of the API.
		APIAuthInfo authInfo = authInfoLoader.loadApiAuthInfo(paramsWithVersion,
				apiConfig, parties, context);

		APIReqHeaderBuilder apiReqHeaderBuilder = apiExecComponentsFactory
				.getReqHearderBuilder();
		// Build the API Request Headers.
		Map<String, String> reqHdrMap = apiReqHeaderBuilder.buildReqHeaderMap(
				paramsWithVersion, apiConfig, parties, authInfo, context);
		APIReqQueryParamsBuilder apiReqQryParamsBuilder = apiExecComponentsFactory
				.getReqQueryParamsBuilder();
		// Build the Query Parameter map.
		Map<String, String> qryParamsMap = apiReqQryParamsBuilder
				.buildQueryParamsMap(paramsWithVersion, apiConfig, parties,
						authInfo, context);

		// Create the reqParts object
		APIReqParts reqParts = new APIReqParts(reqHdrMap, qryParamsMap,
				reqData);

		APIReqPartsEncryptor encryptor = apiExecComponentsFactory
				.getReqPartsEncryptor();

		// Perform Encryption on individual header parameters, query parameters
		// and the request body.
		APIReqParts encReqParts = encryptor.encrypt(paramsWithVersion,
				apiConfig, parties, reqParts, authInfo, context);
		ProviderAPIExecutor apiExecutor = apiExecComponentsFactory
				.getapiExecutor();
		// Build the URL, populate headers, execute the API and get
		// the response.
		String encResp = apiExecutor.execute(paramsWithVersion, apiConfig,
				parties, encReqParts, authInfo, context);

		APIResponseHandlerFactory apiRespHandlerFactory = apiExecComponentsFactory
				.getApiResponseHandlerFacctory();
		// After executing the API, use the response string to decide on
		// what kind of handling we've to do.
		APIResponseHandler handler = apiRespHandlerFactory.getHandler(
				paramsWithVersion, apiConfig, parties, encReqParts, authInfo,
				encResp, context);

		return handler.handleResponse(paramsWithVersion, apiConfig, parties,
				encReqParts, authInfo, encResp, context);

	}

	private boolean isPublicApiAllowed(APIParams params) {

		if (!PUBLIC_API_LIST.contains(params.getApiIdentifier())) {
			return true;
		}

		boolean isAllowed = false;
		try {

			String url = env.getProperty("public.api.counter.api");
			if (url == null) {
				LOGGER.error("Counter app get Usage & limit count URL in "
						+ "Application.properties is empty");
				return true;
			}

			HttpGet httpGet = new HttpGet(url + "isApiCallAllowed?groupCode="
					+ TenantContext.getTenantId());

			HttpResponse resp = httpClient.execute(httpGet);
			Integer httpStatusCd = resp.getStatusLine().getStatusCode();
			String apiResp = EntityUtils.toString(resp.getEntity());
			if (httpStatusCd == 200) {
				JsonObject reqObject = (new JsonParser()).parse(apiResp)
						.getAsJsonObject();
				String respStatus = reqObject.get("hdr").getAsString();

				if ("S".equalsIgnoreCase(respStatus)) {
					isAllowed = reqObject.get("resp").getAsBoolean();

					LOGGER.debug(
							" GSTN Public Api allow status for"
									+ " Group is succesfull {} with {}",
							TenantContext.getTenantId(), isAllowed);
					return isAllowed;
				} else {
					LOGGER.error(
							" GSTN Public Api allow status for"
									+ " Group is error for {} with {}",
							TenantContext.getTenantId(),
							reqObject.get("resp").getAsString());
					return false;
				}
			} else {
				LOGGER.error(
						"Error while invoking the GSTN Public Api allow Api  "
								+ " for Group {} with status {} and error ",
						TenantContext.getTenantId(), httpStatusCd);
				return false;
			}
		} catch (Exception e) {
			LOGGER.error(
					"Error while getting GSTN Public Api allowed for Group {}",
					TenantContext.getTenantId(), e);
			return false;
		}

	}

}
