package com.ey.advisory.app.anx2.reconsummary;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Anx2ReconSummaryDto {
	
	@Expose
	private String particulars;
	
	@Expose
	private String suggestedResponse;
	
	@Expose
	private BigInteger anx2count = BigInteger.ZERO;
	
	@Expose
	private BigDecimal anx2per = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal anxtaxvalue = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal anxtotaltax = BigDecimal.ZERO;
	
	@Expose
	private BigInteger pr2count = BigInteger.ZERO;
	
	@Expose
	private BigDecimal pr2per = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal  prtaxvalue = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal prtotaltax = BigDecimal.ZERO;

	@Expose
	private BigDecimal totalAvailableTax = BigDecimal.ZERO; 
	
	@Expose
	private String category;
}
