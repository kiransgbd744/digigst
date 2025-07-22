package com.ey.advisory.app.docs.dto.erp;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@XmlRootElement(name = "B2csaPopup")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class B2csaPopupDto {

	@XmlElement(name = "Summary")
	private B2csaPopupSummaryDto sumDto;

	@XmlElement(name = "GstnView")
	private B2csPopupGstnViewDto b2csPopupGstnViewDto;

	@XmlElement(name = "Vertical")
	private ReviwSumB2bAPopupVerticalDto vericalDto;
}
