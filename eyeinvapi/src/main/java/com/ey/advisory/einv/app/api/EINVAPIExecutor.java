package com.ey.advisory.einv.app.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.service.ERPReqRespLogHelper;
import com.ey.advisory.core.api.APIProviderEnum;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

@Component("EINVAPIExecutor")
@Slf4j
public class EINVAPIExecutor implements APIExecutor {

	private static final List<String> INCLUDE_APIS = ImmutableList
			.of(APIIdentifiers.GENERATE_EINV, APIIdentifiers.GET_SYNCGSTINFROMCP);

	@Autowired
	@Qualifier("EINVAPIVersionCalculatorImpl")
	private APIVersionCalculator versionCalculator;

	@Autowired
	private APIReqBuilder reqHdrsBuilder;

	@Autowired
	@Qualifier("EINVAPIReqPartsEncryptorImpl")
	private APIReqPartsEncryptor encryptor;

	@Autowired
	@Qualifier("NICAPIApacheClientExecutorImpl")
	private NICAPIExecutor nicApiExecutor;

	@Autowired
	@Qualifier("EINVAPIResponseDecryptorImpl")
	private APIResponseDecryptor decryptor;

	@Autowired
	APIExecConfigBuilder apiExecConfigBuilder;

	@Autowired
	@Qualifier("APIResponseProcessImpl")
	APIResponseProcessor apiResponseProcessor;

	@Autowired
	ERPReqRespLogHelper reqLogHelper;

	@Autowired
	CommonUtil commUtil;

	@Override
	public APIResponse execute(APIParams params, String reqData) {

		// Build the APIConfig and Auth Info objects depending on the
		// parameters passed by the caller.
		if (LOGGER.isDebugEnabled()
				&& (reqData != null && !reqData.isEmpty())) {
			LOGGER.debug("Input Json is " + reqData);
		}
		APIProviderEnum apiProvider = commUtil.getSource(params.getApiParamValue(APIReqParamConstants.GSTIN));
		if (INCLUDE_APIS.contains(params.getApiAction())) {
			params.setApiProvider(apiProvider);
		}
		reqLogHelper.logRequest(reqData, apiProvider.name());

		APIConfig apiConfig = apiExecConfigBuilder.buildAPIConfig(params);

		APIExecParties parties = apiExecConfigBuilder
				.buildAPIPartiesInfo(params);

		Map<String, Object> context = new HashMap<>();

		// Get the API Version of the specified API.
		String apiVersion = versionCalculator.getAPIVersion(params, apiConfig,
				parties, context);

		// Build the API Request Headers.
		Map<String, String> reqHdrMap = reqHdrsBuilder.buildReqHeaderMap(params,
				apiVersion, apiConfig, parties, context);

		// Build the Query Parameter map.
		Map<String, String> qryParamsMap = reqHdrsBuilder.buildQueryParamsMap(
				params, apiVersion, apiConfig, parties, context);

		// Build the Path Parameter map.
		Map<String, String> pathParamsMap = reqHdrsBuilder.buildPathParamsMap(
				params, apiVersion, apiConfig, parties, context);

		// Create the reqParts object
		APIReqParts reqParts = new APIReqParts(reqHdrMap, qryParamsMap,
				pathParamsMap, reqData);

		// Perform Encryption on individual header parameters, query parameters
		// and the request body.
		APIReqParts encReqParts = encryptor.encrypt(params, apiVersion,
				apiConfig, parties, context, reqParts);

		// Build the URL, populate headers, execute the API and get
		// the response
		String encResp = nicApiExecutor.execute(params, apiVersion, apiConfig,
				parties, context, encReqParts);

		APIResponse response = decryptor.decrypt(params, apiVersion, apiConfig,
				parties, context, encResp);

		return apiResponseProcessor.processResponse(params, apiVersion,
				apiConfig, parties, context, response);
	}

}
