package com.ey.advisory.app.docs.dto.anx2;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Anx2ReconResultsAbsoluteMatchReqDto {
	
	@Expose
	@SerializedName("gstins")
	private List<String> sgstins = new ArrayList<>();

	@Expose
	@SerializedName("retPeriod")
	private String returnPeriod;

	public List<String> getSgstins() {
		return sgstins;
	}

	public void setSgstins(List<String> sgstins) {
		this.sgstins = sgstins;
	}

	public String getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}
}
