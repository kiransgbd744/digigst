package com.ey.advisory.app.gstr3b.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
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
	private InterSupDetailsInvoiceGstr3B interSup;
	
	@SerializedName("itc_elg")
	@Expose
	private ItcEligibleInvoiceGstr3B itcElg;
	
	@SerializedName("inward_sup")
	@Expose
	private InwardSupDetailsInvoiceGstr3B inwardSup;
	
	@SerializedName("intr_ltfee")
	@Expose
	private InterestLateFeeDetailsInvoice intrLtfee;
	
	@SerializedName("tx_pmt")
	@Expose
	private TaxPaymentDetailsInvoice txPmt;

	
}
