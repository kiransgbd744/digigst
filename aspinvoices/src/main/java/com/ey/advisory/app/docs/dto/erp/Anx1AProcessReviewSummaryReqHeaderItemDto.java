package com.ey.advisory.app.docs.dto.erp;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import com.ey.advisory.common.JaxbXmlFormatter;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "ImData")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
public class Anx1AProcessReviewSummaryReqHeaderItemDto
		implements JaxbXmlFormatter {

	@XmlElement(name = "item")
	private List<Anx1AHeaderItemProcessReviewSummaryReqDto> dtos;

}
