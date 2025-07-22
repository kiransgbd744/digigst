package com.ey.advisory.app.vendor.service;

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
@Service("VendorValidationMetaDataDto")
public class VendorValidationMetaDataDto implements JaxbXmlFormatter {

	@XmlElement(name = "BUSINESSMESSAGE")
	private VendorValidationBusinessMsgDto msgDto;

	@XmlElement(name = "VENDORDETAILS")
	private VendorValidatorResponseListDto responseDto;
}
