package com.ey.advisory.gstr9.jsontocsv.model.gstr2A;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class B2baInvoiceGstr2A extends B2bInvoiceGstr2A {
	
	@SerializedName("oinum")
	@Expose
	private String orgInvoiceNumber;

	@SerializedName("oidt")
	@Expose
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
