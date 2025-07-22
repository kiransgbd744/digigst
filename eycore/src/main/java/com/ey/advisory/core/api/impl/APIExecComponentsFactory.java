package com.ey.advisory.core.api.impl;

public interface APIExecComponentsFactory {

	public abstract APIVersionCalculator getVersionCalculator();
	
	public abstract APIAuthInfoLoader getAuthInfoLoader();
	
	public abstract APIReqHeaderBuilder getReqHearderBuilder();
	
	public abstract APIReqQueryParamsBuilder getReqQueryParamsBuilder();
	
	public abstract APIReqPartsEncryptor getReqPartsEncryptor();
	
	public abstract ProviderAPIExecutor getapiExecutor();
	
	public abstract APIResponseHandlerFactory getApiResponseHandlerFacctory();
	
	public abstract APIConfigBuilder getApiConfigBuilder();
	
	public abstract APIExecPartiesBuilder getApiExecPartiesBuilder();
	
	
	
	
}
