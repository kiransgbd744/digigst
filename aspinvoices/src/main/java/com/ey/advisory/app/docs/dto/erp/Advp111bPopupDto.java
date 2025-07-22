package com.ey.advisory.app.docs.dto.erp;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@XmlRootElement(name = "Advp111aPopup")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Advp111bPopupDto {

	@XmlElement(name = "Summary")
	private AdvncePopupSummaryDto sumDto;

	@XmlElement(name = "GstnView")
	private AdvancePopupGstnViewDto advancePopupGstnViewDto;

	@XmlElement(name = "Vertical")
	private AdvancePopupVerticalDto vericalDto;
}
