package com.ey.advisory.app.docs.dto.erp;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
@ToString
public class Anx1AHeaderItemProcessReviewSummaryReqDto {

	@XmlElement(name="EntityName")
	private String entityName ;
	
	@XmlElement(name="RetPer")
	private String retPer;
	
	@XmlElement(name="GstinNum")
	private String gstinNum;
	
	@XmlElement(name = "EntityPan")
	private String entityPan;
	
	@XmlElement(name = "CompanyCode")
	private String companyCode;
	
	@XmlElement(name = "ProcessSummary")
	private Anx1AHeaderProcessSummaryReqDto headerProcessSummDto;

	@XmlElement(name = "ReviewSummary")
	private Anx1AHeaderReviewSummaryReqDto headerReviewSummDto;

}
