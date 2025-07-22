package com.ey.advisory.core.api.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import com.google.common.collect.ImmutableList;

public class APIVersionConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String baseUrl;
	protected String urlSuffix;
	protected String httpMethod;
	
	private List<APIHttpReqParamConfig> expectedHeaders = 
			new ArrayList<>();
	private List<APIHttpReqParamConfig> expectedUrlParams =  
			new ArrayList<>();
	
	public APIVersionConfig(String baseUrl, String httpMethod) {
		super();
		this.baseUrl = baseUrl;
		this.httpMethod = httpMethod;
	}
	
	public APIVersionConfig addExpectedHeader(
			String name, boolean isMandatory) {
		APIHttpReqParamConfig config = 
				new APIHttpReqParamConfig(name, isMandatory);
		expectedHeaders.add(config);
		return this;
	}
	
	public APIVersionConfig addExpectedUrlParam(
			String name, boolean isMandatory) {
		APIHttpReqParamConfig config = 
				new APIHttpReqParamConfig(name, isMandatory);
		expectedUrlParams.add(config);
		return this;
	}
	
	public String getBaseUrl() {
		return baseUrl;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public String getUrlSuffix() {
		return urlSuffix;
	}

	public void setUrlSuffix(String urlSuffix) {
		this.urlSuffix = urlSuffix;
	}
	
	public List<APIHttpReqParamConfig> getExpectedHeaders() {
		return ImmutableList.copyOf(expectedHeaders);
	}

	public List<APIHttpReqParamConfig> getExpectedUrlParams() {
		return ImmutableList.copyOf(expectedUrlParams);
	}

	@Override
	public String toString() {
		return "APIVersionConfig [baseUrl=" + baseUrl + ", urlSuffix="
				+ urlSuffix + ", httpMethod=" + httpMethod
				+ ", expectedHeaders={" + StringUtils.join(expectedHeaders, ',')
				+ "}, expectedUrlParams={" 
				+ StringUtils.join(expectedUrlParams, ',') + "}]";
	}
	
}
