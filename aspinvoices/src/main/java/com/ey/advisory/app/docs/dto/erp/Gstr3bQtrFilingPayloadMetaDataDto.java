package com.ey.advisory.app.docs.dto.erp;

import org.springframework.stereotype.Service;

import com.ey.advisory.common.JaxbXmlFormatter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "IMDATA")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
@Service("Gstr3bQtrFilingPayloadMetaDataDto")
public class Gstr3bQtrFilingPayloadMetaDataDto implements JaxbXmlFormatter {

	@XmlElement(name = "BUSINESSMESSAGE")
	private Gstr3bQtrFilingPayloadBusMesgDto dto;

	@XmlElement(name = "ROOT")
	private Gstr3bQtrFilingDetailsPayloadMsgGstinDetailsDto gstinDetailDto;
}
