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
public class Gstr6RevIntProcSumItemDto {

	@XmlElement(name = "State")
	private String state;

	@XmlElement(name = "EyRegType")
	private String eyRegType;

	@XmlElement(name = "EyStatus")
	private String eyStatus;

	@XmlElement(name = "EyDate")
	private String eyDate;

	@XmlElement(name = "EyTime")
	private String eyTime;

	@XmlElement(name = "TaxTotDoc")
	private BigInteger taxTotDoc;

	@XmlElement(name = "TaxInvval")
	private BigDecimal taxInvval;

	@XmlElement(name = "TaxTaxval")
	private BigDecimal taxTaxval;

	@XmlElement(name = "TaxTotval")
	private BigDecimal taxTotval;

	@XmlElement(name = "TaxIgstval")
	private BigDecimal taxIgstval;

	@XmlElement(name = "TaxCgstval")
	private BigDecimal taxCgstval;

	@XmlElement(name = "TaxSgstval")
	private BigDecimal taxSgstval;

	@XmlElement(name = "TaxCessval")
	private BigDecimal taxCessval;

	@XmlElement(name = "TotCreditElg")
	private BigDecimal totCreditElg;

	@XmlElement(name = "CreditIgst")
	private BigDecimal creditIgst;

	@XmlElement(name = "CreditSgst")
	private BigDecimal creditSgst;

	@XmlElement(name = "CreditCgst")
	private BigDecimal creditCgst;

	@XmlElement(name = "CreditCess")
	private BigDecimal creditCess;

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
}
