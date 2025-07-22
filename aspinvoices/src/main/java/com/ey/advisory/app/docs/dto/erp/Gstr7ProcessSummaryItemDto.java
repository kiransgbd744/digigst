package com.ey.advisory.app.docs.dto.erp;


import java.math.BigDecimal;
import java.math.BigInteger;

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
public class Gstr7ProcessSummaryItemDto {

	@XmlElement(name = "SaveStatus")
	private String saveStatus;
	@XmlElement(name = "TimeStamp")
	private String  timeStamp;
	@XmlElement(name = "Cont")
	private BigInteger cont = BigInteger.ZERO;
	@XmlElement(name = "TotalAmt")
	private BigDecimal totalAmt = BigDecimal.ZERO;
	@XmlElement(name = "Igst")
	private BigDecimal igst = BigDecimal.ZERO;
	@XmlElement(name = "Cgst")
	private BigDecimal cgst = BigDecimal.ZERO;
	@XmlElement(name = "Sgst")
	private BigDecimal sgst = BigDecimal.ZERO;
	@XmlElement(name = "Diff")
	private BigDecimal diff = BigDecimal.ZERO;
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
	private String  distChannel;
	
}
