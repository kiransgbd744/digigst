package com.ey.advisory.app.docs.dto.anx1;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class Anx1ImpgAndImpgSezBillEntryDetails {

	@Expose
	@SerializedName("num")
	private String billNum;

	@Expose
	@SerializedName("pcode")
	private String portCode;

	@Expose
	@SerializedName("dt")
	private String billDate;

	@Expose
	@SerializedName("val")
	private BigDecimal billValue;

	public String getBillNum() {
		return billNum;
	}

	public void setBillNum(String billNum) {
		this.billNum = billNum;
	}

	public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	public String getBillDate() {
		return billDate;
	}

	public void setBillDate(String billDate) {
		this.billDate = billDate;
	}

	public BigDecimal getBillValue() {
		return billValue;
	}

	public void setBillValue(BigDecimal billValue) {
		this.billValue = billValue;
	}

}
