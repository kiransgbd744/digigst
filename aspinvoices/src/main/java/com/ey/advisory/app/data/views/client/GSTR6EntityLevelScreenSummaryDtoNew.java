package com.ey.advisory.app.data.views.client;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GSTR6EntityLevelScreenSummaryDtoNew {

	private String gstin;
	private String docType;
	private String tableDescription;
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
	private BigDecimal diffTotTax = BigDecimal.ZERO;
	private BigDecimal diffIgst = BigDecimal.ZERO;
	private BigDecimal diffCgst = BigDecimal.ZERO;
	private BigDecimal diffSgst = BigDecimal.ZERO;
	private BigDecimal diffCess = BigDecimal.ZERO;
}
