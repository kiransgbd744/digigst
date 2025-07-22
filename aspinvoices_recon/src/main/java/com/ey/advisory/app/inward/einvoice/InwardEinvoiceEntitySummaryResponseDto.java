package com.ey.advisory.app.inward.einvoice;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * 
 * @author vishal.verma
 *
 */

@Data
public class InwardEinvoiceEntitySummaryResponseDto {

	@Expose
	public String gstin;
	
	@Expose
	public String state;
	
	@Expose
	public String regType;
	
	@Expose
	public String authToken;
	
	@Expose
	public String status;
	
	@Expose
	public String getCallTimestamp;
	
	@Expose
	public Integer countSuppGstn = 0;
	
	@Expose
	public Integer countEinv = 0;
	
	@Expose
	public Integer activeEinv = 0;
	
	@Expose
	public Integer canclEinv = 0;
	
	@Expose
	public BigDecimal totlInvAmt = BigDecimal.ZERO;

}
