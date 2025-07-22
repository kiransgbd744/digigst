package com.ey.advisory.gstnapi;

import java.util.Map;

/**
 * @author Khalid1.Khan
 *
 */
public interface FailureBlock {
	
	public boolean failureTask(
			ExecResult<Boolean> execResult,
			ExecResult<Boolean> handlerResult,
			Map<String, Object> map
		);

}
