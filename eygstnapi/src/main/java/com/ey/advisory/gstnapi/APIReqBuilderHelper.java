package com.ey.advisory.gstnapi;

import java.util.Map;

import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.impl.APIAuthInfo;
import com.ey.advisory.core.api.impl.APIConfig;
import com.ey.advisory.core.api.impl.APIExecParties;

public interface APIReqBuilderHelper {
	
	public Map<String, String> setAPIHeaders(APIParams params,
					APIConfig config, 
					APIExecParties parties,
					APIAuthInfo authInfo, 
					Map<String, Object> context);
}
