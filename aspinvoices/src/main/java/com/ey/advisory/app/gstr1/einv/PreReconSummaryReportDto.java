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
public class PreReconSummaryReportDto {
	
	@Expose
	private String gstin;
	
	@Expose
	private String section;
	
	@Expose
	private BigInteger einvCount = BigInteger.ZERO;

	@Expose
	private BigDecimal einvTaxableVal = BigDecimal.ZERO;

	@Expose
	private BigDecimal einvIgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal einvCgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal einvSgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal einvCess = BigDecimal.ZERO;

	@Expose
	private BigInteger srCount = BigInteger.ZERO;

	@Expose
	private BigDecimal srTaxableVal = BigDecimal.ZERO;

	@Expose
	private BigDecimal srIgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal srCgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal srSgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal srCess = BigDecimal.ZERO;

}
