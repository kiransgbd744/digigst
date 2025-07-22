package com.ey.advisory.app.docs.dto.erp;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@XmlRootElement(name = "NilPopup")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class NilPopupDto {

	@XmlElement(name = "Nil")
	private NilDto nilDto;

	@XmlElement(name = "Compouted")
	private NilCompoutedDto nilComputedDto;

	private NilPopupVerticalDto nilPopupVertDto;
	@XmlElement(name = "Vertical")
	private NilPopupVerticalDto verticleDto;

}
