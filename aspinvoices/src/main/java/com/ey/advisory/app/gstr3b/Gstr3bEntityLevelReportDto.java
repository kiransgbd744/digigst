package com.ey.advisory.app.gstr3b;

import java.io.Serializable;
import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Arun KA
 *
 */
@Getter
@Setter
public class Gstr3bEntityLevelReportDto implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Expose
	private String gstin;
	
	@Expose
	private String taxPeriod;
	
	@Expose
	private String derivedTaxPeriod;
	
	@Expose
	private String tableSection;
	
	@Expose
	private String tableHeading;
	
	@Expose
	private String tableDesc;
	
	@Expose
	private BigDecimal computeTaxableVal = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal computeIgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal computeCgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal computeSgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal computeCess = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal userTaxableVal = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal userIgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal userCgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal userSgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal userCess = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal gstnTaxableVal = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal gstnIgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal gstnCgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal gstnSgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal gstnCess = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal diffTaxableVal = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal diffIgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal diffCgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal diffSgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal diffCess = BigDecimal.ZERO;

}
