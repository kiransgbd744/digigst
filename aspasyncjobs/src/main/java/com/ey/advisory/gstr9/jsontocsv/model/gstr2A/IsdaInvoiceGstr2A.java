package com.ey.advisory.gstr9.jsontocsv.model.gstr2A;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IsdaInvoiceGstr2A extends IsdInvoiceGstr2A {
	
	@SerializedName("odocnum")
	@Expose
	private String originalDocumentNumber;

	@SerializedName("odocdt")
	@Expose
	private String originalDocumentDate;

	public String getOriginalDocumentNumber() {
		return originalDocumentNumber;
	}

	public String getOriginalDocumentDate() {
		return originalDocumentDate;
	}

	@Override
	public String toString() {
		return "IsdInvoice [originalDocumentNumber=" +originalDocumentNumber +","
				+ " originalDocumentDate=" + originalDocumentDate +"]";
	}
	
	

}
