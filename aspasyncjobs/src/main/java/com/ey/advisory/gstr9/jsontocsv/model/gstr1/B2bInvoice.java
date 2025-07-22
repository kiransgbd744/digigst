package com.ey.advisory.gstr9.jsontocsv.model.gstr1;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class B2bInvoice {

	@Expose
	@SerializedName("inum")
	private String invoiceNumber;

	@Expose
	@SerializedName("updby")
	private String uploadedBy;

	@Expose
	@SerializedName("flag")
	private String invoiceStatus;

	@Expose
	@SerializedName("idt")
	private String invoiceDate;

	@Expose
	@SerializedName("chksum")
	private String checkSum;

	@Expose
	@SerializedName("val")
	private String invoiceValue;

	@Expose
	@SerializedName("pos")
	private String pos;

	@Expose
	@SerializedName("rchrg")
	private String reverseCharge;

	@Expose
	@SerializedName("etin")
	private String ecomTin;

	@Expose
	@SerializedName("inv_typ")
	private String invoiceType;

	@Expose
	@SerializedName("cflag")
	private String counterPartyFlag;

	@Expose
	@SerializedName("diff_percent")
	private String diffPercent;

	@Expose
	@SerializedName("opd")
	private String taxPeriod;
	
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
	private List<B2bLineItem> lineItems;

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

	public String getEcomTin() {
		return ecomTin;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public String getCounterPartyFlag() {
		return counterPartyFlag;
	}

	public String getDiffPercent() {
		return diffPercent;
	}

	public String getTaxPeriod() {
		return taxPeriod;
	}

	public List<B2bLineItem> getLineItems() {
		return lineItems;
	}

	public String getCheckSum() {
		return checkSum;
	}

	public String getUploadedBy() {
		return uploadedBy;
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

	@Override
	public String toString() {
		return "B2bInvoice [invoiceNumber=" + invoiceNumber + ", uploadedBy="
				+ uploadedBy + ", invoiceDate=" + invoiceDate + ", checkSum="
				+ checkSum + ", invoiceValue=" + invoiceValue + ", pos=" + pos
				+ ", reverseCharge=" + reverseCharge + ", ecomTin=" + ecomTin
				+ ", invoiceType=" + invoiceType + ", counterPartyFlag="
				+ counterPartyFlag + ", diffPercent=" + diffPercent
				+ ", taxPeriod=" + taxPeriod + ", lineItems=" + lineItems + "]";
	}

}
