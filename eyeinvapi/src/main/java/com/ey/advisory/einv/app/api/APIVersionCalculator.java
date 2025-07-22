package com.ey.advisory.einv.app.api;

import java.util.Map;

public interface APIVersionCalculator {
	
	public String getAPIVersion(APIParams params,
			APIConfig config, APIExecParties parties,
			Map<String, Object> context);
}
