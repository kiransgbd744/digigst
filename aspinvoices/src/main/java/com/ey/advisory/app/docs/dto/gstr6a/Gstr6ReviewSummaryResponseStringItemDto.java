package com.ey.advisory.app.docs.dto.gstr6a;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Gstr6ReviewSummaryResponseStringItemDto {

	private String retPeriod;
	private String gstin;
	private String docType;
	private String eligInd;
	private String aspCount;
	private String aspInvValue;
	private String aspTaxbValue;
	private String aspTotTax = BigDecimal.ZERO.toString();
	private String aspIgst = BigDecimal.ZERO.toString();
	private String aspCgst = BigDecimal.ZERO.toString();
	private String aspSgst = BigDecimal.ZERO.toString();
	private String aspCess = BigDecimal.ZERO.toString(); 
	
	private String gstnCount;
	private String gstnInvValue;
	private String gstnTaxbValue;
	private String gstnTotTax = BigDecimal.ZERO.toString();
	private String gstnIgst = BigDecimal.ZERO.toString();
	private String gstnCgst = BigDecimal.ZERO.toString();
	private String gstnSgst = BigDecimal.ZERO.toString();
	private String gstnCess = BigDecimal.ZERO.toString();
	
	private String diffCount;
	private String diffInvValue;
	private String diffTaxbValue;
	@JsonSerialize(using = BigDecimalNAorValueSerializer.class)
	private String diffTotTax = BigDecimal.ZERO.toString();
	@JsonSerialize(using = BigDecimalNAorValueSerializer.class)
	private String diffIgst = BigDecimal.ZERO.toString();
	@JsonSerialize(using = BigDecimalNAorValueSerializer.class)
	private String diffCgst = BigDecimal.ZERO.toString();
	@JsonSerialize(using = BigDecimalNAorValueSerializer.class)
	private String diffSgst = BigDecimal.ZERO.toString();
	@JsonSerialize(using = BigDecimalNAorValueSerializer.class)
	private String diffCess = BigDecimal.ZERO.toString();
	
}
