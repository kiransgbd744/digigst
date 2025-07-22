package com.ey.advisory.app.services.aspreports;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MasterAspReportsDetailsRequestDto {

	@Expose
	@SerializedName("reportsKey")
	private String reportsKey;

	public String getReportsKey() {
		return reportsKey;
	}

	public void setReportsKey(String reportsKey) {
		this.reportsKey = reportsKey;
	}

}
