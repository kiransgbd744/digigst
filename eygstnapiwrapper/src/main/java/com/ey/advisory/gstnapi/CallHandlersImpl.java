/**
 * 
 */
package com.ey.advisory.gstnapi;

import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.StaticContextHolder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Slf4j
@Component("CallHandlersImpl")
public class CallHandlersImpl implements CallHandlers {

	@Autowired
	private APIExecEventsLogger logger;

	public Pair<ExecResult<Boolean>, Map<String, Object>> callFailureHandler(
			Long requestId, APIInvocationError error, String failureHandler,
			String apiParams, Map<String, Object> retryCtxMap) {
		ExecResult<Boolean> execResult = null;
		try {
			logger.logInfo(requestId, "calling the Failure Handler");
			JsonObject jobj = new Gson().fromJson(apiParams, JsonObject.class);
			FailureResult result = new FailureResult(requestId, error,
					jobj.get("ctxParams").toString());

			StaticContextHolder
					.getBean("DefaultFailureHandler",
							FailureHandlerWrapper.class)
					.handleFailure(failureHandler, result, apiParams);

			execResult = ExecResult.successResult(true,
					"Failure Handler Called Successfully");
		} catch (Exception ex) {
			logger.logInfo(requestId,
					String.format(
							"calling the Failure Handler, exception is %s ",
							ex.getMessage()));
			LOGGER.error("Exception while calling the Failure Handler", ex);
			execResult = ExecResult.errorResult(
					ExecErrorCodes.FAILURE_HNDLR_INVOC_FAILURE,
					"Error In Failure Handler", ex);
		} finally {
			retryCtxMap.put(RetryMapKeysConstants.FAILURE_HANDLER_CALL,
					execResult);

		}
		return new Pair<>(execResult, retryCtxMap);

	}

	public Pair<ExecResult<Boolean>, Map<String, Object>> callSuccessHandler(
			String apiParams, List<Long> successIds, Long requestId,
			String successHandler, Map<String, Object> retryCtxMap) {
		ExecResult<Boolean> execResult = null;
		try {
			JsonObject jobj = new Gson().fromJson(apiParams, JsonObject.class);
			SuccessResult result = new SuccessResult(requestId, successIds,
					jobj.get("ctxParams").toString());
			StaticContextHolder
					.getBean("DefaultSuccessHandler",
							SuccessHandlerWrapper.class)
					.handleSuccess(successHandler, result, apiParams);
			execResult = ExecResult.successResult(true,
					"Handler Called Successfully");
		} catch (Exception ex) {
			logger.logInfo(requestId,
					String.format(
							"calling the Success Handler, exception is %s ",
							ex.getMessage()));
			LOGGER.error("Exception while calling the Success Handler", ex);
			execResult = ExecResult.errorResult(
					ExecErrorCodes.SUCCESS_HNDLR_INVOC_FAILURE,
					"Exception occured in handler", ex);
		} finally {
			retryCtxMap.put(RetryMapKeysConstants.SUCCESS_HANDLER_CALL,
					execResult);
		}
		return new Pair<>(execResult, retryCtxMap);

	}
}
