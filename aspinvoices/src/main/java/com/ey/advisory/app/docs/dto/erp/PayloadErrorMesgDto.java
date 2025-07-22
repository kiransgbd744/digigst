package com.ey.advisory.app.docs.dto.erp;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@XmlRootElement(name = "Errors")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class PayloadErrorMesgDto {

	@XmlElement(name = "item")
	private List<PayloadErrorMesgItemDto> items;
}
