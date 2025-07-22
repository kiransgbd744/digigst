package com.ey.advisory.app.docs.dto.gstr2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Getgstr2aApiCallDashboard {
	
	@Expose
	@SerializedName("entityId")
	private Long entityId;

	@Expose
	@SerializedName("returnType")
	private String returnType;

	@Expose
	@SerializedName("fyYear")
	private String fyYear;

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String getFyYear() {
		return fyYear;
	}

	public void setFyYear(String fyYear) {
		this.fyYear = fyYear;
	}

	
}
