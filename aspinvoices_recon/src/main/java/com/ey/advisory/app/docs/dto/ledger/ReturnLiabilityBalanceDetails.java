package com.ey.advisory.app.docs.dto.ledger;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class ReturnLiabilityBalanceDetails {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("ret_period")
	private String retPeriod;

	@Expose
	@SerializedName("ret_type")
	private String retType;

	@Expose
	@SerializedName("op_liab")
	private List<OpenLiabilitiesDetails> openLiabilities;

	public String getGstin() {
		return gstin;
	}

	public String getRetPeriod() {
		return retPeriod;
	}

	public String getRetType() {
		return retType;
	}

	public List<OpenLiabilitiesDetails> getOpenLiabilities() {
		return openLiabilities;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public void setRetPeriod(String retPeriod) {
		this.retPeriod = retPeriod;
	}

	public void setRetType(String retType) {
		this.retType = retType;
	}

	public void setOpenLiabilities(
			List<OpenLiabilitiesDetails> openLiabilities) {
		this.openLiabilities = openLiabilities;
	}

}
