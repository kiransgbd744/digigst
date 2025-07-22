package com.ey.advisory.app.docs.dto.ledger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class RetLiabBalanceReqDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("action")
	private String action;

	@Expose
	@SerializedName("ret_period")
	private String retPeriod;

	@Expose
	@SerializedName("ret_type")
	private String retType;

	public String getGstin() {
		return gstin;
	}

	public String getAction() {
		return action;
	}

	public String getRetPeriod() {
		return retPeriod;
	}

	public String getRetType() {
		return retType;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setRetPeriod(String retPeriod) {
		this.retPeriod = retPeriod;
	}

	public void setRetType(String retType) {
		this.retType = retType;
	}

	@Override
	public String toString() {
		return "RetLiabBalanceReqDto [gstin=" + gstin + ", action=" + action
				+ ", retPeriod=" + retPeriod + ", retType=" + retType + "]";
	}

}
