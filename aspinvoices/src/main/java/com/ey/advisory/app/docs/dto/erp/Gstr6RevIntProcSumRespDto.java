package com.ey.advisory.app.docs.dto.erp;

import java.math.BigDecimal;
import java.math.BigInteger;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
@ToString
public class Gstr6RevIntProcSumRespDto {

	private String gstin;
	private String state;
	private String retPeriod;
	private String eyStatus;
	private String eyRegType;
	private String eyDate;
	private String eyTime;
	private BigInteger taxTotDoc;
	private BigDecimal taxInvval;
	private BigDecimal taxTaxval;
	private BigDecimal taxTotval;
	private BigDecimal taxIgstval;
	private BigDecimal taxCgstval;
	private BigDecimal taxSgstval;
	private BigDecimal taxCessval;
	private BigDecimal totCreditEligable;
	private BigDecimal creditIgst;
	private BigDecimal creditSgst;
	private BigDecimal creditCgst;
	private BigDecimal creditCess;
	private String purchOrg;
	private String location;
	private String division;
	private String proctrCenter1;
	private String proctrCenter2;
}
