package com.ey.advisory.app.services.ledger;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RcmLedgerOpnBal {

	@Expose
	private BigDecimal igst;

	@Expose
	private BigDecimal cgst;

	@Expose
	private BigDecimal sgst;

	@Expose
	private BigDecimal cess;

}
