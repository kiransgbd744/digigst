
package com.ey.advisory.gstnapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.gstnapi.domain.client.APIInvocationReqEntity;
import com.ey.advisory.gstnapi.repositories.client.APIResponseRepository;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("TokenResponseRetryBlockImpl")
public class TokenResponseRetryBlockImpl implements RetryBlock {

	@Autowired
	@Qualifier("GstnAPICallImpl")
	private GstnApiCall gstnApiCall;

	@Autowired
	@Qualifier("UrlListProcessorImpl")
	private UrlListProcessor urlProcessor;

	@Autowired
	@Qualifier("CallHandlersImpl")
	private CallHandlers callHandlers;

	@Autowired
	private APIExecEventsLogger logger;

	@Autowired
	APIResponseRepository apiRespRepo;

	@Autowired
	private LoadInvocationRequest loadInvocReq;

	@Override
	public Pair<Pair<ExecResult<Boolean>, ExecResult<Boolean>>, Map<String, Object>> retry(
	        String params) {

		ExecResult<Boolean> callHandlerExecResult = null;
		Map<String, Object> retryCtxMap = new HashMap<>();
		Gson gson = new Gson();
		JsonObject obj = new JsonParser().parse(params).getAsJsonObject();
		JsonObject reqEntityObj = obj.get("requestEntity").getAsJsonObject();
		APIInvocationReqEntity req = gson.fromJson(reqEntityObj,
		        APIInvocationReqEntity.class);
		JsonObject tokenParams = obj.get("tokenParams").getAsJsonObject();
		String msg = String.format("Begining the token response processing "
		        + "with token params %s", tokenParams);
		logger.logInfo(req.getId(), msg);
		loadInvocReq.loadInvocationRequest(req.getId(), retryCtxMap);
		APIParams tokenApiParams = prepareApiParams(req, tokenParams);
		JsonElement tokenJsonElement = gson.toJsonTree(tokenApiParams);
		JsonObject tokenapiParams = new JsonObject();
		logger.logInfo(req.getId(), "Calling GSTN  to fetch UrlList");
		tokenapiParams.add("apiParams", tokenJsonElement);
		ExecResult<APIResponse> apiResponseExecResult = gstnApiCall
		        .callGstnApi(gson.toJson(tokenapiParams), retryCtxMap)
		        .getValue0();

		if (apiResponseExecResult.isSuccess()) {
			logger.logInfo(req.getId(), "UrlList fetch successfull");
			APIResponse response = apiResponseExecResult.getResult();
			Pair<String, List<String>> urlRespPair = getUrlsFromResponse(
			        response);
			String ek = urlRespPair.getValue0();
			List<String> urlList = urlRespPair.getValue1();
			logger.logInfo(req.getId(), "size of the url is " + urlList.size()
			        + " and ek is " + ek);
			APIParams apiParams = gson.fromJson(req.getApiParams(),
			        APIParams.class);
			logger.logInfo(req.getId(), "processing of urls begin");
			ExecResult<Boolean> urlProcessorResult = urlProcessor
			        .processTasksForUrls(urlList, tokenApiParams, ek,
			                response.getSk(), req.getId(), retryCtxMap)
			        .getValue0();
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
				        "URL Processing is completed for Group {},"
				                + " API Params {}, Processing Result is {}",
				        TenantContext.getTenantId(), apiParams,
				        urlProcessorResult.getResult());
			logger.logInfo(req.getId(),
			        "processing of urls Ended with either Failed/Success Status");
			if (urlProcessorResult.isSuccess()) {
				LOGGER.debug(
				        "URL's are downloaded successfully for group Code {}, Api Params {}",
				        TenantContext.getTenantId(), apiParams);
				logger.logInfo(req.getId(), "All the urls are processed "
				        + "successfully, fetching the successIds from api response");
				List<Long> resultIds = apiRespRepo
				        .getResultIdByInvocationId(req.getId());
				logger.logInfo(req.getId(),
				        "success Ids are fetched which is having size of "
				                + resultIds.size());

				callHandlerExecResult = callSuccessHandler(req, resultIds,
				        retryCtxMap);
			} else {
				LOGGER.debug(
				        "One or more URL's are Failed to download"
				                + " for group Code {}, Api Params {}",
				        TenantContext.getTenantId(), apiParams);
				apiRespRepo.updateRequestStatus(req.getId(),
				        GstnApiWrapperConstants.FAILED);
				APIInvocationError error = createApiInvocationError(
				        urlProcessorResult);
				logger.logInfo(req.getId(), "processing of urls "
				        + "failed, Calling failure handler");
				callHandlerExecResult = callFailureHandler(req.getId(), error,
				        req.getFailureHandler(), req.getApiParams(),
				        retryCtxMap);
				return new Pair<>(new Pair<>(urlProcessorResult.toBoolResult(),
				        callHandlerExecResult), retryCtxMap);
			}
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
				        "About to return urlprocessing result for Group Code {}, Api Params {}",
				        TenantContext.getTenantId(), apiParams);
			logger.logInfo(req.getId(), "About to return urlprocessing result");
			return new Pair<>(new Pair<>(apiResponseExecResult.toBoolResult(),
			        callHandlerExecResult), retryCtxMap);

		}
		APIInvocationError error = createApiInvocationError(
		        apiResponseExecResult);
		logger.logError(req.getId(),
		        "Gstn called failed " + error.getErrorCode(),
		        error.getErrorDesc());
		ExecResult<Boolean> handlerExecResp = callFailureHandler(req.getId(),
		        error, req.getFailureHandler(), req.getApiParams(),
		        retryCtxMap);
		return new Pair<>(new Pair<>(apiResponseExecResult.toBoolResult(),
		        handlerExecResp), retryCtxMap);

	}

	private APIParams prepareApiParams(APIInvocationReqEntity req,
	        JsonObject tokenParams) {
		Gson gson = new Gson();
		JsonObject obj = new JsonParser().parse(req.getApiParams())
		        .getAsJsonObject();
		APIParams apiParams = gson.fromJson(obj.get("apiParams"),
		        APIParams.class);
		APIParam param1 = new APIParam("gstin",
		        apiParams.getAPIParamValue("gstin"));
		APIParam param2 = new APIParam("ret_period",
		        apiParams.getAPIParamValue("ret_period"));
		APIParam param3 = new APIParam("token",
		        tokenParams.get("token").getAsString());

		if (APIIdentifiers.GET_IRN_LIST.equalsIgnoreCase(req.getApiIdentifier())
		        || APIIdentifiers.GET_IRN_JSON
		                .equalsIgnoreCase(req.getApiIdentifier())) {
			return new APIParams(TenantContext.getTenantId(),
			        APIProviderEnum.GSTN, APIIdentifiers.GET_FILE_LIST, param1,
			        param2, param3);
		} else if (APIIdentifiers.IMS_INVOICE
		        .equalsIgnoreCase(req.getApiIdentifier())) {
			return new APIParams(TenantContext.getTenantId(),
			        APIProviderEnum.GSTN, APIIdentifiers.IMS_FILE_LIST, param1,
			        param3);
		} else
			return new APIParams(TenantContext.getTenantId(),
			        APIProviderEnum.GSTN, APIIdentifiers.GET_FILE_DETAILS,
			        param1, param2, param3);

	}

	private Pair<String, List<String>> getUrlsFromResponse(
	        APIResponse response) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		TokenResponseDto tokenResponsedto = gson
		        .fromJson(response.getResponse(), TokenResponseDto.class);
		List<TokenUrlDto> urlObjects = tokenResponsedto.getUrlList();
		String ek = tokenResponsedto.getEk();
		List<String> urlList = urlObjects.stream().map(TokenUrlDto::getUrl)
		        .collect(Collectors.toList());
		return new Pair<>(ek, urlList);
	}

	private ExecResult<Boolean> callSuccessHandler(
	        APIInvocationReqEntity requestEntity, List<Long> ids,
	        Map<String, Object> retryCtxMap) {
		return callHandlers
		        .callSuccessHandler(requestEntity.getApiParams(), ids,
		                requestEntity.getId(),
		                requestEntity.getSuccessHandler(), retryCtxMap)
		        .getValue0();

	}

	private <T> APIInvocationError createApiInvocationError(
	        ExecResult<T> execResult) {
		return APIInvokerUtil.createErrorObject(execResult.getErrMsg(),
		        execResult.getExecErrCode(), execResult.getExtErrCode(),
		        execResult.getException());

	}

	private ExecResult<Boolean> callFailureHandler(Long requestId,
	        APIInvocationError error, String failureHandler, String apiParams,
	        Map<String, Object> retryCtxMap) {
		return callHandlers.callFailureHandler(requestId, error, failureHandler,
		        apiParams, retryCtxMap).getValue0();

	}

}
