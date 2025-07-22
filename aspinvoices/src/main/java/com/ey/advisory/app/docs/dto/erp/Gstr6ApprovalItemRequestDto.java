package com.ey.advisory.app.docs.dto.erp;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
public class Gstr6ApprovalItemRequestDto {

	@XmlElement(name = "Id")
	private Long id;
	
	@XmlElement(name = "ReqstDate")
	private String reqstDate;

	@XmlElement(name = "ReqstTime")
	private String reqstTime;

	@XmlElement(name = "Entity")
	private String entity;

	@XmlElement(name = "Bukrs")
	private String companyCode;
	
	@XmlElement(name = "GstinNum")
	private String gstinNum;
	
	@XmlElement(name = "RetPer")
	private String retPer;

	@XmlElement(name = "AppRejUser")
	private String appRejUser;

	@XmlElement(name = "UdFlag")
	private String udFlag;

	@XmlElement(name = "AppRejDate")
	private String appRejDate;

	@XmlElement(name = "AppRejTime")
	private String appRejTime;

}
