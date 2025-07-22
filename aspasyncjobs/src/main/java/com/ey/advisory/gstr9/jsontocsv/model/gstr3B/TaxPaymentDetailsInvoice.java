package com.ey.advisory.gstr9.jsontocsv.model.gstr3B;

import java.util.List;

import com.ey.advisory.app.gstr3b.dto.TaxPayableDetails;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TaxPaymentDetailsInvoice {

	@SerializedName("tx_py")
	@Expose
	private List<TaxPayableDetails> taxPayable;

	@SerializedName("pdcash")
	@Expose
	private List<PaidCashDetails> paidCash;

	@SerializedName("pditc")
	@Expose
	private PaidItcDetails paidItc;

	public List<TaxPayableDetails> getTaxPayable() {
		return taxPayable;
	}

	public List<PaidCashDetails> getPaidCash() {
		return paidCash;
	}

	public PaidItcDetails getPaidItc() {
		return paidItc;
	}

	@Override
	public String toString() {
		return "TaxPaymentDetailsInvoice [taxPayment="
				+ taxPayable + ", paidCash=" + paidCash + ", paidItc="
				+ paidItc+ "]";
	}
}
