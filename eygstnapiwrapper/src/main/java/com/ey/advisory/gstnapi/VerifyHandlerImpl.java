/**
 * 
 */
package com.ey.advisory.gstnapi;

import java.util.Map;

import org.javatuples.Pair;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.StaticContextHolder;

/**
 * @author Khalid1.Khan
 *
 */
@Component("VerifyHandlerImpl")
public class VerifyHandlerImpl implements VerifyHandler{

	public Pair<ExecResult<Boolean>, Map<String, Object>> verifyHandler(
			String successHandler, String failureHandler,
			Map<String, Object> retryCtxMap) {
		ExecResult<Boolean> execResult = null;
		try {
			if ((StaticContextHolder.containsBean(successHandler))
					&& (StaticContextHolder.containsBean(failureHandler))) {
				execResult = ExecResult.successResult(true, "valid handler");
			} else {
				execResult = ExecResult.errorResult(
						ExecErrorCodes.HANDLER_VERIFICATION_FAILURE, "handler");
			}

		} catch (Exception ex) {
			execResult = ExecResult.errorResult(
					ExecErrorCodes.HANDLER_VERIFICATION_FAILURE,
					"Invalid handler", ex);
		} finally {
			retryCtxMap.put(RetryMapKeysConstants.HANDLER_VERIFICATION,
					execResult);
			
		}
		return new Pair<>(execResult, retryCtxMap);

	}

}
