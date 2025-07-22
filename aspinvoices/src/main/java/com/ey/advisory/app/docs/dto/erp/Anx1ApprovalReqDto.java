package com.ey.advisory.app.docs.dto.erp;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import com.ey.advisory.common.JaxbXmlFormatter;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Hemasundar.J
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
public class Anx1ApprovalReqDto implements JaxbXmlFormatter {

	@XmlElement(name = "ImHdata")
	private Anx1ApprovalRequestHeaderDto ImHeader;

	@XmlElement(name = "ImData")
	private Anx1ReviewSummaryRequestDto ImItem;
}
