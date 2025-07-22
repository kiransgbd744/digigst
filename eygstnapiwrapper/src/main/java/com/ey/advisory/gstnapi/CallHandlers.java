/**
 * 
 */
package com.ey.advisory.gstnapi;

import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

/**
 * @author Khalid1.Khan
 *
 */
public interface CallHandlers {
	public Pair<ExecResult<Boolean>, Map<String, Object>> callFailureHandler(
			Long requestId, APIInvocationError error, String failureHandler,
			String apiParams, Map<String, Object> retryCtxMap);
	
	public Pair<ExecResult<Boolean>, Map<String, Object>> callSuccessHandler(
			String apiParams, List<Long> successIds, Long requestId,
			String successHandler, Map<String, Object> retryCtxMap);
}
