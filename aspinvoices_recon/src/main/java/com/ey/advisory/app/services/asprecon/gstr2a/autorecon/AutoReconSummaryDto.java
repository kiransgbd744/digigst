package com.ey.advisory.app.services.asprecon.gstr2a.autorecon;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AutoReconSummaryDto {

	@Expose
	private String reportCategory;

	@Expose
	private String reportType;

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
	
	@Expose
	private String level = "L2";
	
	@Expose
	private String orderPosition;
	
	@Expose
	private String lockStatus;
}
