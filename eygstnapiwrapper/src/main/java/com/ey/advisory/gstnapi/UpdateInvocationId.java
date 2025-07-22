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
public interface UpdateInvocationId {
	public Pair<ExecResult<Boolean>, Map<String, Object>> updateINvocationId(
			Long invocationId,Long requestId, Map<String, Object> retryCtxMap);

}
