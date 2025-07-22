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
public class Gstr3bGstinLevelReportDto implements Serializable{
	
private static final long serialVersionUID = 1L;
	
	@Expose
	private String gstin;
	
	@Expose
	private String taxPeriod;
	
	@Expose
	private String tableSection;
	
	@Expose
	private String tableHeading;
	
	@Expose
	private String tableDesc;
	
	@Expose
	private BigDecimal totalTaxableVal = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal igst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal cgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal sgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal cess = BigDecimal.ZERO;

}
