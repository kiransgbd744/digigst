package com.ey.advisory.core.api.impl;

import java.util.Map;

import com.ey.advisory.core.api.APIParams;

public interface APIAuthInfoLoader {
	
	/**
	 * Load any Authentication/Authorization object already associated with the
	 * API. If the API invoked is Authentication API itself, then the return
	 * value can be null. For other APIs, there should be an AuthInfo associated
	 * with it. The implementation of this method also should check the validity
	 * of the AuthInfo (like the expiry of auth tokens etc), if possible.
	 * 
	 * @param params the API Params.
	 * @param version the version of the API.
	 * @param config the API configuration that's already loaded.
	 * @param parties the parties involved in executing the API
	 * @param context the context information that flows through down the 
	 * 	chain of execution steps.
	 * 
	 * @return any authentication/authorization information about the 
	 * 	APIEndUser.
	 */
	public APIAuthInfo loadApiAuthInfo(APIParams params,
			APIConfig config, APIExecParties parties,
			Map<String, Object> context);
}
