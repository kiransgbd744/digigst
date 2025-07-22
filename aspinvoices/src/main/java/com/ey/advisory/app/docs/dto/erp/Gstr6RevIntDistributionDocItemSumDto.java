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
public class Gstr6RevIntDistributionDocItemSumDto {

	@XmlElement(name = "EligInd")
	private String eligInd;

	@XmlElement(name = "PurchOrg")
	private String purchOrg;

	@XmlElement(name = "Location")
	private String location;

	@XmlElement(name = "Division")
	private String division;

	@XmlElement(name = "ProctrCenter1")
	private String proctrCenter1;

	@XmlElement(name = "ProctrCenter2")
	private String proctrCenter2;

	@XmlElement(name = "Isdgstin")
	private String isdgstin;

	@XmlElement(name = "RecpGstin")
	private String recpGstin;

	@XmlElement(name = "Statecode")
	private String statecode;

	@XmlElement(name = "Doctype")
	private String documentType;

	@XmlElement(name = "Supplytype")
	private String supplytype;

	@XmlElement(name = "DocNumber")
	private String docNumber;

	@XmlElement(name = "DocDate")
	private String docDate;

	@XmlElement(name = "OrgDocNumber")
	private String orgDocNumber;

	@XmlElement(name = "OrgDocdate")
	private String orgDocdate;

	@XmlElement(name = "IgstAsIgst")
	private BigDecimal igstAsIgst;

	@XmlElement(name = "IgstAsSgst")
	private BigDecimal igstAsSgst;

	@XmlElement(name = "IgstAsCgst")
	private BigDecimal igstAsCgst;

	@XmlElement(name = "SgstAsSgst")
	private BigDecimal sgstAsSgst;

	@XmlElement(name = "SgstAsIgst")
	private BigDecimal sgstAsIgst;

	@XmlElement(name = "CgstAsCgst")
	private BigDecimal cgstAsCgst;

	@XmlElement(name = "CgstAsIgst")
	private BigDecimal cgstAsIgst;

	@XmlElement(name = "CessAmount")
	private BigDecimal cessAmount;
}
