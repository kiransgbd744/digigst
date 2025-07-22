package com.ey.advisory.app.docs.dto.erp;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.springframework.stereotype.Service;

import com.ey.advisory.common.JaxbXmlFormatter;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
@Service("Anx2PRSummaryDto")
public class Anx2PRSummaryDto implements JaxbXmlFormatter {

	@XmlElement(name = "IT_DATA_T1")
	private Anx2PRSummaryHeaderDto headerDto;

	@XmlElement(name = "IT_DATA_T2")
	private Anx2PRSummaryDetailDto detailDto;
}
