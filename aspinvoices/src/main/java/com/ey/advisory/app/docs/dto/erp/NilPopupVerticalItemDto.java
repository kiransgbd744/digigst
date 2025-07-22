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
public class NilPopupVerticalItemDto {

	@XmlElement(name = "Hsn")
	private String hsn;

	@XmlElement(name = "Description")
	private String description;

	@XmlElement(name = "Uqc")
	private String uqc;

	@XmlElement(name = "Quantity")
	private String quantity;

	@XmlElement(name = "NiInterRegd")
	private BigDecimal niInterRegd;

	@XmlElement(name = "NiIntraRegd")
	private BigDecimal niIntraRegd;

	@XmlElement(name = "NiInterUnregd")
	private BigDecimal niInterUnregd;

	@XmlElement(name = "NiIntraUnregd")
	private BigDecimal niIntraUnregd;

	@XmlElement(name = "ExInterRegd")
	private BigDecimal exInterRegd;

	@XmlElement(name = "ExIntraRegd")
	private BigDecimal exIntraRegd;

	@XmlElement(name = "ExInterUnregd")
	private BigDecimal exInterUnregd;

	@XmlElement(name = "ExIntraUnregd")
	private BigDecimal exIntraUnregd;

	@XmlElement(name = "NoInterRegd")
	private BigDecimal noInterRegd;

	@XmlElement(name = "NoIntraRegd")
	private BigDecimal noIntraRegd;

	@XmlElement(name = "NoInterUnregd")
	private BigDecimal noInterUnregd;

	@XmlElement(name = "NoIntraUnregd")
	private BigDecimal noIntraUnregd;
}
