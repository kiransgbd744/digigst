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
public class GstinLevelInnerDto {
	
	@Expose
	public String docType;
	
	@Expose
	public String supplyType;
	
	@Expose
	public Integer count = 0;
	
	@Expose
	public BigDecimal taxableVal = BigDecimal.ZERO;
	
	@Expose
	public BigDecimal igst = BigDecimal.ZERO;
	
	@Expose
	public BigDecimal cgst = BigDecimal.ZERO;
	
	@Expose
	public BigDecimal sgst = BigDecimal.ZERO;
	
	@Expose
	public BigDecimal cess = BigDecimal.ZERO;
	
	@Expose
	public BigDecimal totalTax = BigDecimal.ZERO;
	
	@Expose
	public BigDecimal totInvVal = BigDecimal.ZERO;
	
	public String order;
	
}
