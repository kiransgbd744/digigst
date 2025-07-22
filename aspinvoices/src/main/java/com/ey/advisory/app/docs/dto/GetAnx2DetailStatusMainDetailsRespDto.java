package com.ey.advisory.app.docs.dto;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetAnx2DetailStatusMainDetailsRespDto {

	@Expose
	@SerializedName("lastCall")
	private List<GetAnx2DetailStatusSubItemDetailsRespDto> lastCall = new ArrayList<GetAnx2DetailStatusSubItemDetailsRespDto>();

	@Expose
	@SerializedName("lastSuccess")
	private List<GetAnx2DetailStatusSubItemDetailsRespDto> lastSuccess = new ArrayList<GetAnx2DetailStatusSubItemDetailsRespDto>();

	public List<GetAnx2DetailStatusSubItemDetailsRespDto> getLastCall() {
		return lastCall;
	}

	public void setLastCall(
			List<GetAnx2DetailStatusSubItemDetailsRespDto> lastCall) {
		this.lastCall = lastCall;
	}

	public List<GetAnx2DetailStatusSubItemDetailsRespDto> getLastSuccess() {
		return lastSuccess;
	}

	public void setLastSuccess(
			List<GetAnx2DetailStatusSubItemDetailsRespDto> lastSuccess) {
		this.lastSuccess = lastSuccess;
	}

}
