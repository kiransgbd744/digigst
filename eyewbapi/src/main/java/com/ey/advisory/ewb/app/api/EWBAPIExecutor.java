package com.ey.advisory.ewb.app.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.service.ERPReqRespLogHelper;

import lombok.extern.slf4j.Slf4j;

@Component("EWBAPIExecutor")
@Slf4j
public class EWBAPIExecutor implements APIExecutor {

	@Autowired
	@Qualifier("EWBAPIVersionCalculatorImpl")
	private APIVersionCalculator versionCalculator;

	@Autowired
	@Qualifier("EWBAPIReqBuilderImpl")
	private APIReqBuilder reqHdrsBuilder;

	@Autowired
	@Qualifier("EWBAPIReqPartsEncryptorImpl")
	private APIReqPartsEncryptor encryptor;

	@Autowired
	@Qualifier("EWBNICAPIApacheClientExecutorImpl")
	private NICAPIExecutor nicApiExecutor;

	@Autowired
	@Qualifier("EWBAPIResponseDecryptorImpl")
	private APIResponseDecryptor decryptor;

	@Autowired
	@Qualifier("EWBAPIExecConfigBuilderImpl")
	APIExecConfigBuilder apiExecConfigBuilder;

	@Autowired
	@Qualifier("EWBAPIResponseProcessImpl")
	APIResponseProcessor apiResponseProcessor;

	@Autowired
	ERPReqRespLogHelper reqLogHelper;

	@Override
	public APIResponse execute(APIParams params, String reqData) {

		// Build the APIConfig and Auth Info objects depending on the
		// parameters passed by the caller.
		if (LOGGER.isDebugEnabled()
				&& (reqData != null && !reqData.isEmpty())) {
			LOGGER.debug("Input Json is " + reqData);
		}

		reqLogHelper.logRequest(reqData, params.getApiProvider().name());

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

		// Create the reqParts object
		APIReqParts reqParts = new APIReqParts(reqHdrMap, qryParamsMap,
				reqData);

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
