package com.ey.advisory.app.docs.dto.gstr6a;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class Gstr6ReviewSummaryResponseItemDtoNew {

	public Gstr6ReviewSummaryResponseItemDtoNew(String gstin, String retPeriod,
			String docType, String eligInd, String tableDescription,
			BigInteger aspCount, BigDecimal aspInvValue,
			BigDecimal aspTaxbValue, BigDecimal aspTotTax, BigDecimal aspIgst,
			BigDecimal aspCgst, BigDecimal aspSgst, BigDecimal aspCess,
			BigInteger gstnCount, BigDecimal gstnInvValue,
			BigDecimal gstnTaxbValue, BigDecimal gstnTotTax,
			BigDecimal gstnIgst, BigDecimal gstnCgst, BigDecimal gstnSgst,
			BigDecimal gstnCess, BigInteger diffCount, BigDecimal diffInvValue,
			BigDecimal diffTaxbValue, BigDecimal diffTotTax,
			BigDecimal diffIgst, BigDecimal diffCgst, BigDecimal diffSgst,
			BigDecimal diffCess) {
		super();
		this.gstin = gstin;
		this.retPeriod = retPeriod;
		this.docType = docType;
		this.eligInd = eligInd;
		this.tableDescription = tableDescription;
		this.aspCount = aspCount;
		this.aspInvValue = aspInvValue;
		this.aspTaxbValue = aspTaxbValue;
		this.aspTotTax = aspTotTax;
		this.aspIgst = aspIgst;
		this.aspCgst = aspCgst;
		this.aspSgst = aspSgst;
		this.aspCess = aspCess;
		this.gstnCount = gstnCount;
		this.gstnInvValue = gstnInvValue;
		this.gstnTaxbValue = gstnTaxbValue;
		this.gstnTotTax = gstnTotTax;
		this.gstnIgst = gstnIgst;
		this.gstnCgst = gstnCgst;
		this.gstnSgst = gstnSgst;
		this.gstnCess = gstnCess;
		this.diffCount = diffCount;
		this.diffInvValue = diffInvValue;
		this.diffTaxbValue = diffTaxbValue;
		this.diffTotTax = diffTotTax;
		this.diffIgst = diffIgst;
		this.diffCgst = diffCgst;
		this.diffSgst = diffSgst;
		this.diffCess = diffCess;
	}

	private String gstin;
	private String retPeriod;
	private String docType;
	private String eligInd;
	private String tableDescription;
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
	private BigDecimal diffTotTax = BigDecimal.ZERO;
	private BigDecimal diffIgst = BigDecimal.ZERO;
	private BigDecimal diffCgst = BigDecimal.ZERO;
	private BigDecimal diffSgst = BigDecimal.ZERO;
	private BigDecimal diffCess = BigDecimal.ZERO;

}
