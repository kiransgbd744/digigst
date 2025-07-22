package com.ey.advisory.app.docs.dto.gstr3B;

import com.ey.advisory.app.gstr3b.dto.TaxPaymentDetailsInvoice;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Gstr3BSavetoGstnDTO {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("ret_period")
	private String retPeriod;

	@Expose
	@SerializedName("sup_details")
	private Gstr3BOutInwSuppDTO supDetails;

	@Expose
	@SerializedName("eco_dtls")
	private Gstr3BEcoDtlsDTO ecoDtls;

	@Expose
	@SerializedName("inter_sup")
	private Gstr3BInterStateSuppDTO interSup;

	@Expose
	@SerializedName("itc_elg")
	private Gstr3BElgItcDTO itcElg;

	@Expose
	@SerializedName("inward_sup")
	private Gstr3BInwSuppDTO inwardSup;

	@Expose
	@SerializedName("intr_ltfee")
	private Gstr3BIntLateFeeDTO intrLtfee;

	@SerializedName("tx_pmt")
	@Expose
	private TaxPaymentDetailsInvoice txPmt;

}
