package com.ey.advisory.core.api.impl;

import java.util.Map;

import com.ey.advisory.core.api.APIParams;

public interface APIReqQueryParamsBuilder {
	public Map<String, String> buildQueryParamsMap(
				APIParams params, 
				APIConfig config, 
				APIExecParties parties, 
				APIAuthInfo authInfo,
				Map<String, Object> context);
}
