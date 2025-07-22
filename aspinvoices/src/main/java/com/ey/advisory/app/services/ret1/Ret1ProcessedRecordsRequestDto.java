package com.ey.advisory.app.services.ret1;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * 
 * @author Anand3.M
 *
 */


public class Ret1ProcessedRecordsRequestDto {

	@Expose
	@SerializedName("entityId")
	private List<Long> entityId;

	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;

	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs;

	@Expose
	@SerializedName("gstin")
	private List<String> gstins;

	public List<Long> getEntityId() {
		return entityId;
	}

	public void setEntityId(List<Long> entityId) {
		this.entityId = entityId;
	}

	public String getTaxPeriod() {
		return taxPeriod;
	}

	public void setTaxPeriod(String taxPeriod) {
		this.taxPeriod = taxPeriod;
	}

	public Map<String, List<String>> getDataSecAttrs() {
		return dataSecAttrs;
	}

	public void setDataSecAttrs(Map<String, List<String>> dataSecAttrs) {
		this.dataSecAttrs = dataSecAttrs;
	}

	public List<String> getGstins() {
		return gstins;
	}

	public void setGstins(List<String> gstins) {
		this.gstins = gstins;
	}

	@Override
	public String toString() {
		return "Ret1ProcessedRecordsRequestDto [entityId=" + entityId
				+ ", taxPeriod=" + taxPeriod + ", dataSecAttrs=" + dataSecAttrs
				+ ", gstins=" + gstins + "]";
	}

}
