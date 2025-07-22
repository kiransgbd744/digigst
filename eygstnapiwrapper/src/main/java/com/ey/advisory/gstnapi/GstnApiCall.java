/**
 * 
 */
package com.ey.advisory.gstnapi;

import java.util.Map;

import org.javatuples.Pair;

import com.ey.advisory.core.api.APIResponse;

/**
 * @author Khalid1.Khan
 *
 */
public interface GstnApiCall {
	public Pair<ExecResult<APIResponse>, Map<String, Object>> callGstnApi(
			String apiParams, Map<String, Object> retryCtxMap);
}
