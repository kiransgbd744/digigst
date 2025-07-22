package com.ey.advisory.app.docs.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ey.advisory.core.search.SearchCriteria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EntityIRDto{
	
	/*public EntityIRDto(String searchType) {
		super(SearchTypeConstants.INITIATE_RECON);
		// TODO Auto-generated constructor stub
	}
*/
	public EntityIRDto() {
		// TODO Auto-generated constructor stub
	}

	@Expose
	@SerializedName("entityId")
	private List<Long> entityId;
	
	@Expose
	@SerializedName("gstin")
	private List<String> sgstins = new ArrayList<>();
	
	@Expose
	@SerializedName("returnPeriod")
	private  String returnPeriod;
	
	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs;

	public List<Long> getEntityId() {
		return entityId;
	}

	public void setEntityId(List<Long> entityId) {
		this.entityId = entityId;
	}

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

	public Map<String, List<String>> getDataSecAttrs() {
		return dataSecAttrs;
	}

	public void setDataSecAttrs(Map<String, List<String>> dataSecAttrs) {
		this.dataSecAttrs = dataSecAttrs;
	}

	@Override
	public String toString() {
		return "EntityIRDto [entityId=" + entityId + ", sgstins=" + sgstins
				+ ", returnPeriod=" + returnPeriod + ", dataSecAttrs="
				+ dataSecAttrs + "]";
	}
	
	
	
	
}