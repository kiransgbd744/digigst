package com.ey.advisory.ewb.app.api;

import java.util.Map;

public interface APIResponseProcessor {
	
	public APIResponse processResponse(APIParams params,
			String version, APIConfig config, 
			APIExecParties parties,
			Map<String, Object> context, APIResponse response);

}
