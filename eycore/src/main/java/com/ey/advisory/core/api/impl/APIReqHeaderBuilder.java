package com.ey.advisory.core.api.impl;

import java.util.Map;

import com.ey.advisory.core.api.APIParams;

public interface APIReqHeaderBuilder {
	
	public Map<String, String> buildReqHeaderMap(
			APIParams params, APIConfig config, APIExecParties parties,
			APIAuthInfo authInfo, Map<String, Object> context);
}
