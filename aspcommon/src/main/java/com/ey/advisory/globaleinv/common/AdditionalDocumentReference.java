package com.ey.advisory.globaleinv.common;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AdditionalDocumentReference {
	@XmlElement(name = "ID")
	public String iD;
	@XmlElement(name = "DocumentType")
	public String documentType;
	@XmlElement(name = "Attachment")
	public Attachment attachment;
}
