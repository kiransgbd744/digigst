package com.ey.advisory.app.data.returns.compliance.service;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@XmlRootElement(name = "Itc04Summ")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class ComplainceHistoryItc04SumDto {

	@XmlElement(name = "LastUpdDate")
	private String lastUpdatedDate;
	
	@XmlElement(name = "Gstn")
	private String gstn;

	@XmlElement(name = "State")
	private String state;

	@XmlElement(name = "Status")
	private String status;

	@XmlElement(name = "Q1AprJun")
	private String q1aprJun;

	@XmlElement(name = "Q2JulSep")
	private String q2julSep;

	@XmlElement(name = "Q3OctDec")
	private String q3octDec;

	@XmlElement(name = "Q4JanMar")
	private String q4janMar;

}
