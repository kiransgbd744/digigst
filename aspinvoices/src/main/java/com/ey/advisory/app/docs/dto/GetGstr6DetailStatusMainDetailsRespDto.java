package com.ey.advisory.app.docs.dto;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetGstr6DetailStatusMainDetailsRespDto {

	@Expose
	@SerializedName("lastCall")
	private List<GetGstr6DetailStatusSubItemDetailsRespDto> lastCall = new ArrayList<GetGstr6DetailStatusSubItemDetailsRespDto>();

	@Expose
	@SerializedName("lastSuccess")
	private List<GetGstr6DetailStatusSubItemDetailsRespDto> lastSuccess = new ArrayList<GetGstr6DetailStatusSubItemDetailsRespDto>();

	public List<GetGstr6DetailStatusSubItemDetailsRespDto> getLastCall() {
		return lastCall;
	}

	public void setLastCall(
			List<GetGstr6DetailStatusSubItemDetailsRespDto> lastCall) {
		this.lastCall = lastCall;
	}

	public List<GetGstr6DetailStatusSubItemDetailsRespDto> getLastSuccess() {
		return lastSuccess;
	}

	public void setLastSuccess(
			List<GetGstr6DetailStatusSubItemDetailsRespDto> lastSuccess) {
		this.lastSuccess = lastSuccess;
	}

}
