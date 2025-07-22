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
public class Gstr9TaxPaidDashboardDto {

	@Expose
	private String subSection;
	
	@Expose
	private String subSectionName;
	@Expose
	private BigDecimal autoCalTaxableVal = BigDecimal.ZERO;

	@Expose
	private BigDecimal autoCalPaidThrCash = BigDecimal.ZERO;

	@Expose
	private BigDecimal autoCalPaidThrItcIgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal autoCalPaidThrItcCgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal autoCalPaidThrItcSgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal autoCalPaidThrItcCess = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal computedTaxableVal = BigDecimal.ZERO;

	@Expose
	private BigDecimal computedPaidThrCash = BigDecimal.ZERO;

	@Expose
	private BigDecimal computedPaidThrItcIgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal computedPaidThrItcCgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal computedPaidThrItcSgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal computedPaidThrItcCess = BigDecimal.ZERO;

	@Expose
	private BigDecimal userInputTaxableVal = BigDecimal.ZERO;

	@Expose
	private BigDecimal gstinTaxableVal = BigDecimal.ZERO;

	@Expose
	private BigDecimal gstinPaidThrCash = BigDecimal.ZERO;

	@Expose
	private BigDecimal gstinIgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal gstinCgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal gstinSgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal gstinCess = BigDecimal.ZERO;

	public Gstr9TaxPaidDashboardDto(String subSection, String subSectionName) {
		super();
		this.subSection = subSection;
		this.subSectionName = subSectionName;
	}
}
