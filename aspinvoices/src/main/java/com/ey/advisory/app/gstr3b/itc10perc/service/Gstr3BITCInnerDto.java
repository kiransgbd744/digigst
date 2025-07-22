package com.ey.advisory.app.gstr3b.itc10perc.service;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */
@Data
public class Gstr3BITCInnerDto {

	@Expose
	private String sectionName;
	
	@Expose
	private String subSectionName;
	
	@Expose
	private String rowSection;
	
	@Expose
	private BigDecimal igst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal cgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal sgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal cess = BigDecimal.ZERO;
	
	@Expose
	private Boolean flag;
}
