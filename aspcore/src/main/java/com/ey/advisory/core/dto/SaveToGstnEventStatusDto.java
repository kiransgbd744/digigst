package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * 
 * @author Hemasundar.J
 *
 */
public class SaveToGstnEventStatusDto {

	@Expose
	@SerializedName("retPeriod")
	private String retPeriod ;
	
	@Expose
	@SerializedName("gstin")
	private String sgstin ;

	public String getRetPeriod() {
		return retPeriod;
	}

	public String getSgstin() {
		return sgstin;
	}

	public void setRetPeriod(String retPeriod) {
		this.retPeriod = retPeriod;
	}

	public void setSgstin(String sgstin) {
		this.sgstin = sgstin;
	}	
	
}
