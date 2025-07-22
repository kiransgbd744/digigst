package com.ey.advisory.ewb.app.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.ToString;

@ToString
public class APIError {

	@Expose
	@SerializedName(value = "errorCode", alternate = { "ErrorCode" })
	protected String errorCode;

	@Expose
	@SerializedName(value = "errorDesc", alternate = { "ErrorMessage" })
	protected String errorDesc;
	@Expose
	@SerializedName(value = "errorInfo")
	protected String errorInfo;

	public APIError() {

	}

	public APIError(String errorCode, String errorDesc) {
		this.errorCode = errorCode;
		this.errorDesc = errorDesc;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorDesc() {
		return errorDesc;
	}

	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	public String getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

}