package com.ey.advisory.services.days180.api.push;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.springframework.stereotype.Service;

import com.ey.advisory.common.JaxbXmlFormatter;

import lombok.Data;

/**
 * 
 * @author vishal.verma
 *
 */

@XmlRootElement(name = "IM_DATA")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Service("PayloadMetaDataXMLDto")
public class PayloadMetaDataXMLDto implements JaxbXmlFormatter {

	@XmlElement(name = "HDR")
	private PayloadMetaDataHeaderXmlDto hdrDto;

	@XmlElement(name = "RESP")
	private PayloadMetaDataRespDto resp;
}
