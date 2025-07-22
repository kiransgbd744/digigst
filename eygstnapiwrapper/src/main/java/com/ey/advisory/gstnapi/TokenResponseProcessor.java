/**
 * 
 */
package com.ey.advisory.gstnapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.gstnapi.repositories.client.APIInvocationReqRepository;
import com.ey.advisory.gstnapi.repositories.client.APIResponseRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Slf4j
@Component("TokenResponseProcessor")
public class TokenResponseProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	APIExecutor apiExecutor;

	@Autowired
	APIInvocationReqRepository aiReqRepo;

	@Autowired
	APIResponseRepository apiRespRepo;

	@Autowired
	@Qualifier("UrlListProcessorImpl")
	UrlListProcessor urlProcessor;
	
	@Autowired
	@Qualifier("RetryLoopImpl")
	private RetryLoop retryLoop;

	@Override
	public void execute(Message message, AppExecContext context) {
		
		try {
			String jsonString = message.getParamsJson();
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Inside ApiInvocation Processor with"
						+ " request = '%s' ", jsonString);
				LOGGER.debug(msg);
			}
			retryLoop.retryLoop(GstnApiWrapperConstants.TOKEN_RESPONSE_RETRY_BLOCK,
					jsonString);

		} catch (Exception e) {
			String msg = String.format(
					"Error occured in InvocationReqProcessor "
					+ "for jobId = %d with message [%s]", 
					message.getId(), e.getMessage());
			APILogger.logError(msg, null, e);
			throw e;

		}
		/*Long requestId = null;
		Long invocationId = null;
		APIParams apiParams = null;
		try {
			String params = message.getParamsJson();
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonElement jelem = gson.fromJson(params, JsonElement.class);
			JsonObject jobj = jelem.getAsJsonObject();
			apiParams = gson.fromJson(jobj.get("apiParams").getAsString(),
					APIParams.class);
			JsonObject tokenParams = jobj.get("tokenParams").getAsJsonObject();
			requestId = jobj.get("requestId").getAsLong();
			 The below code will call the gstn API with the the token, 
			APIResponse response = callGstnApi(apiParams, tokenParams);
			if (response.isSuccess()) {
				List<String> urlList = getUrlsFromResponse(response);
				urlProcessor.processTasksForUrls(urlList, apiParams,
						response.getRek(), response.getSk(), invocationId,
						requestId);
				callSuccessFailureHandler(invocationId, requestId, null,
						GstnApiWrapperConstants.SUCCESS);
			} else {

				APIInvocationError error = createErroredResponse(response);
				updateFailedResponse(invocationId, requestId, error);
			}
		} catch (JsonParseException e) {
			String msg = "exception occured while parsing the json"
					+ e.getMessage();
			LOGGER.error(msg, e);
			APIInvocationError error = createErroredResponse(e);
			updateFailedResponse(invocationId, requestId, error);
		} catch (Exception e) {
			String msg = "exception occured " + e.getMessage();
			LOGGER.error(msg, e);
			APIInvocationError error = createErroredResponse(e);
			updateFailedResponse(invocationId, requestId, error);
		}

	}

	private void callSuccessFailureHandler(Long invocationId, Long requestId,
			APIInvocationError error, String successFailureFlag) {
		List<APIResponseEntity> responses = apiRespRepo
				.findByInvocationId(invocationId);
		List<Long> successIds = responses.stream().map(APIResponseEntity::getId)
				.collect(Collectors.toList());
		
		APIInvocationReqEntity apiReqEntity = aiReqRepo.findById(requestId)
				.orElseThrow(() -> new AppException(
						"requested entity does not exist"));
		String apiParams = apiReqEntity.getApiParams();
		JsonObject apiParamsJson = new Gson().fromJson(apiParams, JsonObject.class);
		if (GstnApiWrapperConstants.SUCCESS
				.equalsIgnoreCase(successFailureFlag)) {
			SuccessResult result = new SuccessResult(requestId, successIds,
					apiParamsJson.get("ctxParams").toString());
			StaticContextHolder.getBean(apiReqEntity.getSuccessHandler(),
					SuccessHandler.class).handleSuccess(result, apiReqEntity.getApiParams());

		} else {
			FailureResult result = new FailureResult(apiReqEntity.getId(),
					error,apiParamsJson.get("ctxParams").toString());
			StaticContextHolder.getBean(apiReqEntity.getFailureHandler(),
					FailureHandler.class).handleFailure(result, apiReqEntity.getApiParams());
		}

	}

	private List<String> getUrlsFromResponse(APIResponse response) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		TokenResponseDto tokenResponsedto = gson
				.fromJson(response.getResponse(), TokenResponseDto.class);
		List<TokenUrlDto> urlObjects = tokenResponsedto.getUrlList();
		return urlObjects.stream().map(TokenUrlDto::getUrl)
				.collect(Collectors.toList());
	}

	private APIResponse callGstnApi(APIParams apiParams,
			JsonObject tokenParams) {
		APIParam param1 = new APIParam("gstin",
				apiParams.getAPIParamValue("gstin"));
		APIParam param2 = new APIParam("ret_period",
				apiParams.getAPIParamValue("ret_period"));
		APIParam param3 = new APIParam("token",
				tokenParams.get("token").getAsString());
		APIParams params = new APIParams(TenantContext.getTenantId(),
				APIProviderEnum.GSTN, APIIdentifiers.GET_FILE_DETAILS, "v1.1",
				param1, param2, param3);
		return apiExecutor.execute(params, "");
	}

	private APIInvocationError createErroredResponse(APIResponse response) {
		return new APIInvocationError(GstnApiWrapperConstants.GSTN_ERROR,
				response.getError(), null);

	}

	private APIInvocationError createErroredResponse(Exception ex) {
		return new APIInvocationError(GstnApiWrapperConstants.INTERNAL_ERROR,
				null, ex);
	}

	private void updateFailedResponse(Long invocationId, Long requestId,
			APIInvocationError error) {
		aiReqRepo.updateStatus(GstnApiWrapperConstants.FAILED, requestId);
		APIResponseEntity responseEntity = new APIResponseEntity(
				error.getErrorCode(), error.getErrorDesc(),invocationId,
				GstnApiWrapperConstants.FAILED);
		responseEntity.setErrorCode(error.getErrorCode());
		responseEntity.setErrorDesc(error.getErrorDesc());
		responseEntity.setInvocationId(invocationId);
		responseEntity.setStatus(GstnApiWrapperConstants.FAILED);
		apiRespRepo.save(responseEntity);
		callSuccessFailureHandler(invocationId, requestId, error,
				GstnApiWrapperConstants.FAILED);
	}*/
	}

}
