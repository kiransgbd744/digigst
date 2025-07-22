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
public interface RetryBlock {

	public Pair<Pair<ExecResult<Boolean>, ExecResult<Boolean>>,
		Map<String, Object>> retry(String params);

}
