package com.ey.advisory.einv.app.api;

import java.util.HashMap;
import java.util.Map;

import com.ey.advisory.common.KeyValuePair;
import com.ey.advisory.core.api.APIProviderEnum;

public class APIParams {

	
	/**
	 * This is a unique code given for each API to be executed. Note that it
	 * might not be the 'ACTION' parameter mentioned in the API specifications.
	 * This is because, the same 'ACTION' parameter can be reused for different
	 * APIs, but this action variable represents a unique string for an API.
	 * A separate constant file will be maintained that contains all possible
	 * actions for the API.
	 */
	protected String apiAction;
	
	/**
	 * This variable represents the version of the API to be executed. There 
	 * might be scenarios where more than one version of the same API can 
	 * exist in production (due to various business reasons). One example 
	 * would be to use a higher version of the API for all tax periods 
	 * after a certain month and the previous version of the API for all
	 * prior tax periods. The version of the api is determined by a combination
	 * of configuration values and the input data provided by the caller. 
	 */
	protected String apiVersion;
	
	
	/**
	 * Currently, there are 2 supported providers - GSTN and NIC.
	 */
	protected APIProviderEnum apiProvider;
	
	protected Map<String, String> apiParamsMap = new HashMap<String, String>();
	
	@SafeVarargs
	public APIParams(APIProviderEnum apiProvider, String apiIdentifier, 
			KeyValuePair<String, String>...keyValuePairs) {
		this.apiAction = apiIdentifier;
		this.apiProvider = apiProvider;
		// Add all the key value pairs passed, to the api params map.
		// These key/value pairs will be used to set the API parameters.
		for(KeyValuePair<String, String> keyValuePair: keyValuePairs) {
			apiParamsMap.put(keyValuePair.getKey(), keyValuePair.getValue());
		}
	}
	
	@SafeVarargs
	public APIParams(APIProviderEnum apiProvider, 
			String apiAction, String apiVersion, 
			KeyValuePair<String, String>...keyValuePairs) {
		this.apiAction = apiAction;
		this.apiProvider = apiProvider;
		this.apiVersion = (apiVersion != null) ? apiVersion.trim() : null;
		for(KeyValuePair<String, String> keyValuePair: keyValuePairs) {
			apiParamsMap.put(keyValuePair.getKey(), keyValuePair.getValue());
		}
	}
	
	public String getApiAction() {
		return apiAction;
	}
	
	public  boolean isVersionSpecified() {
		return (apiVersion != null) && !apiVersion.isEmpty();
	}
	
	public String getApiVersion() {
		return apiVersion;
	}

	public String getApiParamValue(String paramName) {
		return apiParamsMap.get(paramName);
	}

	public APIProviderEnum getApiProvider() {
		return apiProvider;
	}

	public void setApiProvider(APIProviderEnum apiProvider) {
		this.apiProvider = apiProvider;
	}
	
	
	
}

