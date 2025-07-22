/**
 * 
 */
package com.ey.advisory.gstnapi;

import java.util.Map;

import org.javatuples.Pair;

/**
 * @author Khalid1.Khan
 *
 */
public interface VerifyHandler {
	public Pair<ExecResult<Boolean>, Map<String, Object>> verifyHandler(
			String successHandler, String failureHandler,
			Map<String, Object> retryCtxMap);
}
