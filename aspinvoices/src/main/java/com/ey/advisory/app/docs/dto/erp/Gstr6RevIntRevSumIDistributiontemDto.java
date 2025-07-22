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
public class Gstr6RevIntRevSumIDistributiontemDto {

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

	@XmlElement(name = "AIgstAsIgst")
	private BigDecimal aIgstAsIgst;

	@XmlElement(name = "AIgstAsSgst")
	private BigDecimal aIgstAsSgst;

	@XmlElement(name = "AIgstAsCgst")
	private BigDecimal aIgstAsCgst;

	@XmlElement(name = "ASgstAsSgst")
	private BigDecimal aSgstAsSgst;

	@XmlElement(name = "ASgstAsIgst")
	private BigDecimal aSgstAsIgst;

	@XmlElement(name = "ACgstAsCgst")
	private BigDecimal aCgstAsCgst;

	@XmlElement(name = "ACgstAsIgst")
	private BigDecimal aCgstAsIgst;

	@XmlElement(name = "ACessAmt")
	private BigDecimal aCessAmt;

	@XmlElement(name = "GCount")
	private BigInteger gCount;

	@XmlElement(name = "GIgstAsIgst")
	private BigDecimal gIgstAsIgst;

	@XmlElement(name = "GIgstAsSgst")
	private BigDecimal gIgstAsSgst;

	@XmlElement(name = "GIgstAsCgst")
	private BigDecimal gIgstAsCgst;

	@XmlElement(name = "GSgstAsSgst")
	private BigDecimal gSgstAsSgst;

	@XmlElement(name = "GSgstAsIgst")
	private BigDecimal gSgstAsIgst;

	@XmlElement(name = "GCgstAsCgst")
	private BigDecimal gCgstAsCgst;

	@XmlElement(name = "GCgstAsIgst")
	private BigDecimal gCgstAsIgst;

	@XmlElement(name = "GCessAmt")
	private BigDecimal gCessAmt;

	@XmlElement(name = "DCount")
	private BigInteger dCount;

	@XmlElement(name = "DIgstAsIgst")
	private BigDecimal dIgstAsIgst;

	@XmlElement(name = "DIgstAsSgst")
	private BigDecimal dIgstAsSgst;

	@XmlElement(name = "DIgstAsCgst")
	private BigDecimal dIgstAsCgst;

	@XmlElement(name = "DSgstAsSgst")
	private BigDecimal dSgstAsSgst;

	@XmlElement(name = "DSgstAsIgst")
	private BigDecimal dSgstAsIgst;

	@XmlElement(name = "DCgstAsCgst")
	private BigDecimal dCgstAsCgst;

	@XmlElement(name = "DCgstAsIgst")
	private BigDecimal dCgstAsIgst;

	@XmlElement(name = "DCessAmt")
	private BigDecimal dCessAmt;
}
