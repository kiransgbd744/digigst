package com.ey.advisory.app.docs.dto.ledger;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class ItcTransactionTypeBalDto {
	

	@Expose
	@SerializedName(value = "dt")
	private String itcTransDate;
	
	@Expose
	@SerializedName(value = "desc")
    private String description;
	
	@Expose
	@SerializedName(value = "ref_no")
	private String referenceNo;
	
	@Expose
	@SerializedName(value = "ret_period")
	private String retPeriod;
	
	@Expose
	@SerializedName(value = "sgstTaxAmt")
	private BigDecimal sgstTaxAmt = BigDecimal.ZERO;
	
	@Expose
	@SerializedName(value = "cgstTaxAmt")
	private BigDecimal  cgstTaxAmt = BigDecimal.ZERO;
	
	@Expose
	@SerializedName(value = "igstTaxAmt")
	private BigDecimal igstTaxAmt = BigDecimal.ZERO;
	
	@Expose
	@SerializedName(value = "cessTaxAmt")
	private BigDecimal cessTaxAmt = BigDecimal.ZERO;
	
	@Expose
	@SerializedName(value = "igstTaxBal")
	private BigDecimal igstTaxBal = BigDecimal.ZERO;
	
	@Expose
	@SerializedName(value = "cgstTaxBal")
	private BigDecimal cgstTaxBal = BigDecimal.ZERO;

	@Expose
	@SerializedName(value = "sgstTaxBal")
	private BigDecimal sgstTaxBal = BigDecimal.ZERO;

	@Expose
	@SerializedName(value = "cessTaxBal")
	private BigDecimal cessTaxBal = BigDecimal.ZERO;
	
	@Expose
	@SerializedName(value = "tr_typ")
	private String transType;
	
	@Expose
	@SerializedName(value = "tot_tr_amt")
	private BigDecimal totTrAmt = BigDecimal.ZERO;
	
	@Expose
	@SerializedName(value = "tot_rng_bal")
	private BigDecimal totRngBal = BigDecimal.ZERO;
	
}
