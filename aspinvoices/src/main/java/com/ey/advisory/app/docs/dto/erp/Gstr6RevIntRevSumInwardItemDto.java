package com.ey.advisory.app.docs.dto.erp;

import java.math.BigDecimal;
import java.math.BigInteger;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
@ToString
public class Gstr6RevIntRevSumInwardItemDto {

	@XmlElement(name = "PurchOrg")
	private String purchOrg;

	@XmlElement(name = "Location")
	private String location;

	@XmlElement(name = "Division")
	private String division;

	@XmlElement(name = "ProctrCenter1")
	private String proctrCenter1;

	@XmlElement(name = "ProctrCenter2")
	private String proctrCenter2;

	@XmlElement(name = "DocType")
	private String docType;

	@XmlElement(name = "TableType")
	private String tableType;

	@XmlElement(name = "EligInd")
	private String eligInd;

	@XmlElement(name = "ACount")
	private BigInteger aCount;

	@XmlElement(name = "AInvVal")
	private BigDecimal aInvVal;

	@XmlElement(name = "ATaxVal")
	private BigDecimal aTaxVal;

	@XmlElement(name = "ATotalTax")
	private BigDecimal aTotalTax;

	@XmlElement(name = "AIgst")
	private BigDecimal aIgst;

	@XmlElement(name = "ACgst")
	private BigDecimal aCgst;

	@XmlElement(name = "ASgst")
	private BigDecimal aSgst;

	@XmlElement(name = "ACess")
	private BigDecimal aCess;

	@XmlElement(name = "GCount")
	private BigInteger gCount;

	@XmlElement(name = "GInvVal")
	private BigDecimal gInvVal;

	@XmlElement(name = "GTaxVal")
	private BigDecimal gTaxVal;

	@XmlElement(name = "GTotalTax")
	private BigDecimal gTotalTax;

	@XmlElement(name = "GIgst")
	private BigDecimal gIgst;

	@XmlElement(name = "GCgst")
	private BigDecimal gCgst;

	@XmlElement(name = "GSgst")
	private BigDecimal gSgst;

	@XmlElement(name = "GCess")
	private BigDecimal gCess;

	@XmlElement(name = "DCount")
	private BigInteger dCount;

	@XmlElement(name = "DInvVal")
	private BigDecimal dInvVal;

	@XmlElement(name = "DTaxVal")
	private BigDecimal dTaxVal;

	@XmlElement(name = "DTotalTax")
	private BigDecimal dTotalTax;

	@XmlElement(name = "DIgst")
	private BigDecimal dIgst;

	@XmlElement(name = "DCgst")
	private BigDecimal dCgst;

	@XmlElement(name = "DSgst")
	private BigDecimal dSgst;

	@XmlElement(name = "DCess")
	private BigDecimal dCess;

}
