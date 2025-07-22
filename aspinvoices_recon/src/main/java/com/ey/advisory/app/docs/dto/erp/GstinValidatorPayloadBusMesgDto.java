package com.ey.advisory.app.docs.dto.erp;

import java.time.LocalDateTime;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@XmlRootElement(name = "BUSINESSMESSAGE")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
@ToString
public class GstinValidatorPayloadBusMesgDto {

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

	@XmlElement(name = "MODIFIEDON")
	private LocalDateTime modifiedOn;
	
	@XmlElement(name = "MESSAGEINFO")
	private String messageInfo;
	
	@XmlElement(name = "PUSHTYPE")
	private Integer pushType;

}
