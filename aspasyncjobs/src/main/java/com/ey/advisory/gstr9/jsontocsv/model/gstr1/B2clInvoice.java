package com.ey.advisory.gstr9.jsontocsv.model.gstr1;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class B2clInvoice {

	@Expose
	@SerializedName("inum")
	private String invoiceNumber;

	@Expose
	@SerializedName("idt")
	private String invoiceDate;

	@Expose
	@SerializedName("val")
	private String invoiceValue;

	@Expose
	@SerializedName("etin")
	private String ecomTin;

	@Expose
	@SerializedName("inv_typ")
	private String invoiceType;

	@Expose
	@SerializedName("flag")
	private String invoicestatus;

	@Expose
	@SerializedName("chksum")
	private String checkSum;

	@Expose
	@SerializedName("diff_percent")
	private String diffPercent;

	@Expose
	@SerializedName("itms")
	private List<B2clLineItem> lineItems;

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public String getInvoiceDate() {
		return invoiceDate;
	}

	public String getInvoiceValue() {
		return invoiceValue;
	}

	public String getEcomTin() {
		return ecomTin;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public String getInvoicestatus() {
		return invoicestatus;
	}

	public List<B2clLineItem> getLineItems() {
		return lineItems;
	}

	public String getCheckSum() {
		return checkSum;
	}

	public String getDiffPercent() {
		return diffPercent;
	}

	@Override
	public String toString() {
		return "B2clInvoice [invoiceNumber=" + invoiceNumber + ", invoiceDate="
				+ invoiceDate + ", invoiceValue=" + invoiceValue + ", ecomTin="
				+ ecomTin + ", invoiceType=" + invoiceType + ", invoicestatus="
				+ invoicestatus + ", lineItems=" + lineItems + "]";
	}

}
