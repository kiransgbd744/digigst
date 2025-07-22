/**
 * 
 */
package com.ey.advisory.gstnapi;

import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import com.ey.advisory.core.api.APIParams;

/**
 * @author Khalid1.Khan
 *
 */
public interface UrlListProcessor {
	public Pair<ExecResult<Boolean>, Map<String, Object>>
	processTasksForUrls(List<String> urls, APIParams apiParams,
			String rek, String sk, Long requestId,
			Map<String, Object> retryCtxMap);
}
