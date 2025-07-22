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
public class DiffPopupProcSumItemRevDto {
	
	@XmlElement(name = "EyDate")
	private String eyDate;

	@XmlElement(name = "EyTime")
	private String eyTime;

	@XmlElement(name = "Sec")
	private String section;

	@XmlElement(name = "Cont")
	private BigInteger dCount = BigInteger.ZERO;

	@XmlElement(name = "AssessableAmt")
	private BigDecimal assAmount = BigDecimal.ZERO;

	@XmlElement(name = "TotalTax")
	private BigDecimal totalTax = BigDecimal.ZERO;

	@XmlElement(name = "TotalValue")
	private BigDecimal totalValue = BigDecimal.ZERO;

	@XmlElement(name = "Igst")
	private BigDecimal igst = BigDecimal.ZERO;

	@XmlElement(name = "Cgst")
	private BigDecimal cgst = BigDecimal.ZERO;

	@XmlElement(name = "Sgst")
	private BigDecimal sgst = BigDecimal.ZERO;

	@XmlElement(name = "Cess")
	private BigDecimal cess = BigDecimal.ZERO;
	
	@XmlElement(name = "TaxableVal")
	private BigDecimal taxableVal = BigDecimal.ZERO;


}
