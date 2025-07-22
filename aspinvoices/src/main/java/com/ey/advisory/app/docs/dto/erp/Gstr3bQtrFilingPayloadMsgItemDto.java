package com.ey.advisory.app.docs.dto.erp;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Gstr3bQtrFilingPayloadMsgItemDto {

	@XmlElement(name = "GSTIN")
	private String gstin;

	@XmlElement(name = "RETURNPERIOD")
	private String returnPeriod;

	@XmlElement(name = "QUARTER")
	private String quarter;

	@XmlElement(name = "ISFILED")
	private String isFiled;

	@XmlElement(name = "ERRORMSG")
	private String errorMsg;

}
