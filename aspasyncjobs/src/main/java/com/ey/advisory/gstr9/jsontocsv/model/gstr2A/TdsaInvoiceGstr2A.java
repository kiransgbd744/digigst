package com.ey.advisory.gstr9.jsontocsv.model.gstr2A;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TdsaInvoiceGstr2A {
	
	@SerializedName("ogstin_ded")
	@Expose
	private String orgGstinDeducted;
	
	@SerializedName("omonth")
	@Expose
	private String orgMonth;

	@SerializedName("oamt_ded")
	@Expose
	private String orgAmtDeducted;
	
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

	public String getOrgGstinDeducted() {
		return orgGstinDeducted;
	}

	public String getOrgMonth() {
		return orgMonth;
	}

	public String getOrgAmtDeducted() {
		return orgAmtDeducted;
	}
	
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
		return "TdsaInvoice [orgGstinDeducted = " + orgGstinDeducted + ", "
				+ "orgMonth = " + orgMonth + ", orgAmtDeducted=" +orgAmtDeducted + ","
				+ "gstinDeducted = " + gstinDeducted + ", amtDeducted=" + amtDeducted + ","
				+ "igstAmount=" +igstAmount + ",cgstAmount=" + cgstAmount +",sgstAmount=" +sgstAmount + "]";
	}

}
