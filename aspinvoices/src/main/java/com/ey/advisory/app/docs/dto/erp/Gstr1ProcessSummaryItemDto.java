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
public class Gstr1ProcessSummaryItemDto {

	@XmlElement(name = "State")
	private String state;

	@XmlElement(name = "EyStatus")
	private String eyStatus;
	
	@XmlElement(name = "EyDate")
	private String eyDate;

	@XmlElement(name = "EyTime")
	private String eyTime;
	
	@XmlElement(name = "EyTotDoc")
	private BigInteger eyTotDoc = BigInteger.ZERO;

	@XmlElement(name = "EyOutsupp")
	private BigDecimal eyOutsupp = BigDecimal.ZERO;

	@XmlElement(name = "EyIgstval")
	private BigDecimal eyIgstval = BigDecimal.ZERO;

	@XmlElement(name = "EyCgstval")
	private BigDecimal eyCgstval = BigDecimal.ZERO;

	@XmlElement(name = "EySgstval")
	private BigDecimal eySgstval = BigDecimal.ZERO;

	@XmlElement(name = "EyCessval")
	private BigDecimal eyCessval = BigDecimal.ZERO;
	
	@XmlElement(name = "RtnPerdStatus")
	private String rtnPerdStatus;

	@XmlElement(name = "Prctr")
	private String profitCenter;

	@XmlElement(name = "DocType")
	private String docType;

	@XmlElement(name = "Werks")
	private String plantCode;

	@XmlElement(name = "Vkorg")
	private String salesOrganization;

	@XmlElement(name = "DistChannel")
	private String distChannel;

	@XmlElement(name = "Division")
	private String division;

	@XmlElement(name = "Useraccess1")
	private String useraccess1;

	@XmlElement(name = "Useraccess2")
	private String useraccess2;

	@XmlElement(name = "Useraccess3")
	private String useraccess3;
	@XmlElement(name = "Useraccess4")
	private String useraccess4;

	@XmlElement(name = "Useraccess5")
	private String useraccess5;

	@XmlElement(name = "Useraccess6")
	private String useraccess6;

	@XmlElement(name = "Location")
	private String location;
}
