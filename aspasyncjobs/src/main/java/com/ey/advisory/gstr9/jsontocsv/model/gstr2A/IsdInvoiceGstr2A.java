package com.ey.advisory.gstr9.jsontocsv.model.gstr2A;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IsdInvoiceGstr2A {
	@SerializedName("chksum")
	@Expose
	private String checkSum;

	@SerializedName("isd_docty")
	@Expose
	private String documentType;
	
	@SerializedName("docnum")
	@Expose
	private String documentNumber;

	@SerializedName("docdt")
	@Expose
	private String documentDate;

	@SerializedName("itc_elg")
	@Expose
	private String eligibleITC;
	
	@SerializedName("iamt")
	@Expose
	private String igstAmount;

	@SerializedName("camt")
	@Expose
	private String cgstAmount;

	@SerializedName("samt")
	@Expose
	private String sgstAmount;

	@SerializedName("cess")
	@Expose
	private String cessAmount;
	
	public String getCheckSum() {
		return checkSum;
	}

	public String getDocumentType() {
		return documentType;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public String getDocumentDate() {
		return documentDate;
	}

	public String getEligibleITC() {
		return eligibleITC;
	}

	public String getIgstAmount() {
		return igstAmount;
	}

	public String getCgstAmount() {
		return cgstAmount;
	}

	public String getSgstAmount() {
		return sgstAmount;
	}

	public String getCessAmount() {
		return cessAmount;
	}

	@Override
	public String toString() {
		return "IsdInvoice [checkSum=" + checkSum + ",documentType=" + documentType + ", documentNumber="
				+ documentNumber + ", documentDate=" + documentDate + ", eligibleITC="
				+ eligibleITC +",igstAmount=" +igstAmount + ",cgstAmount=" 
				+ cgstAmount +",sgstAmount=" +sgstAmount + ", cessAmount=" +cessAmount + "]";
	}

}
