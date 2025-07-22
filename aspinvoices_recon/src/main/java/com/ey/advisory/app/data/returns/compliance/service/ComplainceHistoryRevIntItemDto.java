package com.ey.advisory.app.data.returns.compliance.service;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class ComplainceHistoryRevIntItemDto {

	@XmlElement(name = "EntityPan")
	private String entityPan;

	@XmlElement(name = "EntityName")
	private String entityName;

	@XmlElement(name = "Gstn")
	private String gstn;

	@XmlElement(name = "Status")
	private String status;

	@XmlElement(name = "FiYear")
	private String fiYear;

	@XmlElement(name = "Gstr1Summ")
	private ComplainceHistoryGstr1SumDto gstr1SumDto;

	@XmlElement(name = "Gstr3bSumm")
	private ComplainceHistoryGstr1SumDto gstr3bSumm;

	@XmlElement(name = "Gstr6Summ")
	private ComplainceHistoryGstr1SumDto gstr6Summ;

	@XmlElement(name = "Gstr7Summ")
	private ComplainceHistoryGstr1SumDto gstr7Summ;

	@XmlElement(name = "Gstr9Summ")
	private ComplainceHistoryGstr9SumDto gstr9SumDto;

	@XmlElement(name = "Itc04Summ")
	private ComplainceHistoryItc04SumDto itc04SumDto;

}
