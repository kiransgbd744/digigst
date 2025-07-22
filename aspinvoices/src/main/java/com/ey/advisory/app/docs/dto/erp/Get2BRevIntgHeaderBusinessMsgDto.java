package com.ey.advisory.app.docs.dto.erp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ey.advisory.common.JaxbXmlFormatter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;


@XmlRootElement(name = "IM_DATA")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Service("Get2BRevIntgHeaderBusinessMsgDto")
public class Get2BRevIntgHeaderBusinessMsgDto implements JaxbXmlFormatter {

	@XmlElement(name = "BUSINESSMESSAGE")
	private Get2BRevIntgHeaderChunkDtlsDto bussinessMsg;
	
	
	@XmlElementWrapper(name = "ITMDTLS")
    @XmlElement(name = "item")
    protected List<Get2BRevIntgInvDto> itmDtls = new ArrayList<>();
	 
}
