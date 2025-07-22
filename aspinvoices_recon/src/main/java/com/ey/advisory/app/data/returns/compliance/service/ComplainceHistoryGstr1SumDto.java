package com.ey.advisory.app.data.returns.compliance.service;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class ComplainceHistoryGstr1SumDto {

	
	@XmlElement(name = "LastUpdDate")
	private String lastUpdatedDate;
	
	@XmlElement(name = "Gstn")
	private String gstn;

	@XmlElement(name = "State")
	private String state;

	@XmlElement(name = "Status")
	private String status;

	@XmlElement(name = "April")
	private String april;
	
	@XmlElement(name = "May")
	private String may;

	
	@XmlElement(name = "June")
	private String june;

	@XmlElement(name = "July")
	private String july;

	@XmlElement(name = "August")
	private String august;

	@XmlElement(name = "September")
	private String september;

	@XmlElement(name = "October")
	private String october;

	@XmlElement(name = "November")
	private String november;

	@XmlElement(name = "December")
	private String december;

	@XmlElement(name = "January")
	private String january;

	@XmlElement(name = "February")
	private String february;

	@XmlElement(name = "March")
	private String march;

}
