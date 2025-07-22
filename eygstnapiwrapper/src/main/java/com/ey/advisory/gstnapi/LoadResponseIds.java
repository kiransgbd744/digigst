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
public interface LoadResponseIds {
	public Pair<ExecResult<List<Long>>, Map<String, Object>> loadInvocationReponseIds(
			Long invocationId, Map<String, Object> retryCtxMap);
}
