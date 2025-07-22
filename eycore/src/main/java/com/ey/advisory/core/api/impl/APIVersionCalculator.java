package com.ey.advisory.core.api.impl;

import java.util.Map;

import com.ey.advisory.core.api.APIParams;

public interface APIVersionCalculator {
	
	public String getAPIVersion(APIParams params,
					APIConfig config, APIExecParties parties,
					Map<String, Object> context);
}
