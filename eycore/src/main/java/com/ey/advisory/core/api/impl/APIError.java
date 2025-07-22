package com.ey.advisory.core.api.impl;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class APIError {

	@Expose
	@SerializedName("error_cd")
	protected String errorCode;

	@Expose
	@SerializedName("message")
	protected String errorDesc;

	public APIError() {
	}

	public APIError(String errorCode, String errorDesc) {
		this.errorCode = errorCode;
		this.errorDesc = errorDesc;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorDesc() {
		return errorDesc;
	}

	@Override
	public String toString() {
		return "APIError [errorCode=" + errorCode + ", errorDesc=" + errorDesc
				+ "]";
	}

}
