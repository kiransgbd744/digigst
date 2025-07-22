package com.ey.advisory.app.inward.einvoice;

import com.ey.advisory.common.JaxbXmlFormatter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
public class InwardEInvoiceErpPushDto implements JaxbXmlFormatter{

	@XmlElement(name = "HEADER")
	public Header header;

	@XmlElement(name = "ITEM")
	public InwardEinvoiceRevIntItemDto item;

	@XmlElement(name = "PRECEEDING")
	public InwardEinvoiceRevIntPrecedingDocDto preceeding;
	
	@XmlElement(name = "ATTRIBUTES")
	public InwardEinvoiceRevIntAttributeDocDto attribute;
	
	@XmlElement(name = "CONTRACT")
	public InwardEinvoiceRevIntContractDocDto contract;
	
	@XmlElement(name = "ADDITIONAL")
	public InwardEinvoiceRevIntAdditionalDocDto additional;
}
