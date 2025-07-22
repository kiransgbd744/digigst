package com.ey.advisory.app.docs.dto.anx1;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SavestatusReqDto {

	@Expose
	@SerializedName("entityId")
	private String entityId;

	@Expose
	@SerializedName("returnType")
	private String returnType;

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public String getTaxPeriod() {
		return taxPeriod;
	}

	public void setTaxPeriod(String taxPeriod) {
		this.taxPeriod = taxPeriod;
	}

}
