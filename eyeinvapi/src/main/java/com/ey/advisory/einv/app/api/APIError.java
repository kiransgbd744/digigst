package com.ey.advisory.einv.app.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class APIError {

	@Expose
	@SerializedName("ErrorCode")
	protected String errorCode;
	@Expose
	@SerializedName("ErrorMessage")
	protected String errorDesc;
	@Expose
	@SerializedName("errorInfo")
	protected String errorInfo;

	@Expose
	@SerializedName("regIrp")
	protected String regIrp;

	public APIError() {
	}

	public APIError(String errorCode, String errorDesc) {
		this.errorCode = errorCode;
		this.errorDesc = errorDesc;
	}

}