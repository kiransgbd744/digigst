package com.ey.advisory.gstr9.jsontocsv.model.gstr2A;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class B2bInvoiceGstr2A {

	@SerializedName("chksum")
	@Expose
	private String checkSum;

	@SerializedName("inum")
	@Expose
	private String invoiceNumber;

	@SerializedName("idt")
	@Expose
	private String invoiceDate;

	@SerializedName("val")
	@Expose
	private String invoiceValue;

	@SerializedName("pos")
	@Expose
	private String pos;

	@SerializedName("rchrg")
	@Expose
	private String reverseCharge;

	@SerializedName("inv_typ")
	@Expose
	private String invoiceType;

	@SerializedName("diff_percent")
	@Expose
	private String diffPercent;

	@SerializedName("itms")
	@Expose
	private List<B2bLineItemGstr2A> lineItems;

	public String getCheckSum() {
		return checkSum;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public String getInvoiceDate() {
		return invoiceDate;
	}

	public String getInvoiceValue() {
		return invoiceValue;
	}

	public String getPos() {
		return pos;
	}

	public String getReverseCharge() {
		return reverseCharge;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public String getDiffPercent() {
		return diffPercent;
	}

	public List<B2bLineItemGstr2A> getLineItems() {
		return lineItems;
	}

	@Override
	public String toString() {
		return "B2bInvoice [checkSum=" + checkSum + ",invoiceNumber="
				+ invoiceNumber + ", invoiceDate=" + invoiceDate + ", invoiceValue=" + invoiceValue + ", pos=" + pos
				+ ", reverseCharge=" + reverseCharge + ", invoiceType=" + invoiceType + ", " + "diffPercent="
				+ diffPercent + ", lineItems=" + lineItems + "]";
	}

}
