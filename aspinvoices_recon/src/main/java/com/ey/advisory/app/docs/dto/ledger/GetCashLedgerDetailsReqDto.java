package com.ey.advisory.app.docs.dto.ledger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Data
public class GetCashLedgerDetailsReqDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;
	
	@Expose
	@SerializedName("fr_dt")
	private String fromDate;
	
	@Expose
	@SerializedName("to_dt")
	private String toDate;
	
	@Expose
	@SerializedName("frdt")
	private String fromDat;
	
	@Expose
	@SerializedName("todt")
	private String toDat;
	
	@Expose
	@SerializedName("from_date")
	private String frmDate;
	
	@Expose
	@SerializedName("to_date")
	private String toDte;
	
	
	
	
	@Expose
	@SerializedName("returnPeriod")
	private String returnPeriod;

	public String getGstin() {
		return gstin;
	}

	public String getFromDate() {
		return fromDate;
	}

	public String getToDate() {
		return toDate;
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

	@Override
	public String toString() {
		return "GetCashLedgerDetailsReqDto [gstin=" + gstin + ", fromDate="
				+ fromDate + ", toDate=" + toDate + ", returnPeriod="
				+ returnPeriod + "]";
	}

	/*@Override
	public String toString() {
		return "GetCashLedgerDetailsReqDto [gstin=" + gstin + ", fromDate="
				+ fromDate + ", toDate=" + toDate + "]";
	}
	*/
	
}
