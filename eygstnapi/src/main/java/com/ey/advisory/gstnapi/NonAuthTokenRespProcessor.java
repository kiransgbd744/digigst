package com.ey.advisory.gstnapi;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.impl.APIAuthInfo;
import com.ey.advisory.core.api.impl.APIConfig;
import com.ey.advisory.core.api.impl.APIExecParties;
import com.ey.advisory.core.api.impl.APIResponseProcessor;

@Component("NonAuthTokenRespProcessor")
public class NonAuthTokenRespProcessor implements APIResponseProcessor {

	@Override
	public APIResponse processResponse(
				APIParams params, 
				APIConfig config, 
				APIExecParties parties, 
				APIAuthInfo authInfo,
				APIResponse response, 
				Map<String, Object> context) {
		
		// Return the input response. There is no need to post process 
		// a Non-Auth Token Request.
		return response;
	}

}
