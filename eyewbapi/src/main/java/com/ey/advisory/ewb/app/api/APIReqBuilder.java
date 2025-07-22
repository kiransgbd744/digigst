package com.ey.advisory.ewb.app.api;

import java.util.Map;

/**
 * 
 * 
 * @author Sai.Pakanati
 *
 */
public interface APIReqBuilder {
	
	public Map<String, String> buildReqHeaderMap(
			APIParams params, String version,
			APIConfig config, APIExecParties parties,
			Map<String, Object> context);
	
	public Map<String, String> buildQueryParamsMap(
			APIParams params, String version,
			APIConfig config, APIExecParties parties,
			Map<String, Object> context);
}
