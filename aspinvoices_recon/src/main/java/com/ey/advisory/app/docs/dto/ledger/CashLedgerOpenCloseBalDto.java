package com.ey.advisory.app.docs.dto.ledger;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class CashLedgerOpenCloseBalDto {
	
	@Expose
	@SerializedName(value = "desc")
	private String description;
	
	@Expose
	private LedgerBalanceAmts igstbal;
	
	@Expose
	private LedgerBalanceAmts cgstbal;
	
	@Expose
	private LedgerBalanceAmts sgstbal;
	
	@Expose
	private LedgerBalanceAmts cessbal;
	
	
	@Expose
	@SerializedName("tot_rng_bal")
	private BigDecimal totRngBal = BigDecimal.ZERO;;
    
}
