package com.ey.advisory.app.docs.dto.erp;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.springframework.stereotype.Service;

import com.ey.advisory.common.JaxbXmlFormatter;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@XmlRootElement(name = "ImData")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
@ToString
@Service("Get2ARevIntgHeaderDto")
public class Get2ARevIntgHeaderDto implements JaxbXmlFormatter {

	@XmlElement(name = "item")
	private List<Get2ARevIntgItemDto> itemDtos;

}
