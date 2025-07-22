package com.ey.advisory.app.docs.dto.erp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ey.advisory.common.JaxbXmlFormatter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@XmlRootElement(name = "ImData")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Service("BCAPIOutwardPayloadErrorIFinalDto")
public class BCAPIOutwardPayloadErrorIFinalDto implements JaxbXmlFormatter {

	@XmlElement(name = "item")
	private List<BCAPIOutwardPayloadErrorItemDto> itemDtos = new ArrayList<>();

}
