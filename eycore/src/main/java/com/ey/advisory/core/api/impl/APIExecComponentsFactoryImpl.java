package com.ey.advisory.core.api.impl;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.StaticContextHolder;

@Component
public class APIExecComponentsFactoryImpl implements APIExecComponentsFactory {

	@Override
	public APIVersionCalculator getVersionCalculator() {
		return (APIVersionCalculator)
				StaticContextHolder.getBean("APIVersionCalculatorImpl");
	}

	@Override
	public APIAuthInfoLoader getAuthInfoLoader() {
		return StaticContextHolder.getBean(APIAuthInfoLoader.class);
	}

	@Override
	public APIReqHeaderBuilder getReqHearderBuilder() {
		return (APIReqHeaderBuilder) StaticContextHolder.getBean(
				"APIReqHeaderBuilderImpl");
	}

	@Override
	public APIReqQueryParamsBuilder getReqQueryParamsBuilder() {
		return (APIReqQueryParamsBuilder) StaticContextHolder.getBean(
				"APIReqQueryParamsBuilderImpl");
	}

	@Override
	public APIReqPartsEncryptor getReqPartsEncryptor() {
		return (APIReqPartsEncryptor) StaticContextHolder.getBean(
				"APIReqPartsEncryptorImpl");
	}

	@Override
	public ProviderAPIExecutor getapiExecutor() {
		return (ProviderAPIExecutor) StaticContextHolder.getBean(
				"GSTNAPIApacheClientExecutorImpl");
	}

	@Override
	public APIResponseHandlerFactory getApiResponseHandlerFacctory() {
		return StaticContextHolder.getBean(APIResponseHandlerFactory.class);
	}

	@Override
	public APIConfigBuilder getApiConfigBuilder() {
		return (APIConfigBuilder) StaticContextHolder.getBean(
				"APIConfigBuilderImpl");
	}

	@Override
	public APIExecPartiesBuilder getApiExecPartiesBuilder() {
		return (APIExecPartiesBuilder) StaticContextHolder.getBean(
				"APIExecPartiesBuilderImpl");
	}

}
