package com.ey.advisory.gstr9.jsontocsv.model.gstr1;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExpType {

	@Expose
	@SerializedName("flag")
	private String invoiceStatus;

	@Expose
	@SerializedName("chksum")
	private String checkSum;

	@Expose
	@SerializedName("inum")
	private String invoiceNumber;

	@Expose
	@SerializedName("val")
	private String invoiceValue;

	@Expose
	@SerializedName("idt")
	private String invoiceDate;

	@Expose
	@SerializedName("sbpcode")
	private String portCode;

	@Expose
	@SerializedName("sbnum")
	private String billNumber;

	@Expose
	@SerializedName("sbdt")
	private String billDate;

	@Expose
	@SerializedName("diff_percent")
	private String diffPercent;
	
	@Expose
	@SerializedName("srctyp")
	private String srctyp;
	
	@Expose
	@SerializedName("irn")
	private String irn;
	
	@Expose
	@SerializedName("irngendate")
	private String irngendate;

	@Expose
	@SerializedName("itms")
	private List<ExpLineItemDetail> lineItems;

	public String getInvoiceStatus() {
		return invoiceStatus;
	}

	public String getDiffPercent() {
		return diffPercent;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public String getInvoiceValue() {
		return invoiceValue;
	}

	public String getInvoiceDate() {
		return invoiceDate;
	}

	public String getCheckSum() {
		return checkSum;
	}

	public String getPortCode() {
		return portCode;
	}

	public String getBillNumber() {
		return billNumber;
	}

	public String getBillDate() {
		return billDate;
	}

	public String getSrctyp() {
		return srctyp;
	}

	public String getIrn() {
		return irn;
	}

	public String getIrngendate() {
		return irngendate;
	}

	public List<ExpLineItemDetail> getLineItems() {
		return lineItems;
	}

	@Override
	public String toString() {
		return "ExpInvoice [invoiceStatus=" + invoiceStatus + ", invoiceNumber="
				+ invoiceNumber + ", invoiceValue=" + invoiceValue
				+ ", invoiceDate=" + invoiceDate + ", portCode=" + portCode
				+ ", billNumber=" + billNumber + ", billDate=" + billDate
				+ ", lineItems=" + lineItems + "]";
	}

}
