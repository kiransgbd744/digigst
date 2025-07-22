/**
 * 
 */
package com.ey.advisory.gstnapi;

import java.util.Map;

import org.javatuples.Pair;

import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.gstnapi.domain.client.APIResponseEntity;

/**
 * @author Khalid1.Khan
 *
 */
public interface SaveGstnCallResponse {
	public Pair<ExecResult<APIResponseEntity>, Map<String, Object>> saveResponse(
			APIResponse apiresp,Long invocationId,
			Map<String, Object> retryCtxMap);
}
