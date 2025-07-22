package com.ey.advisory.globaleinv.common;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class LegalMonetaryTotal {
	@XmlElement(name = "LineExtensionAmount")
	public Double lineExtensionAmount;

	@XmlElement(name = "TaxExclusiveAmount")
	public Double taxExclusiveAmount;

	@XmlElement(name = "TaxInclusiveAmount")
	public Double taxInclusiveAmount;

	@XmlElement(name = "AllowanceTotalAmount")
	public Integer allowanceTotalAmount;

	@XmlElement(name = "ChargeTotalAmount")
	public Integer chargeTotalAmount;

	@XmlElement(name = "PrepaidAmount")
	public Integer prepaidAmount;

	@XmlElement(name = "PayableRoundingAmount")
	public Double payableRoundingAmount;

	@XmlElement(name = "PayableAmount")
	public Integer payableAmount;
}
