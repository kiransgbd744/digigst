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
public class TransactionTypeLibBalDto {
	
	@Expose
	@SerializedName(value = "dt")
	private String dptDate;
	
	@Expose
	@SerializedName(value = "desc")
    private String description;
	
	@Expose
	@SerializedName(value = "tr_typ")
	private String transType;
	
	@Expose
	@SerializedName(value = "dschrg_typ")
	private String dischargingType;
	
	@Expose
	@SerializedName(value = "tot_tr_amt")
	private BigDecimal totTrAmt = BigDecimal.ZERO;;
	
	@Expose
	@SerializedName(value = "tot_rng_bal")
	private BigDecimal totRngBal = BigDecimal.ZERO;;
	
	@Expose
	@SerializedName(value = "ref_no")
	private String referenceNo;
	
	@Expose
	@SerializedName(value = "igst")
	private LedgerBalanceAmts igst;
	
	@Expose
	@SerializedName(value = "sgst")
	private LedgerBalanceAmts sgst;
	
	@Expose
	@SerializedName(value = "cgst")
	private LedgerBalanceAmts cgst;
	
	@Expose
	@SerializedName(value = "cess")
	private LedgerBalanceAmts cess;
	
	@Expose
	@SerializedName(value = "igstbal")
	private LedgerBalanceAmts igstBal;
	
	@Expose
	@SerializedName(value = "sgstbal")
	private LedgerBalanceAmts sgstBal;

	@Expose
	@SerializedName(value = "cgstbal")
	private LedgerBalanceAmts cgstBal;

	@Expose
	@SerializedName(value = "cessbal")
	private LedgerBalanceAmts cessBal;
	
}
