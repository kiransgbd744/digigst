/**
 * 
 */
package com.ey.advisory.gstnapi;

import java.util.Map;

/**
 * @author Khalid1.Khan
 *
 */
public interface PolicyManager {
	
	 public RetryAction evaluteRetryPolicy(
				String retryBlockName,
				RetryInfo retryInfo,
				Map<String, Object> retryCtxMap,
				ExecResult<Boolean> execResult,
				ExecResult<Boolean> handlerResult);

}
