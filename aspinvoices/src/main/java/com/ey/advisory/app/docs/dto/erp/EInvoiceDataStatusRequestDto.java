package com.ey.advisory.app.docs.dto.erp;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.springframework.stereotype.Service;

import com.ey.advisory.common.JaxbXmlFormatter;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
@Service("EInvoiceDataStatusRequestDto")
public class EInvoiceDataStatusRequestDto implements JaxbXmlFormatter {

	@XmlElement(name = "ImDetails")
	private EInvoiceDataStatusRequestDataHeaderDto headerDto;

	@XmlElement(name = "ImSummary")
	private EInvoiceDataStatusRequestDataSummaryDto summaryDto;
}
