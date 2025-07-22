package com.ey.advisory.gstr9.jsontocsv.model.gstr1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CdnraInvoice extends CdnrInvoice {

	@Expose @SerializedName("ont_num")
	private String originalNoteNumber;

	@Expose @SerializedName("ont_dt")
	private String originalNoteDate;

	public String getOriginalNoteNumber() {
		return originalNoteNumber;
	}

	public String getOriginalNoteDate() {
		return originalNoteDate;
	}

	@Override
	public String toString() {
		return "CdnraInvoice [noteNumber=" + originalNoteNumber + ", noteDate="
				+ originalNoteDate + "]";
	}

}
