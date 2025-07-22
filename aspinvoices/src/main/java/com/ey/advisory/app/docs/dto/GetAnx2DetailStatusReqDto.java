package com.ey.advisory.app.docs.dto;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetAnx2DetailStatusReqDto {

	@Expose
	@SerializedName("entityId")
	private String entityId;

	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;
	
	@Expose
	@SerializedName("fromPeriod")
	private String fromPeriod;

	@Expose
	@SerializedName("toPeriod")
	private String toPeriod;

	public String getFromPeriod() {
		return fromPeriod;
	}

	public void setFromPeriod(String fromPeriod) {
		this.fromPeriod = fromPeriod;
	}

	public String getToPeriod() {
		return toPeriod;
	}

	public void setToPeriod(String toPeriod) {
		this.toPeriod = toPeriod;
	}

	@Expose
	@SerializedName("gstin")
	private List<String> gstin = new ArrayList<>();

	public List<String> getGstin() {
		return gstin;
	}

	public void setGstin(List<String> gstin) {
		this.gstin = gstin;
	}

	public String getTaxPeriod() {
		return taxPeriod;
	}

	public void setTaxPeriod(String taxPeriod) {
		this.taxPeriod = taxPeriod;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	@Override
	public String toString() {
		return "GetAnx2DetailStatusReqDto [entityId=" + entityId + ", taxPeriod=" + taxPeriod + ", fromPeriod="
				+ fromPeriod + ", toPeriod=" + toPeriod + "]";
	}
	

}
