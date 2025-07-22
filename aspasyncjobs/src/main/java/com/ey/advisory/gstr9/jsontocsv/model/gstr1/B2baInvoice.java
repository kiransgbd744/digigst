package com.ey.advisory.gstr9.jsontocsv.model.gstr1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class B2baInvoice extends B2bInvoice {
	
	@Expose @SerializedName("oinum")
	private String orgInvoiceNumber;

	@Expose @SerializedName("oidt")
	private String orgInvoiceDate;

	public String getOrgInvoiceNumber() {
		return orgInvoiceNumber;
	}

	public String getOrgInvoiceDate() {
		return orgInvoiceDate;
	}

	@Override
	public String toString() {
		return "B2baInvoice [orgInvoiceNumber=" + orgInvoiceNumber
				+ ", orgInvoiceDate=" + orgInvoiceDate + "]";
	}
	
	

}
