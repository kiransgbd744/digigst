package com.ey.advisory.app.data.returns.compliance.service;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@XmlRootElement(name = "Gstr9Summ")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class ComplainceHistoryGstr9SumDto {

	@XmlElement(name = "LastUpdDate")
	private String lastUpdatedDate;
	
	@XmlElement(name = "Gstn")
	private String gstn;

	@XmlElement(name = "State")
	private String state;

	@XmlElement(name = "Status")
	private String status;

	@XmlElement(name = "AckNo")
	private String ackNo;

	@XmlElement(name = "FileStatus")
	private String fileStatus;

	@XmlElement(name = "Ddate")
	private String ddate;

	@XmlElement(name = "Ttime")
	private String ttime;
}
