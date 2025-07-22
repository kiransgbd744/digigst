/**
 * 
 */
package com.ey.advisory.gstnapi;

import java.util.Map;

import org.javatuples.Pair;

import com.ey.advisory.gstnapi.domain.client.APIInvocationReqEntity;

/**
 * @author Khalid1.Khan
 *
 */
public interface LoadInvocationRequest {
	public Pair<ExecResult<APIInvocationReqEntity>, Map<String, Object>>
	loadInvocationRequest(
			Long id, Map<String, Object> retryCtxMap);
}
