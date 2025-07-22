package com.ey.advisory.app.docs.dto.ledger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class GetOtherthanReturnLedgerReqDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("fromDate")
	private String fromDate;

	@Expose
	@SerializedName("toDate")
	private String toDate;

	@Expose
	@SerializedName("demid")
	private String demid;

	@Expose
	@SerializedName("stayStatus")
	private String stayStatus;

	public String getGstin() {
		return gstin;
	}

	public String getFromDate() {
		return fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public String getDemid() {
		return demid;
	}

	public String getStayStatus() {
		return stayStatus;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public void setDemid(String demid) {
		this.demid = demid;
	}

	public void setStayStatus(String stayStatus) {
		this.stayStatus = stayStatus;
	}

	@Override
	public String toString() {
		return "GetOtherthanReturnLedgerReqDto [gstin=" + gstin + ", fromDate="
				+ fromDate + ", toDate=" + toDate + ", demid=" + demid
				+ ", stayStatus=" + stayStatus + "]";
	}

}
