package com.ey.advisory.gstr9.jsontocsv.model.gstr1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CdnuraInvoice extends CdnurInvoice {

	@Expose @SerializedName("ont_num")
	private String orgNoteNumber;

	@Expose @SerializedName("ont_dt")
	private String orgNoteDate;

	public String getOrgNoteNumber() {
		return orgNoteNumber;
	}

	public String getOrgNoteDate() {
		return orgNoteDate;
	}

	@Override
	public String toString() {
		return "CdnuraInvoice [orgNoteNumber=" + orgNoteNumber
				+ ", orgNoteDate=" + orgNoteDate + "]";
	}

}
