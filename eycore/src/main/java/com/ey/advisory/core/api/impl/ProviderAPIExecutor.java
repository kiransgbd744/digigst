package com.ey.advisory.core.api.impl;

import java.util.Map;

import com.ey.advisory.core.api.APIParams;

public interface ProviderAPIExecutor {
	
	public String execute(
				APIParams params, 
				APIConfig config, 
				APIExecParties parties,
				APIReqParts reqParts,
				APIAuthInfo authInfo,
				Map<String, Object> context);

}
