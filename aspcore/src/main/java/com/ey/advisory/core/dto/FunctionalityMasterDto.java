package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FunctionalityMasterDto {
	
	@Expose
	@SerializedName("functCode")
	private String functCode;
	
	@Expose
	@SerializedName("functDesc")
	private String functDesc;

	/**
	 * @return the functCode
	 */
	public String getFunctCode() {
		return functCode;
	}

	/**
	 * @param functCode the functCode to set
	 */
	public void setFunctCode(String functCode) {
		this.functCode = functCode;
	}

	/**
	 * @return the functDesc
	 */
	public String getFunctDesc() {
		return functDesc;
	}

	/**
	 * @param functDesc the functDesc to set
	 */
	public void setFunctDesc(String functDesc) {
		this.functDesc = functDesc;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FunctionalityMasterDto [functCode=" + functCode + ", functDesc="
				+ functDesc + "]";
	}
	
}
