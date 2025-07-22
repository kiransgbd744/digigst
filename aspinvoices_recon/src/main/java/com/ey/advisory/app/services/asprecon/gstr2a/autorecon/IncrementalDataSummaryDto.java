package com.ey.advisory.app.services.asprecon.gstr2a.autorecon;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IncrementalDataSummaryDto {

	@Expose
	private String gstin;
	
	@Expose
	private String reportCategory;

	@Expose
	private Long prCount = 0L;

	@Expose
	private BigDecimal prTotalTax = BigDecimal.ZERO;

	@Expose
	private BigDecimal prTotalTaxPer = BigDecimal.ZERO;

	@Expose
	private BigDecimal prIgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal prCgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal prSgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal prCess = BigDecimal.ZERO;

	@Expose
	private Long a2Count = 0L;

	@Expose
	private BigDecimal a2TotalTax = BigDecimal.ZERO;

	@Expose
	private BigDecimal a2TotalTaxPer = BigDecimal.ZERO;

	@Expose
	private BigDecimal a2Igst = BigDecimal.ZERO;

	@Expose
	private BigDecimal a2Cgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal a2Sgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal a2Cess = BigDecimal.ZERO;

	private String order;
}
