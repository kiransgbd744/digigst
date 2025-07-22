package com.ey.advisory.app.vendor.service;

import java.time.LocalDateTime;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * 
 * @author vishal.verma
 *
 */


@XmlRootElement(name = "BUSINESSMESSAGE")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
@ToString
public class VendorValidationBusinessMsgDto {

	@XmlElement(name = "PAYLOADID")
	private String payloadId = "";

	@XmlElement(name = "STATUS")
	private String status = "";

	@XmlElement(name = "PROCESSCOUNT")
	private Integer processCount = 0;

	@XmlElement(name = "ERRORCOUNT")
	private Integer errorCount;

	@XmlElement(name = "TOTALCOUNT")
	private Integer totalCount;

	@XmlElement(name = "ModifiedOn")
	private LocalDateTime modifiedOn;
	
	@XmlElement(name = "MESSAGEINFO")
	private String messageInfo;
	
	@XmlElement(name = "COMPANYCODE")
	private String companyCode = "";

}
