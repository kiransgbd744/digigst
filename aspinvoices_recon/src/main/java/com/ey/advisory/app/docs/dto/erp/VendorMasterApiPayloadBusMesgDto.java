package com.ey.advisory.app.docs.dto.erp;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@XmlRootElement(name = "BUSINESS_MESSAGE")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
@ToString
public class VendorMasterApiPayloadBusMesgDto {

	@XmlElement(name = "PAYLOADID")
	private String payloadId;

	@XmlElement(name = "MESSAGEINFO")
	private String messageInfo;

}
