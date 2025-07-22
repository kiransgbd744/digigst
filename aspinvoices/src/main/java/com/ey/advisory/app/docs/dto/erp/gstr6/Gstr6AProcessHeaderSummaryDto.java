package com.ey.advisory.app.docs.dto.erp.gstr6;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "ProcessSummary")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
public class Gstr6AProcessHeaderSummaryDto {

	@XmlElement(name = "item")
	private List<Gstr6AProcessItemSummaryDto> items;
}
