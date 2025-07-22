package com.ey.advisory.app.docs.dto.ledger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class GetCashITCBalanceReqDto {

	@Expose
	@SerializedName("ret_period")
	private String retPeriod;
	
	@Expose
	@SerializedName("gstin")
	private String gstin;

	public String getRetPeriod() {
		return retPeriod;
	}

	public String getGstin() {
		return gstin;
	}

	public void setRetPeriod(String retPeriod) {
		this.retPeriod = retPeriod;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}
	
	
}
