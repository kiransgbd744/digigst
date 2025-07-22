package com.ey.advisory.app.docs.dto.erp;


import java.math.BigDecimal;

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
public class Gstr3bProcessSummaryItemDto {

	@XmlElement(name = "ProfitCentre1")
	private String profitCentre1;

	@XmlElement(name = "ProfitCentre2")
	private String profitCentre2;

	@XmlElement(name = "PlantCode")
	private String plantCode;

	@XmlElement(name = "Division")
	private String division;

	@XmlElement(name = "Location")
	private String location;

	@XmlElement(name = "SalesOrg")
	private String salesOrg;

	@XmlElement(name = "PurchOrg")
	private String purchOrg;

	@XmlElement(name = "DistChannel")
	private String distChannel;

	@XmlElement(name = "SaveStatus")
	private String saveStatus;
	
	@XmlElement(name = "TimeStamp")
	private String  timeStamp;
	
	@XmlElement(name = "TotalLiab")
	private BigDecimal totalLiab = BigDecimal.ZERO;
	
	@XmlElement(name = "TotalItc")
	private BigDecimal totalItc = BigDecimal.ZERO;
	

	@XmlElement(name = "Difference")
	private String difference;

	
}
