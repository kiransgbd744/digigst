package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.ey.advisory.core.search.SearchCriteria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Mohana.Dasari
 *
 */
public class GstnSaveSubmitReqDto extends SearchCriteria {
	
	public GstnSaveSubmitReqDto(String searchType) {
		super(searchType);
	}

	@Expose
	@SerializedName("retPeriodFrom")
	private String retPeriodFrom; // renamed
	
	@Expose
	@SerializedName("retPeriodTo")
	private String retPeriodTo;// renamed
	
	@Expose
	@SerializedName("entityId")
	private List<Long> entityId;// it is a list
	
	@Expose
	@SerializedName("gstin")
	private List<String> sgstins;

	/**
	 * @return the retPeriodFrom
	 */
	public String getRetPeriodFrom() {
		return retPeriodFrom;
	}

	/**
	 * @param retPeriodFrom the retPeriodFrom to set
	 */
	public void setRetPeriodFrom(String retPeriodFrom) {
		this.retPeriodFrom = retPeriodFrom;
	}

	/**
	 * @return the retPeriodTo
	 */
	public String getRetPeriodTo() {
		return retPeriodTo;
	}

	/**
	 * @param retPeriodTo the retPeriodTo to set
	 */
	public void setRetPeriodTo(String retPeriodTo) {
		this.retPeriodTo = retPeriodTo;
	}

	/**
	 * @return the entityId
	 */
	public List<Long> getEntityId() {
		return entityId;
	}

	/**
	 * @param entityId the entityId to set
	 */
	public void setEntityId(List<Long> entityId) {
		this.entityId = entityId;
	}

	/**
	 * @return the sgstins
	 */
	public List<String> getSgstins() {
		return sgstins;
	}

	/**
	 * @param sgstins the sgstins to set
	 */
	public void setSgstins(List<String> sgstins) {
		this.sgstins = sgstins;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format(
				"GstnSaveSubmitReqDto [retPeriodFrom=%s, retPeriodTo=%s, "
				+ "entityId=%s, sgstins=%s]",
				retPeriodFrom, retPeriodTo, entityId, sgstins);
	}
	

}
