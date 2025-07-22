package com.ey.advisory.ewb.app.api;

import java.util.Map;

public interface APIResponseDecryptor {
	public APIResponse decrypt(
			APIParams params, String version, APIConfig config, 
			APIExecParties parties, Map<String,
			Object> context, String respJson);
}
