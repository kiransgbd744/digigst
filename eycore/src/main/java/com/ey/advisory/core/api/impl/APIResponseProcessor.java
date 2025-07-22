package com.ey.advisory.core.api.impl;

import java.util.Map;

import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIResponse;

public interface APIResponseProcessor {
	
	public APIResponse processResponse(
			APIParams params,
			APIConfig config, 
			APIExecParties parties,
			APIAuthInfo authInfo,
			APIResponse response,
			Map<String, Object> context);

}
