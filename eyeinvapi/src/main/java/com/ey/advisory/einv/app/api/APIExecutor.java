package com.ey.advisory.einv.app.api;

public interface APIExecutor {
	
	public APIResponse execute(APIParams params, String reqData);
	
}
