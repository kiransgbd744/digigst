/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Shashikant.Shukla
 *
 */
public class Gstr2BLinkingDto {

	@Expose
	@SerializedName("entityId")
	private List<Long> entityId;

	@SerializedName("financialYear")
	private String financialYear;

	/**
	 * @return the entityId
	 */
	public List<Long> getEntityId() {
		return entityId;
	}

	/**
	 * @param entityId
	 *            the entityId to set
	 */
	public void setEntityId(List<Long> entityId) {
		this.entityId = entityId;
	}


	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	
}
