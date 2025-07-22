package com.ey.advisory.app.gstr3b.itc10perc.service;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class Gstr3BItc10PercDto {
	

	@Expose
	private String sectionName;
	
	@Expose
	private String subSectionName;
	
	@Expose
	private BigDecimal taxableValAsp = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal igstAsp = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal cgstAsp = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal sgstAsp = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal cessAsp = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal taxableValUser = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal igstUser = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal cgstUser = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal sgstUser = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal cessUser = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal taxableValAutoCal = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal igstAutoCal = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal cgstAutoCal = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal sgstAutoCal = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal cessAutoCal = BigDecimal.ZERO;
	
	@Expose
	private String rowName;
	
	@Expose
	private Boolean radioFlag = false;
	
	@Expose
	private String level = "L1";
	
	
	/**
	 * @param sectionName
	 * @param subSectionName
	 */
	public Gstr3BItc10PercDto(String sectionName, String subSectionName) {
		super();
		this.sectionName = sectionName;
		this.subSectionName = subSectionName;
	}
	
	

}
