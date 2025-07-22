
package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Umesha.M
 *
 */
public class ProfitCenterDto {

	@Expose
	@SerializedName("id")
	private Long id;
	
	@Expose
	@SerializedName("profitCenter")
	private String profitCenter;

	/**
	 * @return the profitCenter
	 */
	public String getProfitCenter() {
		return profitCenter;
	}

	/**
	 * @param profitCenter the profitCenter to set
	 */
	public void setProfitCenter(String profitCenter) {
		this.profitCenter = profitCenter;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	
}
