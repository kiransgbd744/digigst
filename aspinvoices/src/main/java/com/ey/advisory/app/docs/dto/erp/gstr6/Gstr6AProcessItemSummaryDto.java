package com.ey.advisory.app.docs.dto.erp.gstr6;

import java.math.BigDecimal;
import java.math.BigInteger;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
public class Gstr6AProcessItemSummaryDto {

	@XmlElement(name = "TableType")
	private String tableType;

	@XmlElement(name = "DocumentType")
	private String documentType;

	@XmlElement(name = "GetStatus")
	private String getStatus;

	@XmlElement(name = "DocCount")
	private BigInteger docCount;

	@XmlElement(name = "TaxableValue")
	private BigDecimal taxableValue;

	@XmlElement(name = "TotalTax")
	private BigDecimal totalTax;

	@XmlElement(name = "Igst")
	private BigDecimal igst;

	@XmlElement(name = "Sgst")
	private BigDecimal sgst;

	@XmlElement(name = "Cgst")
	private BigDecimal cgst;

	@XmlElement(name = "Cess")
	private BigDecimal cess;

	@XmlElement(name = "InvoiceValue")
	private BigDecimal invoiceValue;
}
