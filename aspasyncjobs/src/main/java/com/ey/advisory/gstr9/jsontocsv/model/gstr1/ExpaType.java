package com.ey.advisory.gstr9.jsontocsv.model.gstr1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExpaType extends ExpType {

	@Expose
	@SerializedName("ctin")
	private String counterPartyGstin;

	@Expose
	@SerializedName("oinum")
	private String originalInvoiceNumber;

	@Expose
	@SerializedName("oidt")
	private String originalInvoiceDate;

	public String getOriginalInvoiceNumber() {
		return originalInvoiceNumber;
	}

	public String getOriginalInvoiceDate() {
		return originalInvoiceDate;
	}

	public String getCounterPartyGstin() {
		return counterPartyGstin;
	}

	@Override
	public String toString() {
		return "ExpaType [originalInvoiceNumber=" + originalInvoiceNumber
				+ ", originalInvoiceDate=" + originalInvoiceDate + "]";
	}

}
