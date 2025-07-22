package com.ey.advisory.gstr9.jsontocsv.model.gstr3B;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gstr3BInvoice {

	@SerializedName("gstin")
	@Expose
	private String gstin;

	@SerializedName("ret_period")
	@Expose
	private String retPriod;

	@SerializedName("sup_details")
	@Expose
	private SupDetailsInvoiceGstr3B supDetails;
	
	@SerializedName("inter_sup")
	@Expose
	private InterSupDetailsInvoiceGstr3B inter_sup;

	public String getGstin() {
		return gstin;
	}

	public String getRetPriod() {
		return retPriod;
	}

	public SupDetailsInvoiceGstr3B getSupDetails() {
		return supDetails;
	}


	@Override
	public String toString() {
		return "Gstr3BInvoice [gstin="+ gstin + ", retPriod=" + retPriod + ","
				+ " supDetails=" + supDetails + ", inter_sup=" + inter_sup + "]";
	}
}
