package com.ey.advisory.gstr9.jsontocsv.model.gstr2A;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CdnaInvoiceGstr2A extends CdnInvoiceGstr2A {
	
	@SerializedName("ont_num")
	@Expose
	private String orgNoteNumber;

	@SerializedName("ont_dt")
	@Expose
	private String orgNoteDate;

	public String getOrgNoteNumber() {
		return orgNoteNumber;
	}

	public String getOrgNoteDate() {
		return orgNoteDate;
	}

	@Override
	public String toString() {
		return "CdnaInvoice [orgInvoiceNumber=" + orgNoteNumber
				+ ", orgInvoiceDate=" + orgNoteDate + "]";
	}
	
	

}
