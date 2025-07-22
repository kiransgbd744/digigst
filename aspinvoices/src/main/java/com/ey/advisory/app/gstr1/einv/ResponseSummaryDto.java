package com.ey.advisory.app.gstr1.einv;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ResponseSummaryDto {

	@Expose
	private String gstin;

	@Expose
	private String taxPeriod;
	
	@Expose
	private String particular;

	@Expose
	private BigInteger digiCount = BigInteger.ZERO;

	@Expose
	private BigDecimal digiTaxableVal = BigDecimal.ZERO;

	@Expose
	private BigDecimal digiTotalTax = BigDecimal.ZERO;

	@Expose
	private BigInteger saveCount = BigInteger.ZERO;

	@Expose
	private BigDecimal saveTaxableVal = BigDecimal.ZERO;

	@Expose
	private BigDecimal saveTotalTax = BigDecimal.ZERO;

	@Expose
	private BigInteger notSaveCount = BigInteger.ZERO;

	@Expose
	private BigDecimal notSaveTaxableVal = BigDecimal.ZERO;

	@Expose
	private BigDecimal notSaveTotalTax = BigDecimal.ZERO;

	@Expose
	private BigInteger delCount = BigInteger.ZERO;

	@Expose
	private BigDecimal delTaxableVal = BigDecimal.ZERO;

	@Expose
	private BigDecimal delTotalTax = BigDecimal.ZERO;

	@Expose
	private BigInteger savedCount = BigInteger.ZERO;

	@Expose
	private BigDecimal savedTaxableVal = BigDecimal.ZERO;

	@Expose
	private BigDecimal savedTotalTax = BigDecimal.ZERO;

	@Expose
	private BigInteger pendingCount = BigInteger.ZERO;

	@Expose
	private BigDecimal pendingTaxableVal = BigDecimal.ZERO;

	@Expose
	private BigDecimal pendingTotalTax = BigDecimal.ZERO;

	@Expose
	private String level = "L2";

	@Expose
	private String orderPosition;

}
