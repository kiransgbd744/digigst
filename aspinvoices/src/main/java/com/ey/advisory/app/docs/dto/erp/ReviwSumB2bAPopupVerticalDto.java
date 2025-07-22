package com.ey.advisory.app.docs.dto.erp;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@XmlRootElement(name = "Vertical")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class ReviwSumB2bAPopupVerticalDto {
	@XmlElement(name = "item")
	private List<ReviwB2banPopupVertItemDto> item;
}
