package com.ey.advisory.core.api.impl;

import java.util.Map;

import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIResponse;

public interface APIResponseHandler {
	
	public APIResponse handleResponse(
			APIParams params, 
			APIConfig config, 
			APIExecParties parties,
			APIReqParts reqParts,
			APIAuthInfo authInfo,
			String response,
			Map<String, Object> context);

}
