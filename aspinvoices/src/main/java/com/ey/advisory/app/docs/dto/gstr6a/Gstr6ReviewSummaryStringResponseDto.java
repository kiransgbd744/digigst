package com.ey.advisory.app.docs.dto.gstr6a;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Gstr6ReviewSummaryStringResponseDto {

	private String gstin;
	private String docType;
	private String aspCount = BigInteger.ZERO.toString();
	private String aspInvValue = BigDecimal.ZERO.toString();
	private String aspTaxbValue = BigDecimal.ZERO.toString();
	private String aspTotTax = BigDecimal.ZERO.toString();
	private String aspIgst = BigDecimal.ZERO.toString();
	private String aspCgst = BigDecimal.ZERO.toString();
	private String aspSgst = BigDecimal.ZERO.toString();
	private String aspCess = BigDecimal.ZERO.toString();

	private String gstnCount = BigInteger.ZERO.toString();
	private String gstnInvValue = BigDecimal.ZERO.toString();
	private String gstnTaxbValue = BigDecimal.ZERO.toString();
	private String gstnTotTax = BigDecimal.ZERO.toString();
	private String gstnIgst = BigDecimal.ZERO.toString();
	private String gstnCgst = BigDecimal.ZERO.toString();
	private String gstnSgst = BigDecimal.ZERO.toString();
	private String gstnCess = BigDecimal.ZERO.toString();

	private String diffCount = BigInteger.ZERO.toString();
	private String diffInvValue = BigDecimal.ZERO.toString();
	private String diffTaxbValue = BigDecimal.ZERO.toString();
	private String diffTotTax = BigDecimal.ZERO.toString();
	private String diffIgst = BigDecimal.ZERO.toString();
	private String diffCgst = BigDecimal.ZERO.toString();
	private String diffSgst = BigDecimal.ZERO.toString();
	private String diffCess = BigDecimal.ZERO.toString();

	private List<Gstr6ReviewSummaryResponseStringItemDto> items;

}
