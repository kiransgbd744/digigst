package com.ey.advisory.app.docs.dto.gstr9;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Saif.S
 *
 */
@Getter
@Setter
public class Gstr9PyTransInCyDashboardDto {

	@Expose
	private String section;

	@Expose
	private String subSectionName;

	// @Expose
	// private BigDecimal autoCalTaxableVal = BigDecimal.ZERO;
	//
	// @Expose
	// private BigDecimal autoCalIgst = BigDecimal.ZERO;
	//
	// @Expose
	// private BigDecimal autoCalCgst = BigDecimal.ZERO;
	//
	// @Expose
	// private BigDecimal autoCalSgst = BigDecimal.ZERO;
	//
	// @Expose
	// private BigDecimal autoCalCess = BigDecimal.ZERO;

	@Expose
	private BigDecimal computedTaxableVal = BigDecimal.ZERO;

	@Expose
	private BigDecimal computedIgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal computedCgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal computedSgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal computedCess = BigDecimal.ZERO;

	@Expose
	private BigDecimal userInputTaxableVal = BigDecimal.ZERO;

	@Expose
	private BigDecimal userInputIgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal userInputCgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal userInputSgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal userInputCess = BigDecimal.ZERO;

	@Expose
	private BigDecimal gstinTaxableVal = BigDecimal.ZERO;

	@Expose
	private BigDecimal gstinIgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal gstinCgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal gstinSgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal gstinCess = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal digiComputeTaxableVal = BigDecimal.ZERO;

	@Expose
	private BigDecimal digiComputeIgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal digiComputeCgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal digiComputeSgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal digiComputeCess = BigDecimal.ZERO;

	public Gstr9PyTransInCyDashboardDto(String section, String subSectionName) {
		super();
		this.section = section;
		this.subSectionName = subSectionName;
	}

}
