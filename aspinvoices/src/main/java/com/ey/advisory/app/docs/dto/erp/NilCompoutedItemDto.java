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
public class NilCompoutedItemDto {

	@XmlElement(name = "Description")
	private String description;

	@XmlElement(name = "AspNiRSup")
	private BigDecimal aspNiRSup;

	@XmlElement(name = "AspExemSup")
	private BigDecimal aspExemSup;

	@XmlElement(name = "AspNoGstSup")
	private BigDecimal aspNoGstSup;

	@XmlElement(name = "EditNiRSup")
	private BigDecimal editNiRSup;

	@XmlElement(name = "EditExemSup")
	private BigDecimal editExemSup;

	@XmlElement(name = "EditNoGstSup")
	private BigDecimal editNoGstSup;
}
