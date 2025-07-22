package com.ey.advisory.app.docs.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FilingReqDto {

	
	@Expose
	@SerializedName("gstin")
	private String sgstins;

	@Expose
	@SerializedName("returnPeriod")
	private String returnPeriods;

	/**
	 * @return the sgstins
	 */
	public String getSgstins() {
		return sgstins;
	}

	/**
	 * @param sgstins the sgstins to set
	 */
	public void setSgstins(String sgstins) {
		this.sgstins = sgstins;
	}

	/**
	 * @return the returnPeriods
	 */
	public String getReturnPeriods() {
		return returnPeriods;
	}

	/**
	 * @param returnPeriods the returnPeriods to set
	 */
	public void setReturnPeriods(String returnPeriods) {
		this.returnPeriods = returnPeriods;
	}
	
	
}
