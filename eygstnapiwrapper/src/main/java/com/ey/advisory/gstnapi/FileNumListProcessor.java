/**
 * 
 */
package com.ey.advisory.gstnapi;

import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import com.ey.advisory.core.api.APIParams;

/**
 * @author Sai K Pakanati
 *
 */
public interface FileNumListProcessor {
	public Pair<ExecResult<Boolean>, Map<String, Object>>
	processTasksForUrls(List<String> fileNumList, APIParams apiParams, Long requestId,
			Map<String, Object> retryCtxMap);
}
