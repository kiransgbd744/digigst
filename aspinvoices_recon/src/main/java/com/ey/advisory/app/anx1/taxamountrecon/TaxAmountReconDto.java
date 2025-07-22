package com.ey.advisory.app.anx1.taxamountrecon;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.Data;


/**
 * @author Arun KA
 *
 **/

@Data
public class TaxAmountReconDto {

	@Expose
	private String gstin;

	@Expose
	private BigDecimal updIgstAmt = BigDecimal.ZERO;

	@Expose
	private BigDecimal updCgstAmt = BigDecimal.ZERO;

	@Expose
	private BigDecimal updSgstAmt = BigDecimal.ZERO;

	@Expose
	private BigDecimal memoIgstAmt = BigDecimal.ZERO;

	@Expose
	private BigDecimal memoCgstAmt = BigDecimal.ZERO;

	@Expose
	private BigDecimal memoSgstAmt = BigDecimal.ZERO;

	@Expose
	private BigDecimal retIgstAmt = BigDecimal.ZERO;

	@Expose
	private BigDecimal retCgstAmt = BigDecimal.ZERO;

	@Expose
	private BigDecimal retSgstAmt = BigDecimal.ZERO;
	
	@Expose
	private String stateName;
	
	@Expose
	private String authTokenStatus;

	public TaxAmountReconDto() {}
	
	public TaxAmountReconDto(String gstin) {
		this.gstin = gstin;
	}
	
	
	

	
}
