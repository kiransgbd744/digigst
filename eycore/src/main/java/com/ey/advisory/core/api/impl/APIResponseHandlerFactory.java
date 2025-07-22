package com.ey.advisory.core.api.impl;

import java.util.Map;

import com.ey.advisory.core.api.APIParams;

public interface APIResponseHandlerFactory {
	
	public APIResponseHandler getHandler(
			APIParams params, 
			APIConfig config, 
			APIExecParties parties,
			APIReqParts reqParts,
			APIAuthInfo authInfo,
			String response,
			Map<String, Object> context);

}
