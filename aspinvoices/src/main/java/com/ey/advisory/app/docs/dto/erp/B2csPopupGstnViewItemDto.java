package com.ey.advisory.app.docs.dto.erp;

import java.math.BigDecimal;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class B2csPopupGstnViewItemDto {

	@XmlElement(name = "BillingPos")
	private String billingPos;

	@XmlElement(name = "TranType")
	private String tranType;

	@XmlElement(name = "Rate")
	private BigDecimal rate;

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
