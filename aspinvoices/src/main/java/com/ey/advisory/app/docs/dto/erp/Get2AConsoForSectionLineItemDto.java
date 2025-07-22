package com.ey.advisory.app.docs.dto.erp;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement(name = "LineItem")
@XmlAccessorType(XmlAccessType.FIELD)
public class Get2AConsoForSectionLineItemDto {

	@XmlElement(name = "item")
	private List<Get2AConsoForSectionItemDto> items;
}
