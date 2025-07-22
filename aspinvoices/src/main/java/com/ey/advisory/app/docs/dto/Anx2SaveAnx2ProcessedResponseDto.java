package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;
import java.math.BigInteger;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Anx2SaveAnx2ProcessedResponseDto {

	private String gstin;
	private String state;
	private String authToken;
	private String saveStatus;
	private LocalDateTime timeStamp;
	private String typeCount;
	private String typeTaxable;
	private String typeTotalTax;

	private BigInteger aspCountAccept = BigInteger.ZERO;
	private BigInteger aspCountPending = BigInteger.ZERO;
	private BigInteger aspCountReject = BigInteger.ZERO;
	private BigInteger aspCountnoAction = BigInteger.ZERO;
	private BigInteger aspCountisdCredit = BigInteger.ZERO;

	private BigInteger gstnCountAccept = BigInteger.ZERO;
	private BigInteger gstnCountpending = BigInteger.ZERO;
	private BigInteger gstnCountreject = BigInteger.ZERO;
	private BigInteger gstnCountnoAction = BigInteger.ZERO;
	private BigInteger gstnCountisdCredit = BigInteger.ZERO;

	private BigDecimal aspTaxableAccept = BigDecimal.ZERO;
	private BigDecimal aspTaxablePending = BigDecimal.ZERO;
	private BigDecimal aspTaxableReject = BigDecimal.ZERO;
	private BigDecimal aspTaxablenoAction = BigDecimal.ZERO;
	private BigDecimal aspTaxableisdCredit = BigDecimal.ZERO;

	private BigDecimal gstnTaxableAccept = BigDecimal.ZERO;
	private BigDecimal gstnTaxablepending = BigDecimal.ZERO;
	private BigDecimal gstnTaxablereject = BigDecimal.ZERO;
	private BigDecimal gstnTaxablenoAction = BigDecimal.ZERO;
	private BigInteger gstnTaxableisdCredit = BigInteger.ZERO;

	private BigDecimal aspTotalTaxAccept = BigDecimal.ZERO;
	private BigDecimal aspTotalTaxPending = BigDecimal.ZERO;
	private BigDecimal aspTotalTaxReject = BigDecimal.ZERO;
	private BigDecimal aspTotalTaxnoAction = BigDecimal.ZERO;
	private BigDecimal aspTotalTaxisdCredit = BigDecimal.ZERO;

	private BigDecimal gstnTotalTaxAccept = BigDecimal.ZERO;
	private BigDecimal gstnTotalTaxpending = BigDecimal.ZERO;
	private BigDecimal gstnTotalTaxreject = BigDecimal.ZERO;
	private BigDecimal gstnTotalTaxnoAction = BigDecimal.ZERO;
	private BigInteger gstnTotalTaxisdCredit = BigInteger.ZERO;

}
