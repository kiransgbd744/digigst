package com.ey.advisory.app.anx1.taxamountrecon;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaxAmountReconRet1Dto {
	
	@Expose
	private BigDecimal retTotalIgstAmt = BigDecimal.ZERO;

	@Expose
	private BigDecimal retTotalCgstAmt = BigDecimal.ZERO;

	@Expose
	private BigDecimal retTotalSgstAmt = BigDecimal.ZERO;

}
