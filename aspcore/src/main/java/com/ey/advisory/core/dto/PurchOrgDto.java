package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Umesha.M
 *
 */
public class PurchOrgDto {

	@Expose
	@SerializedName("purchOrg")
	private String purchOrg;

	@Expose
	@SerializedName("id")
	private Long id;
	
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

	/**
	 * @return the purchOrg
	 */
	public String getPurchOrg() {
		return purchOrg;
	}

	/**
	 * @param purchOrg the purchOrg to set
	 */
	public void setPurchOrg(String purchOrg) {
		this.purchOrg = purchOrg;
	}
	
	
}
