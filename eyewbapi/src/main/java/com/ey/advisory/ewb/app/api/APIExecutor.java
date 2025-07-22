package com.ey.advisory.ewb.app.api;

public interface APIExecutor {
	
	public APIResponse execute(APIParams params, String reqData);
	
}
