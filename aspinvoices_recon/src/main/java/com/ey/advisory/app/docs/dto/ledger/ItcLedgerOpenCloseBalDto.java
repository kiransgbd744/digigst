package com.ey.advisory.app.docs.dto.ledger;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@ToString
@Setter
@Getter
public class ItcLedgerOpenCloseBalDto {
	
	@Expose
	@SerializedName(value = "desc")
	private String description;
	
	@Expose
	private BigDecimal igstTaxBal = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal cgstTaxBal = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal sgstTaxBal = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal cessTaxBal = BigDecimal.ZERO;
	
	
	@Expose
	@SerializedName("tot_rng_bal")
	private BigDecimal totRngBal = BigDecimal.ZERO;
	
}
