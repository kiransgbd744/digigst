package com.ey.advisory.ewb.app.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class APIVersionConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String baseUrl;
	protected String urlSuffix;
	protected String httpMethod;
	protected boolean isVersionIncludedInUrl;
	protected boolean isEwbUserIdReq;
	protected List<APIHttpReqParamConfig> expectedHeaders = 
			new ArrayList<>();
	protected List<APIHttpReqParamConfig> expectedUrlParams =  
			new ArrayList<>();
	
	public APIVersionConfig(String baseUrl, String urlSuffix, String httpMethod, 
			boolean isVersionIncludedInUrl,
			boolean isEwbUserIdReq) {
		super();
		this.baseUrl = baseUrl;
		this.urlSuffix = urlSuffix;
		this.httpMethod = httpMethod;
		this.isVersionIncludedInUrl = isVersionIncludedInUrl;
		this.isEwbUserIdReq = isEwbUserIdReq;
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

	public boolean isVersionIncludedInUrl() {
		return isVersionIncludedInUrl;
	}

	public boolean isEwbUserIdReq() {
		return isEwbUserIdReq;
	}

	@Override
	public String toString() {
		return "APIVersionConfig [httpMethod=" + httpMethod + ", baseUrl=" 
				+ baseUrl + ", urlSuffix=" + urlSuffix
				+ ", expectedHeaders=" + expectedHeaders 
				+ ", expectedUrlParams=" + expectedUrlParams
				+ ", isVersionIncludedInUrl=" + isVersionIncludedInUrl 
				+ ", isEwbUserIdReq=" + isEwbUserIdReq + "]";
	}
	
	
	
	
	
}
