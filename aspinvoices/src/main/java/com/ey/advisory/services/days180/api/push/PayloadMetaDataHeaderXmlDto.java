package com.ey.advisory.services.days180.api.push;

import java.time.LocalDateTime;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Data;

/**
 * 
 * @author vishal.verma
 *
 */

@XmlRootElement(name = "HDR")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class PayloadMetaDataHeaderXmlDto {

	@XmlElement(name = "PAYLOADID")
	private String payloadId;

	@XmlElement(name = "STATUS")
	private String status;

	@XmlElement(name = "PROCESSCOUNT")
	private Integer processCount;

	@XmlElement(name = "ERRORCOUNT")
	private Integer errorCount;

	@XmlElement(name = "TOTALCOUNT")
	private Integer totalCount;

	@XmlElement(name = "COMPANYCODE")
	private String companyCode;
	
	@XmlElement(name = "ModifiedOn")
	private LocalDateTime modifiedOn;
	
	@XmlElement(name = "MessageInfo")
	private String messageInfo;
	
	@XmlElement(name = "PUSHTYPE")
	private Integer pushType;

}
