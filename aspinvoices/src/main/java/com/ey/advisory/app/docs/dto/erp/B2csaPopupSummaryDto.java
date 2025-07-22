package com.ey.advisory.app.docs.dto.erp;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@XmlRootElement(name = "Summary")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class B2csaPopupSummaryDto {

	@XmlElement(name = "item")
	List<B2csaPopupSummaryItemDto> sumItemDtos;
}
