package com.ey.advisory.app.docs.dto.erp;

import java.math.BigDecimal;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
public class Get2AConsoForSectionItemDto {

	@XmlElement(name = "ItemNumber")
	private String itemNumber;
	
	@XmlElement(name = "IgstAmt")
	private BigDecimal igstAmt;

	@XmlElement(name = "CgstAmt")
	private BigDecimal cgstAmt;

	@XmlElement(name = "CessAmt")
	private BigDecimal cessAmt;

	@XmlElement(name = "SgstAmt")
	private BigDecimal sgstAmt;

	@XmlElement(name = "TaxableValue")
	private BigDecimal taxableValue;

	@XmlElement(name = "TaxRate")
	private BigDecimal taxRate;
}
