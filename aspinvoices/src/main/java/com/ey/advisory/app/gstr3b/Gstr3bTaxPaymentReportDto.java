package com.ey.advisory.app.gstr3b;

import java.io.Serializable;
import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Gstr3bTaxPaymentReportDto implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Expose
	private String name;
	
	@Expose
	private BigDecimal taxPayable = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal igst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal cgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal sgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal cess = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal paidInCash = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal interest = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal lateFee = BigDecimal.ZERO;

}
