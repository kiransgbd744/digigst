package com.ey.advisory.einv.app.api;

import java.util.Map;

public interface NICAPIExecutor {
	
	public String execute(APIParams params, 
				String version,	APIConfig config, APIExecParties parties,
				Map<String, Object> context, APIReqParts reqParts);

}
