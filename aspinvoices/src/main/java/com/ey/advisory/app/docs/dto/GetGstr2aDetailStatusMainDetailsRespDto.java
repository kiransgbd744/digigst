package com.ey.advisory.app.docs.dto;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetGstr2aDetailStatusMainDetailsRespDto {
	@Expose
	@SerializedName("lastCall")
	private List<GetGstr2aDetailStatusSubItemDetailsRespDto> lastCall = new ArrayList<GetGstr2aDetailStatusSubItemDetailsRespDto>();

	@Expose
	@SerializedName("lastSuccess")
	private List<GetGstr2aDetailStatusSubItemDetailsRespDto> lastSuccess = new ArrayList<GetGstr2aDetailStatusSubItemDetailsRespDto>();

	public List<GetGstr2aDetailStatusSubItemDetailsRespDto> getLastCall() {
		return lastCall;
	}

	public void setLastCall(
			List<GetGstr2aDetailStatusSubItemDetailsRespDto> lastCall) {
		this.lastCall = lastCall;
	}

	public List<GetGstr2aDetailStatusSubItemDetailsRespDto> getLastSuccess() {
		return lastSuccess;
	}

	public void setLastSuccess(
			List<GetGstr2aDetailStatusSubItemDetailsRespDto> lastSuccess) {
		this.lastSuccess = lastSuccess;
	}

}
