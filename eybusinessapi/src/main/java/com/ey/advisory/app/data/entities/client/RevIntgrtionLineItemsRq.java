package com.ey.advisory.app.data.entities.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class RevIntgrtionLineItemsRq {
	@XmlElement(name = "ItemNo")
	protected Integer itemNo;

	@XmlElement(name = "ErrorFields")
	protected String errorFields;

	@XmlElement(name = "ErrorCode")
	protected String errorCode;

	@XmlElement(name = "ErrorType")
	protected String errorType;

	@XmlElement(name = "ErrorDesc")
	protected String errorDesc;

}
