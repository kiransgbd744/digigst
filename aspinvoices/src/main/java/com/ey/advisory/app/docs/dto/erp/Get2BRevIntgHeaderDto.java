package com.ey.advisory.app.docs.dto.erp;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.springframework.stereotype.Service;

import com.ey.advisory.common.JaxbXmlFormatter;

import lombok.Data;

/**
 * 
 * @author Sruthi.P
 *
 */

@XmlRootElement(name = "ITMDTLS")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Service("Get2BRevIntgHeaderDto")
public class Get2BRevIntgHeaderDto implements JaxbXmlFormatter {

	@XmlElement(name = "item")
	private List<Get2BRevIntgInvDto> itemDtos;
}
