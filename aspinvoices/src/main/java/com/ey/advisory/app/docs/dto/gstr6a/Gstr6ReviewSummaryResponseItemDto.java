package com.ey.advisory.app.docs.dto.gstr6a;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Gstr6ReviewSummaryResponseItemDto {

	private String retPeriod;
	private String gstin;
	private String docType;
	private String eligInd;
	private BigInteger aspCount;
	private BigDecimal aspInvValue;
	private BigDecimal aspTaxbValue;
	private BigDecimal aspTotTax = BigDecimal.ZERO;
	private BigDecimal aspIgst = BigDecimal.ZERO;
	private BigDecimal aspCgst = BigDecimal.ZERO;
	private BigDecimal aspSgst = BigDecimal.ZERO;
	private BigDecimal aspCess = BigDecimal.ZERO; 
	
	private BigInteger gstnCount;
	private BigDecimal gstnInvValue;
	private BigDecimal gstnTaxbValue;
	private BigDecimal gstnTotTax = BigDecimal.ZERO;
	private BigDecimal gstnIgst = BigDecimal.ZERO;
	private BigDecimal gstnCgst = BigDecimal.ZERO;
	private BigDecimal gstnSgst = BigDecimal.ZERO;
	private BigDecimal gstnCess = BigDecimal.ZERO;
	
	private BigInteger diffCount;
	private BigDecimal diffInvValue;
	private BigDecimal diffTaxbValue;
	@JsonSerialize(using = BigDecimalNAorValueSerializer.class)
	private BigDecimal diffTotTax = BigDecimal.ZERO;
	@JsonSerialize(using = BigDecimalNAorValueSerializer.class)
	private BigDecimal diffIgst = BigDecimal.ZERO;
	@JsonSerialize(using = BigDecimalNAorValueSerializer.class)
	private BigDecimal diffCgst = BigDecimal.ZERO;
	@JsonSerialize(using = BigDecimalNAorValueSerializer.class)
	private BigDecimal diffSgst = BigDecimal.ZERO;
	@JsonSerialize(using = BigDecimalNAorValueSerializer.class)
	private BigDecimal diffCess = BigDecimal.ZERO;
	
//	    private Object diffTotTax = BigDecimal.ZERO; 
//	    private Object diffIgst = BigDecimal.ZERO; 
//	    private Object diffCgst = BigDecimal.ZERO; 
//	    private Object diffSgst = BigDecimal.ZERO; 
//	    private Object diffCess = BigDecimal.ZERO; 
	
}
