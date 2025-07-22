package com.ey.advisory.app.docs.dto.erp;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.springframework.stereotype.Service;

import com.ey.advisory.common.JaxbXmlFormatter;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "IM_DATA")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
@Service("VendorMasterApiPayloadMetaDataDto")
public class VendorMasterApiPayloadMetaDataDto implements JaxbXmlFormatter {

	@XmlElement(name = "BUSINESS_MESSAGE")
	private VendorMasterApiPayloadBusMesgDto dto;

	@XmlElement(name = "VENDOR")
	private VendorMasterApiDetailsPayloadMsgGstinDetailsDto vendorDetailDto;
}
