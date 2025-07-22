package com.ey.advisory.app.docs.dto.anx2;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Anx2ListGSTINForReconReq {

	@Expose
	@SerializedName("entityId")
	private List<Long> entityId;
	
	@Expose
	@SerializedName("gstin")
	private List<String> cgstins = new ArrayList<>();
	
	@Expose
	@SerializedName("returnPeriod")
	private  String returnPeriod;

	public List<String> getCgstins() {
		return cgstins;
	}

	public void setCgstins(List<String> cgstins) {
		this.cgstins = cgstins;
	}

	public List<Long> getEntityId() {
		return entityId;
	}

	public void setEntityId(List<Long> entityId) {
		this.entityId = entityId;
	}

	public String getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	@Override
	public String toString() {
		return "Anx2ListGSTINForReconReq [entityId=" + entityId
				+ ", returnPeriod=" + returnPeriod + "]";
	}
	
}
