package com.ey.advisory.app.docs.dto.erp;

import org.springframework.stereotype.Service;

import com.ey.advisory.common.JaxbXmlFormatter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
@Service("Gstr1ApproveRequestFinalDto")
public class Gstr1ApproveRequestFinalDto implements JaxbXmlFormatter {

	@XmlElement(name = "ImHdata")
	private Gstr1ApprovalRequestHeaderDto headerDto;

	@XmlElement(name = "ImItem")
	private Gstr1ApprovalRequestDto approveRequestDto;
}
