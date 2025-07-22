package com.ey.advisory.app.docs.dto.erp;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@XmlRootElement(name = "DocPopup")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class DocPopupDto {

	@XmlElement(name = "Summary")
	private DocPopupSummaryDto docPopupSummaryDto;
	
	
	@XmlElement(name="Vertical")
	private DocPopupVerticalDto docPopupVerticalDto;
}
