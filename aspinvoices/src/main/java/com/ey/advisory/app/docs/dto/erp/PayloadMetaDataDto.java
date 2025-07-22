package com.ey.advisory.app.docs.dto.erp;

import org.springframework.stereotype.Service;

import com.ey.advisory.common.JaxbXmlFormatter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "ImData")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
@Service("PayloadMetaDataDto")
public class PayloadMetaDataDto implements JaxbXmlFormatter {

	@XmlElement(name = "BusinessMessage")
	private PayloadBusMesgDto dto;

	@XmlElement(name = "ErrorInfo")
	private PayloadErrorInfoMesgDto errorMsgDto;
}
