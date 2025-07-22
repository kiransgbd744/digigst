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
public class NilItemDto {

	@XmlElement(name = "NiNonGstSup")
	private String niNonGstSup;

	@XmlElement(name = "Cont")
	private BigInteger cont;

	@XmlElement(name = "NilAmt")
	private BigDecimal nilAmt;

	@XmlElement(name = "ExemAmt")
	private BigDecimal exemAmt;

	@XmlElement(name = "NonGstAmt")
	private BigDecimal nonGstAmt;
	
}
