package com.ey.advisory.core.api.impl;

import java.util.Map;

import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIResponse;

public interface APIResponseDecryptor {
	public APIResponse decrypt(
			APIParams params, 
			APIConfig config, 
			APIExecParties parties,
			APIAuthInfo authInfo,
			String respJson,
			Map<String, Object> context);
}
