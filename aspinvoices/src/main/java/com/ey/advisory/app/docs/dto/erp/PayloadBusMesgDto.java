package com.ey.advisory.app.docs.dto.erp;

import java.time.LocalDateTime;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@XmlRootElement(name = "BusinessMessage")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
@ToString
public class PayloadBusMesgDto {

	@XmlElement(name = "PayloadId")
	private String payloadId;

	@XmlElement(name = "Status")
	private String status;

	@XmlElement(name = "ProcessCount")
	private Integer processCount;

	@XmlElement(name = "ErrorCount")
	private Integer errorCount;

	@XmlElement(name = "TotalCount")
	private Integer totalCount;

	@XmlElement(name = "ModifiedOn")
	private LocalDateTime modifiedOn;
	
	@XmlElement(name = "MessageInfo")
	private String messageInfo;
	
	@XmlElement(name = "PushType")
	private Integer pushType;

}
