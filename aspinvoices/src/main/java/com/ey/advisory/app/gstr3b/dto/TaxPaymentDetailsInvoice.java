package com.ey.advisory.app.gstr3b.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
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

	
}
