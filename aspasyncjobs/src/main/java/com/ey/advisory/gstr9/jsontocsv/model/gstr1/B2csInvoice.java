package com.ey.advisory.gstr9.jsontocsv.model.gstr1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class B2csInvoice {

	@Expose
	@SerializedName("flag")
	private String invoicestatus;

	@Expose
	@SerializedName("chksum")
	private String checkSum;

	@Expose
	@SerializedName("sply_ty")
	private String supplyType;

	@Expose
	@SerializedName("diff_percent")
	private String diffPercent;

	@Expose
	@SerializedName("rt")
	private String rate;

	@Expose
	@SerializedName("typ")
	private String type;

	@Expose
	@SerializedName("pos")
	private String pos;

	@Expose
	@SerializedName("etin")
	private String ecomTin;

	@Expose
	@SerializedName("txval")
	private String taxableValue;

	@Expose
	@SerializedName("iamt")
	private String igstAmount;

	@Expose
	@SerializedName("samt")
	private String sgstAmount;

	@Expose
	@SerializedName("camt")
	private String cgstAmount;

	@Expose
	@SerializedName("csamt")
	private String cessAmount;

	public String getInvoicestatus() {
		return invoicestatus;
	}

	public String getSupplyType() {
		return supplyType;
	}

	public String getDiffPercent() {
		return diffPercent;
	}

	public String getRate() {
		return rate;
	}

	public String getType() {
		return type;
	}

	public String getPos() {
		return pos;
	}

	public String getEcomTin() {
		return ecomTin;
	}

	public String getTaxableValue() {
		return taxableValue;
	}

	public String getIgstAmount() {
		return igstAmount;
	}

	public String getCessAmount() {
		return cessAmount;
	}

	public String getCheckSum() {
		return checkSum;
	}

	public String getSgstAmount() {
		return sgstAmount;
	}

	public String getCgstAmount() {
		return cgstAmount;
	}

	@Override
	public String toString() {
		return "B2csInvoice [invoicestatus=" + invoicestatus + ", supplyType="
				+ supplyType + ", diffPercent=" + diffPercent + ", rate=" + rate
				+ ", type=" + type + ", pos=" + pos + ", ecomTin=" + ecomTin
				+ ", taxableValue=" + taxableValue + ", igstAmount="
				+ igstAmount + ", cessAmount=" + cessAmount + "]";
	}

}
