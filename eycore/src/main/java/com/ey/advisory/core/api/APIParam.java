package com.ey.advisory.core.api;

public class APIParam {
	
	private String paramName;
	private String paramValue;
	private APIParamType paramType;
	
	public APIParam(String paramName, String paramValue) {
		super();
		this.paramName = paramName;
		this.paramValue = paramValue;
		this.paramType = APIParamType.ANY;
	}

	public APIParam(String paramName, String paramValue,
			APIParamType paramType) {
		super();
		this.paramName = paramName;
		this.paramValue = paramValue;
		this.paramType = paramType;
	}

	public String getParamName() {
		return paramName;
	}

	public String getKey() {
		return getParamName();
	}
	
	public String getParamValue() {
		return paramValue;
	}
	
	public String getValue() {
		return getParamValue();
	}

	public APIParamType getParamType() {
		return paramType;
	}

	@Override
	public String toString() {
		return "APIParam [paramName=" + paramName + 
				", paramValue=" + paramValue + 
				", paramType=" + paramType + "]";
	}
	
}
