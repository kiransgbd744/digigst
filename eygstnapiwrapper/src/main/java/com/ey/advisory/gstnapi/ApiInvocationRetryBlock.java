/**
 * 
 */
package com.ey.advisory.gstnapi;

import java.sql.Clob;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.gstnapi.domain.client.APIInvocationReqEntity;
import com.ey.advisory.gstnapi.domain.client.APIResponseEntity;
import com.ey.advisory.gstnapi.repositories.client.APIInvocationReqRepository;
import com.ey.advisory.gstnapi.repositories.client.APIResponseRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Slf4j
@Component("ApiInvocationRetryBlock")
public class ApiInvocationRetryBlock implements RetryBlock {

	@Autowired
	private LoadInvocationRequest loadInvocReq;

	@Autowired
	@Qualifier("VerifyHandlerImpl")
	private VerifyHandler verifyHandler;

	@Autowired
	@Qualifier("ApiInvocationCheckImpl")
	private ApiInvocationCheck invocationCheck;

	@Autowired
	@Qualifier("LoadResponseIdsImpl")
	private LoadResponseIds loadResponseIds;

	@Autowired
	@Qualifier("CallHandlersImpl")
	private CallHandlers callHandlers;

	@Autowired
	@Qualifier("GstnAPICallImpl")
	private GstnApiCall gstnApiCall;

	@Autowired
	@Qualifier("SaveGstnCallResponseImpl")
	private SaveGstnCallResponse saveGstnResponse;

	@Autowired
	@Qualifier("SaveFailedResponseImpl")
	private SaveFailedResponse saveFailedResponse;

	@Autowired
	private APIInvocationReqRepository reqRepo;

	@Autowired
	private APIExecEventsLogger logger;

	@Autowired
	@Qualifier("APIResponseRepository")
	private APIResponseRepository respRepo;

	@Override
	public Pair<Pair<ExecResult<Boolean>, ExecResult<Boolean>>, Map<String, Object>> retry(
			String params) {
		Map<String, Object> retryCtxMap = new HashMap<>();
		APIInvocationReqEntity requestEntity = null;
		Long reqId = getRequestId(params);
		logger.logInfo(reqId, "requestid extracted from the json ,now loading"
				+ " the request entity");
		ExecResult<APIInvocationReqEntity> invocReqResult = loadInvocReq
				.loadInvocationRequest(reqId, retryCtxMap).getValue0();
		if (invocReqResult.isSuccess()) {
			requestEntity = invocReqResult.getResult();
			logger.logInfo(reqId, "requestEntity retrieved from db for the "
					+ "corresponding reqId + " + requestEntity);
			return executeIfRequestLoaded(reqId, requestEntity, retryCtxMap);
		} else {
			logger.logError(reqId,
					"Failed to retrieve requestEntity from db "
							+ "for the corresponding reqId",
					invocReqResult.getExecErrCode());
			return new Pair<>(new Pair<>(invocReqResult.toBoolResult(),
					invocReqResult.toBoolResult()), retryCtxMap);
		}

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

	private ExecResult<Boolean> callFailureHandler(Long requestId,
			APIInvocationError error, String failureHandler, String apiParams,
			Map<String, Object> retryCtxMap) {
		return callHandlers.callFailureHandler(requestId, error, failureHandler,
				apiParams, retryCtxMap).getValue0();

	}

	private <T> APIInvocationError createApiInvocationError(
			ExecResult<T> execResult) {
		return APIInvokerUtil.createErrorObject(execResult.getErrMsg(),
				execResult.getExecErrCode(), execResult.getExtErrCode(),
				execResult.getException());

	}

	private Pair<Pair<ExecResult<Boolean>, ExecResult<Boolean>>, Map<String, Object>> executeIfAlreadyInvoked(
			Long reqId, ExecResult<Long> invocationCheckResult,
			APIInvocationReqEntity requestEntity,
			Map<String, Object> retryCtxMap) {
		Long refReqId = invocationCheckResult.getResult();
		String msg = String.format(
				"There is already a successful "
						+ "invocation with the same api params having "
						+ "id : {} ,hence the response corresponding to "
						+ "the that invocation will be returned as a result",
				refReqId);
		logger.logInfo(reqId, msg);
		msg = String.format("updating the refReqId  %d "
				+ "corresponding to the request Id", refReqId);
		logger.logInfo(reqId, msg);
		reqRepo.updateRefReqId(refReqId, requestEntity.getId());
		msg = String.format(
				"loading responseIds corresponding to " + "the refReqIds %d ",
				refReqId);
		logger.logInfo(reqId, msg);
		ExecResult<List<Long>> loadResponseidsResult = loadResponseIds
				.loadInvocationReponseIds(refReqId, retryCtxMap).getValue0();
		if (loadResponseidsResult.isSuccess()) {

			List<Long> responseIds = loadResponseidsResult.getResult();
			logger.logInfo(reqId, "responseIds successfully Loaded ,"
					+ "having size " + responseIds.size());
			logger.logInfo(reqId, "calling successhandler "
					+ requestEntity.getSuccessHandler());
			ExecResult<Boolean> callHandlerExecResult = callSuccessHandler(
					requestEntity, responseIds, retryCtxMap);
			return new Pair<>(new Pair<>(loadResponseidsResult.toBoolResult(),
					callHandlerExecResult), retryCtxMap);

		}

		APIInvocationError error = createApiInvocationError(
				loadResponseidsResult);
		ExecResult<Boolean> handlerExecResp = callFailureHandler(
				requestEntity.getId(), error, requestEntity.getFailureHandler(),
				requestEntity.getApiParams(), retryCtxMap);
		return new Pair<>(new Pair<>(loadResponseidsResult.toBoolResult(),
				handlerExecResp), retryCtxMap);

	}

	Pair<Pair<ExecResult<Boolean>, ExecResult<Boolean>>, Map<String, Object>> executeIfNotInvoked(
			Long requestId, APIInvocationReqEntity requestEntity,
			Map<String, Object> retryCtxMap) {
		logger.logInfo(requestId,
				"There is no successfull response "
						+ "corresponding to the given api params exist,"
						+ "hence calling the gstn api to get the response");

		ExecResult<APIResponse> apiResponseExecResult = gstnApiCall
				.callGstnApi(requestEntity.getApiParams(), retryCtxMap)
				.getValue0();
		if (apiResponseExecResult.isSuccess()) {
			logger.logInfo(requestId,
					"the gstn call is successfull now checking if the response"
							+ " is a token response");
			JsonObject respjson = (new JsonParser())
					.parse(apiResponseExecResult.getResult().getResponse())
					.getAsJsonObject();
			if (respjson.has("token") && respjson.has("est")) {
				logger.logInfo(requestId, "Gstn has returned a token response");
				ExecResult<Boolean> callHandlerExecResult = ExecResult
						.successResult(true, "Handler Called Successfully");
				retryCtxMap.put("token", respjson.get("token").getAsString());
				retryCtxMap.put("est", respjson.get("est").getAsInt());
				retryCtxMap.put("tokenParams", respjson);
				return new Pair<>(
						new Pair<>(apiResponseExecResult.toBoolResult(),
								callHandlerExecResult),
						retryCtxMap);
			}

			if (respjson.has("data") && respjson.get("data").isJsonObject()
					&& respjson.get("data").getAsJsonObject().has("fc")) {
				logger.logInfo(requestId,
						"Gstn has returned a new token response");
				ExecResult<Boolean> callHandlerExecResult = ExecResult
						.successResult(true, "Handler Called Successfully");
				retryCtxMap.put("fileCount", extractFileCount(respjson));
				saveResponse(apiResponseExecResult.getResult().getResponse(),
						requestId);
				updateGstr2bParams(requestEntity, retryCtxMap, respjson);
				
				return new Pair<>(
						new Pair<>(apiResponseExecResult.toBoolResult(),
								callHandlerExecResult),
						retryCtxMap);
			}

			if (respjson.has("rc") && respjson.has("fc")) {
				logger.logInfo(requestId,
						"Gstn has returned a new token response");
				ExecResult<Boolean> callHandlerExecResult = ExecResult
						.successResult(true, "Handler Called Successfully");
				retryCtxMap.put("fileCount", extractFileCount8A(respjson));
				return new Pair<>(
						new Pair<>(apiResponseExecResult.toBoolResult(),
								callHandlerExecResult),
						retryCtxMap);
			}
			logger.logInfo(requestId, "Gstn has returned successfull "
					+ "response ,saving the response in db");
			APIResponseEntity responseEntity = saveGstnResponse
					.saveResponse(apiResponseExecResult.getResult(),
							requestEntity.getId(), retryCtxMap)
					.getValue0().getResult();
			logger.logInfo(requestId, "Gstn has returned successfull "
					+ "response , response saved, calling" + "success handler");
			ExecResult<Boolean> callHandlerExecResult = callSuccessHandler(
					requestEntity, Arrays.asList(responseEntity.getId()),
					retryCtxMap);
			return new Pair<>(new Pair<>(apiResponseExecResult.toBoolResult(),
					callHandlerExecResult), retryCtxMap);
		}
		logger.logError(requestId, apiResponseExecResult.getErrMsg(),
				apiResponseExecResult.getExtErrCode() == null
						? apiResponseExecResult.getExecErrCode()
						: apiResponseExecResult.getExtErrCode());
		APIInvocationError error = createApiInvocationError(
				apiResponseExecResult);
		ExecResult<Boolean> handlerExecResp = callFailureHandler(
				requestEntity.getId(), error, requestEntity.getFailureHandler(),
				requestEntity.getApiParams(), retryCtxMap);
		return new Pair<>(new Pair<>(apiResponseExecResult.toBoolResult(),
				handlerExecResp), retryCtxMap);
	}

	private Pair<Pair<ExecResult<Boolean>, ExecResult<Boolean>>, Map<String, Object>> executeIfRequestLoaded(
			Long reqId, APIInvocationReqEntity requestEntity,
			Map<String, Object> retryCtxMap) {
		logger.logInfo(reqId, "verifying success handler and failure handler "
				+ "to ensure the caller has provided a valid handler");
		ExecResult<Boolean> verifyHandlerResult = verifyHandler
				.verifyHandler(requestEntity.getSuccessHandler(),
						requestEntity.getFailureHandler(), retryCtxMap)
				.getValue0();
		if (verifyHandlerResult.isSuccess()) {
			logger.logInfo(reqId, "Caller has provided valid handlers");
			logger.logInfo(reqId,
					"Now checking if there is any previous"
							+ " invocation with the same API params with success "
							+ "result");
			ExecResult<Long> invocationCheckResult = invocationCheck
					.IsAlreadyInvoked(requestEntity, retryCtxMap).getValue0();
			if (invocationCheckResult.isSuccess()) {
				return executeIfAlreadyInvoked(reqId, invocationCheckResult,
						requestEntity, retryCtxMap);

			} else {
				return executeIfNotInvoked(reqId, requestEntity, retryCtxMap);
			}

		}
		logger.logError(reqId, "Invalid Handler Provided",
				verifyHandlerResult.getExecErrCode());
		return new Pair<>(new Pair<>(verifyHandlerResult.toBoolResult(),
				verifyHandlerResult.toBoolResult()), retryCtxMap);

	}

	/**
	 * If any exception occur in the below block it will be catched by
	 * APIInvocationProcessor class and the exception will be logged in since we
	 * don't have requestId we can only log the exception message without the
	 * requestId
	 */
	private Long getRequestId(String params) {
		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(params).getAsJsonObject();
		return json.get("requestId").getAsLong();

	}

	private int extractFileCount(JsonObject json) {
		return json.get("data").getAsJsonObject().get("fc").getAsInt();
	}

	private int extractFileCount8A(JsonObject json) {
		return json.get("fc").getAsInt();
	}

	private void saveResponse(String decryptedResp, Long requestId) {
		try {
			Clob responseClob = new javax.sql.rowset.serial.SerialClob(
					decryptedResp.toCharArray());
			APIResponseEntity responseEntity = new APIResponseEntity(requestId,
					responseClob, GstnApiWrapperConstants.SUCCESS,
					GstnApiWrapperConstants.SYSTEM, LocalDateTime.now(),
					GstnApiWrapperConstants.SYSTEM, LocalDateTime.now());
			respRepo.save(responseEntity);
		} catch (Exception e) {
			String msg = "Exception while persisting File Response Data";
			LOGGER.error(msg, e);
		}

	}
	

	private void updateGstr2bParams(APIInvocationReqEntity requestEntity,
	        Map<String, Object> retryCtxMap, JsonObject respjson) {
		String apiParams = requestEntity.getApiParams();
		JsonObject jsonObject = new JsonParser().parseString(apiParams)
		        .getAsJsonObject();
		JsonObject ctxObject = jsonObject.get("ctxParams").getAsJsonObject();
		String prevGenDt = ctxObject.has("prevGenDt")
		        ? ctxObject.get("prevGenDt").isJsonNull() ? null
		                : ctxObject.get("prevGenDt").getAsString()
		        : null;
		String prevCheckSum = ctxObject.has("prevCheckSum")
		        ? ctxObject.get("prevCheckSum").isJsonNull() ? null
		                : ctxObject.get("prevCheckSum").getAsString()
		        : null;
		String checkSum = respjson.has("chksum")
		        ? (respjson.get("chksum").isJsonNull() ? null
		                : respjson.get("chksum").getAsString())
		        : null;
		String genDt = respjson.has("data")
		        && respjson.getAsJsonObject("data")
		                .has("gendt")
		                        ? (respjson.getAsJsonObject("data")
		                                .get("gendt").isJsonNull()
		                                        ? null
		                                        : respjson
		                                                .getAsJsonObject("data")
		                                                .get("gendt")
		                                                .getAsString())
		                        : null;
		boolean isGenDtEqual = false;
		if (prevGenDt != null && genDt != null) {
			try {
				DateTimeFormatter formatter = DateTimeFormatter
				        .ofPattern("dd-MM-yyyy");
				LocalDate prevDate = LocalDate.parse(prevGenDt, formatter);
				LocalDate currentDate = LocalDate.parse(genDt, formatter);
				isGenDtEqual = prevDate.equals(currentDate);
			} catch (DateTimeParseException e) {
				LOGGER.error(" Incorrect gen date format ", e);
				isGenDtEqual = false;
			}
		}

		boolean isCheckSumEqual = (prevCheckSum != null
		        && prevCheckSum.equals(checkSum));
		if (isCheckSumEqual && isGenDtEqual) {
			retryCtxMap.put("isDataChanged", true);
			if (ctxObject.has("isDataChanged")) {
				boolean isConditionMet = true;
				ctxObject.addProperty("isDataChanged", isConditionMet);
				ctxObject.addProperty("isTokenResp", isConditionMet);
			}
		} else {
			retryCtxMap.put("isDataChanged", false);

		}
	}

}
