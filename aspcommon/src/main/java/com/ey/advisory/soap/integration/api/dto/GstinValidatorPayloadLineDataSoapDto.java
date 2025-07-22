package com.ey.advisory.soap.integration.api.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import lombok.Getter;
import lombok.Setter;



@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class GstinValidatorPayloadLineDataSoapDto {
	@XmlTransient
	protected Long id;
	/* this is just added for soap request for vendor */

	@XmlElement(name = "customer-code")
	private String customerCode;
	@XmlElement(name = "gstin")
	private String gstin;
}
