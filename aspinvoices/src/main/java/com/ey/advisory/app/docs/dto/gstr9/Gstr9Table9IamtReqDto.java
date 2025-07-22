package com.ey.advisory.app.docs.dto.gstr9;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr9Table9IamtReqDto {

	@Expose
	@SerializedName("txpyble")
	private BigDecimal txpyble = new BigDecimal("0.00");

	@Expose
	@SerializedName("txpaid_cash")
	private BigDecimal txpaidCash;

	@Expose
	@SerializedName("tax_paid_itc_iamt")
	private BigDecimal taxPaidItcIamt;

	@Expose
	@SerializedName("tax_paid_itc_camt")
	private BigDecimal taxPaidItcCamt;

	@Expose
	@SerializedName("tax_paid_itc_samt")
	private BigDecimal taxPaidItcSamt;
}
