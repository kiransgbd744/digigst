package com.ey.advisory.app.docs.dto.erp;

import java.math.BigDecimal;
import java.math.BigInteger;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class B2csPopupSummaryItemDto {

	@XmlElement(name = "B2cs")
	private String b2cs;

	@XmlElement(name = "Cont")
	private BigInteger cont;

	@XmlElement(name = "InvoiceVal")
	private BigDecimal invoiceVal;

	@XmlElement(name = "AssessableAmt")
	private BigDecimal assessableAmt;

	@XmlElement(name = "Igst")
	private BigDecimal igst;

	@XmlElement(name = "Cgst")
	private BigDecimal cgst;

	@XmlElement(name = "Sgst")
	private BigDecimal sgst;

	@XmlElement(name = "Cess")
	private BigDecimal cess;
}
