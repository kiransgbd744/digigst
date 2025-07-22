package com.ey.advisory.gstr9.jsontocsv.model.gstr2A;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TdsInvoiceGstr2A {
	
	@SerializedName("gstin_ded")
	@Expose
	private String gstinDeducted;

	@SerializedName("amt_ded")
	@Expose
	private String amtDeducted;
	
	@SerializedName("iamt")
	@Expose
	private String igstAmount;

	@SerializedName("camt")
	@Expose
	private String cgstAmount;

	@SerializedName("samt")
	@Expose
	private String sgstAmount;

	public String getGstinDeducted() {
		return gstinDeducted;
	}

	public String getAmtDeducted() {
		return amtDeducted;
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

	@Override
	public String toString() {
		return "TdsInvoice [gstinDeducted="+gstinDeducted+",amtDeducted=" + amtDeducted + 
				",igstAmount=" +igstAmount + ",cgstAmount=" 
				+ cgstAmount +",sgstAmount=" +sgstAmount + "]";
	}

}
