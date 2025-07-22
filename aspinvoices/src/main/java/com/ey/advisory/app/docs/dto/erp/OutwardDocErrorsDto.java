package com.ey.advisory.app.docs.dto.erp;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement(name = "Errors")
@XmlAccessorType(XmlAccessType.FIELD)
public class OutwardDocErrorsDto {

	@XmlElement(name = "item")
	private List<OutwardErrorItemDto> errors;
}
