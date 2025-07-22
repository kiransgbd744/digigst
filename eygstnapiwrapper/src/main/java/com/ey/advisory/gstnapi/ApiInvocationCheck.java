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
public interface ApiInvocationCheck {
	public Pair<ExecResult<Long>, Map<String, Object>> IsAlreadyInvoked(
			APIInvocationReqEntity requestEntity, Map<String, Object> retryCtxMap);
}
