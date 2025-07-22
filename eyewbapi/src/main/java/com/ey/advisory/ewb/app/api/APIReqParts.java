package com.ey.advisory.ewb.app.api;

import java.util.Map;

public class APIReqParts {
	
	protected Map<String, String> headers;
	protected Map<String, String> queryParams;
	protected String reqData;
	
	public APIReqParts(Map<String, String> headers, 
			Map<String, String> queryParams, String reqData) {
		super();
		this.headers = headers;
		this.queryParams = queryParams;
		this.reqData = reqData;
	}
	public Map<String, String> getHeaders() {
		return headers;
	}
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	public Map<String, String> getQueryParams() {
		return queryParams;
	}
	public void setQueryParams(Map<String, String> queryParams) {
		this.queryParams = queryParams;
	}
	public String getReqData() {
		return reqData;
	}
	public void setReqData(String reqData) {
		this.reqData = reqData;
	}
	
	
}
