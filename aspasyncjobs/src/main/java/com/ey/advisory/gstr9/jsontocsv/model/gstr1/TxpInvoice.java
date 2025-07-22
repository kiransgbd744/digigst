package com.ey.advisory.gstr9.jsontocsv.model.gstr1;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TxpInvoice {

	@Expose
	@SerializedName("flag")
	private String invoiceStatus;

	@Expose
	@SerializedName("chksum")
	private String checkSum;

	@Expose
	@SerializedName("pos")
	private String pos;

	@Expose
	@SerializedName("sply_ty")
	private String supplyType;

	@Expose
	@SerializedName("diff_percent")
	private String diffPercent;

	@Expose
	@SerializedName("itms")
	private List<TxpLineItemDetail> lineItems;

	public String getInvoiceStatus() {
		return invoiceStatus;
	}

	public String getPos() {
		return pos;
	}

	public String getCheckSum() {
		return checkSum;
	}

	public String getSupplyType() {
		return supplyType;
	}

	public String getDiffPercent() {
		return diffPercent;
	}

	public List<TxpLineItemDetail> getLineItems() {
		return lineItems;
	}

	@Override
	public String toString() {
		return "AtInvoice [invoiceStatus=" + invoiceStatus + ", pos=" + pos
				+ ", supplyType=" + supplyType + ", diffPercent=" + diffPercent
				+ ", lineItems=" + lineItems + "]";
	}

}
