package com.ey.advisory.app.docs.dto.erp;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class PayloadErrorMesgItemDto {

	@XmlElement(name = "ItemNo")
	private String itemNo;
	
	@XmlElement(name="ErrorCode")
	private String errorCode;
	
	@XmlElement(name="ErrorDesc")
	private String errorDesc;
	
	@XmlElement(name="ErrorFields")
	private String errorFields;
	
	@XmlElement(name="ErrorType")
	private String errorType;
	
}
