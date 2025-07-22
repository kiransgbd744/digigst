package com.ey.advisory.gstr9.jsontocsv.model.gstr1;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CdnurInvoice {

	@Expose
	@SerializedName("typ")
	private String invoiceType;

	@Expose
	@SerializedName("chksum")
	private String checkSum;

	@Expose
	@SerializedName("ntty")
	private String noteType;

	@Expose
	@SerializedName("nt_num")
	private String noteNumber;

	@Expose
	@SerializedName("nt_dt")
	private String noteDate;

	@Expose
	@SerializedName("p_gst")
	private String preGst;

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
	@SerializedName("flag")
	private String invoiceStatus;

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
	@SerializedName("d_flag")
	private String dlinkFlag;
	
	@Expose
	@SerializedName("pos")
	private String pos;
	
	@Expose
	@SerializedName("rchrg")
	private String reverseCharge;

	@Expose
	@SerializedName("itms")
	private List<CdnurLineItem> lineItems;

	public String getInvoiceType() {
		return invoiceType;
	}

	public String getDiffPercent() {
		return diffPercent;
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

	public String getInvoiceValue() {
		return invoiceValue;
	}

	public String getInvoiceStatus() {
		return invoiceStatus;
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

	public List<CdnurLineItem> getLineItems() {
		return lineItems;
	}

	public String getCheckSum() {
		return checkSum;
	}

	public String getDlinkFlag() {
		return dlinkFlag;
	}

	public String getPos() {
		return pos;
	}

	public String getReverseCharge() {
		return reverseCharge;
	}

	@Override
	public String toString() {
		return "CdnurInvoice [invoiceType=" + invoiceType + ", noteType="
				+ noteType + ", noteNumber=" + noteNumber + ", noteDate="
				+ noteDate + ", preGst=" + preGst + ", invoiceNumber="
				+ invoiceNumber + ", invoiceDate=" + invoiceDate
				+ ", invoiceValue=" + invoiceValue + ", invoiceStatus="
				+ invoiceStatus + ", diffPercent=" + diffPercent
				+ ", lineItems=" + lineItems + "]";
	}

}
