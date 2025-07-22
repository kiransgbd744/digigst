package com.ey.advisory.app.docs.dto.gstr6a;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Gstr6ReviewSummaryResponseDto {

	private String gstin;
	private String docType;
	private BigInteger aspCount = BigInteger.ZERO;
	private BigDecimal aspInvValue = BigDecimal.ZERO;
	private BigDecimal aspTaxbValue = BigDecimal.ZERO;
	private BigDecimal aspTotTax = BigDecimal.ZERO;
	private BigDecimal aspIgst = BigDecimal.ZERO;
	private BigDecimal aspCgst = BigDecimal.ZERO;
	private BigDecimal aspSgst = BigDecimal.ZERO;
	private BigDecimal aspCess = BigDecimal.ZERO;

	private BigInteger gstnCount = BigInteger.ZERO;
	private BigDecimal gstnInvValue = BigDecimal.ZERO;
	private BigDecimal gstnTaxbValue = BigDecimal.ZERO;
	private BigDecimal gstnTotTax = BigDecimal.ZERO;
	private BigDecimal gstnIgst = BigDecimal.ZERO;
	private BigDecimal gstnCgst = BigDecimal.ZERO;
	private BigDecimal gstnSgst = BigDecimal.ZERO;
	private BigDecimal gstnCess = BigDecimal.ZERO;

	private BigInteger diffCount = BigInteger.ZERO;
	private BigDecimal diffInvValue = BigDecimal.ZERO;
	private BigDecimal diffTaxbValue = BigDecimal.ZERO;
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
	
//	private Object diffTotTax = BigDecimal.ZERO; 
//    private Object diffIgst = BigDecimal.ZERO; 
//    private Object diffCgst = BigDecimal.ZERO; 
//    private Object diffSgst = BigDecimal.ZERO; 
//    private Object diffCess = BigDecimal.ZERO; 

	private List<Gstr6ReviewSummaryResponseItemDto> items;

}
