
package com.ey.advisory.app.docs.dto.erp;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.springframework.stereotype.Service;

import com.ey.advisory.common.JaxbXmlFormatter;

import lombok.Data;

@XmlRootElement(name = "IM_DATA")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Service("Get2BRevIntgAllClientsHeaderDto")
public class Get2BRevIntgAllClientsHeaderDto implements JaxbXmlFormatter {

	@XmlElement(name = "item")
	private List<Get2BRevIntgInvDto> itemDtos;
}
