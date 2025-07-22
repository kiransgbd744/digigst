package com.ey.advisory.gstr9.jsontocsv.model.gstr2A;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CdnInvoiceGstr2A {
	@SerializedName("chksum")
	@Expose
	private String checkSum;

	@SerializedName("ntty")
	@Expose
	private String noteType;

	@SerializedName("nt_num")
	@Expose
	private String noteNumber;

	@SerializedName("nt_dt")
	@Expose
	private String noteDate;

	@SerializedName("p_gst")
	@Expose
	private String preGst;

	@SerializedName("inum")
	@Expose
	private String invoiceNumber;

	@SerializedName("idt")
	@Expose
	private String invoiceDate;

	@SerializedName("val")
	@Expose
	private String noteValue;

	@SerializedName("diff_percent")
	@Expose
	private String diffPercent;

	@SerializedName("itms")
	@Expose
	private List<CdnLineItemGstr2A> lineItems;

	public String getCheckSum() {
		return checkSum;
	}

	public String getNoteType() {
		return noteType;
	}

	public String getNoteNumber() {
		return noteNumber;
	}

	public String getNoteDate() {
		return noteDate;
	}

	public String getPreGst() {
		return preGst;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public String getInvoiceDate() {
		return invoiceDate;
	}

	public String getNoteValue() {
		return noteValue;
	}

	public String getDiffPercent() {
		return diffPercent;
	}

	public List<CdnLineItemGstr2A> getLineItems() {
		return lineItems;
	}

	@Override
	public String toString() {
		return "CdnInvoice [checkSum=" + checkSum + ",noteType=" + noteType
				+ ", noteNumber=" + noteNumber + ", noteDate=" + noteDate + ", preGst=" + preGst + ", invoiceNumber="
				+ invoiceNumber + ", invoiceDate=" + invoiceDate + ", invoiceValue=" + noteValue + ", lineItems="
				+ lineItems + "]";
	}

}
