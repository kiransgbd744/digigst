package com.ey.advisory.einv.app.api;

import java.util.Map;

public interface APIResponseDecryptor {
	public APIResponse decrypt(
			APIParams params, String version, APIConfig config, 
			APIExecParties parties, Map<String,
			Object> context, String respJson);
}
