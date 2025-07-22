package com.ey.advisory.app.docs.dto.erp;

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
public class Gstr6RevIntProcAndReviewSumDto {

	@XmlElement(name = "EntityName")
	private String entityName;

	@XmlElement(name = "RetPer")
	private String retPer;

	@XmlElement(name = "GstinNum")
	private String gstinNum;

	@XmlElement(name = "EntityPan")
	private String entityPan;

	@XmlElement(name = "CompanyCode")
	private String companyCode;

	@XmlElement(name = "ProcessSummary")
	private Gstr6RevIntProcSumDto getGstr6RevIntProcSumDto;

	@XmlElement(name = "ReviewSummary")
	private Gstr6RevIntRevSumDto gstr6RevIntRevSumDto;

	@XmlElement(name = "DistDoc")
	private Gstr6RevIntDistributionDocSumDto gstr6RevIntDistDocSumDto;

	@XmlElement(name = "RedistDoc")
	private Gstr6RevIntReDistributionDocSumDto gstr6RevIntReDistDocSumDto;

}
