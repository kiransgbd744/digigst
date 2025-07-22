package com.ey.advisory.app.data.returns.compliance.service;

import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.springframework.stereotype.Service;

import com.ey.advisory.common.JaxbXmlFormatter;

import lombok.Data;

@XmlRootElement(name = "ImData")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Service("ComplainceHistoryRevIntFinalDto")
public class ComplainceHistoryRevIntFinalDto implements JaxbXmlFormatter {

	@XmlElement(name = "item")
	private List<ComplainceHistoryRevIntItemDto> items;
}
