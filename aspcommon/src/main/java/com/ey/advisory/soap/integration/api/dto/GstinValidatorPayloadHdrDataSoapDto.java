package com.ey.advisory.soap.integration.api.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import lombok.Getter;
import lombok.Setter;



@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class GstinValidatorPayloadHdrDataSoapDto {
	@XmlTransient
	protected Long id;
	/* this is just added for soap request for vendor */

	@XmlElement(name = "id-token")
	protected String idToken;

	@XmlElement(name = "payload-id")
	private String payloadId;

	@XmlElement(name = "push-type")
	private int pushType;
	
	@XmlElement(name = "company-cd")
	protected String companyCode;
	
	@XmlElementWrapper(name = "lineItems")
	@XmlElement(name = "item")
	protected List<GstinValidatorPayloadLineDataSoapDto> lineItems = new ArrayList<>();
}
