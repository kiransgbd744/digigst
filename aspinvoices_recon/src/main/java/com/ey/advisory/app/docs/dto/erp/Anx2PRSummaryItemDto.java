package com.ey.advisory.app.docs.dto.erp;

import java.math.BigDecimal;
import java.math.BigInteger
;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;


import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
public class Anx2PRSummaryItemDto {

	@XmlElement(name = "MANDT")
	private String mandt;

	@XmlElement(name = "ANX2_AR_ID")
	private String anx2ArId;

	@XmlElement(name = "COMAPNY_CODE")
	private String companyCode;

	@XmlElement(name = "ENTITY")
	private String entity;

	@XmlElement(name = "RET_PER")
	private String retPer;

	@XmlElement(name = "GSTIN_NUM")
	private String gstinNum;

	@XmlElement(name = "ENTITY_NAME")
	private String entityName;

	@XmlElement(name = "DIVISION")
	private String division;

	@XmlElement(name = "PROFIT_CENTER")
	private String profitCenter;

	@XmlElement(name = "STO_LOCATION")
	private String stoLocation;

	@XmlElement(name = "PLANT_CODE")
	private String plantCode;

	@XmlElement(name = "PURCHASE_ORG")
	private String purchaseOrg;

	@XmlElement(name = "USERACCESS1")
	private String userAccess1;

	@XmlElement(name = "USERACCESS2")
	private String userAccess2;

	@XmlElement(name = "USERACCESS3")
	private String userAccess3;

	@XmlElement(name = "USERACCESS4")
	private String userAccess4;

	@XmlElement(name = "USERACCESS5")
	private String userAccess5;

	@XmlElement(name = "USERACCESS6")
	private String userAccess6;

	@XmlElement(name = "STATE")
	private String state;

	@XmlElement(name = "LAST_UPDATE")
	private String lastUpdate;

	@XmlElement(name = "T_COUNT")
	private BigInteger tCount = BigInteger.ZERO;

	@XmlElement(name = "INV_VALUE")
	private BigDecimal invValue = BigDecimal.ZERO;

	@XmlElement(name = "TAXABLE_VALUE")
	private BigDecimal taxableValue = BigDecimal.ZERO;

	@XmlElement(name = "TOTAL_TAX")
	private BigDecimal totalTax = BigDecimal.ZERO;

	@XmlElement(name = "TAX_IGST")
	private BigDecimal taxIgst = BigDecimal.ZERO;

	@XmlElement(name = "TAX_CGST")
	private BigDecimal taxCgst = BigDecimal.ZERO;

	@XmlElement(name = "TAX_SGST")
	private BigDecimal taxSgst = BigDecimal.ZERO;

	@XmlElement(name = "TAX_CESS")
	private BigDecimal taxCess = BigDecimal.ZERO;

	@XmlElement(name = "TOTAL_CREDIT_E")
	private BigDecimal totalCreditE = BigDecimal.ZERO;

	@XmlElement(name = "CREDIT_E_IGST")
	private BigDecimal creditEIgst = BigDecimal.ZERO;

	@XmlElement(name = "CREDIT_E_CGST")
	private BigDecimal creditECgst = BigDecimal.ZERO;

	@XmlElement(name = "CREDIT_E_SGST")
	private BigDecimal creditESgst = BigDecimal.ZERO;

	@XmlElement(name = "CREDIT_E_CESS")
	private BigDecimal creditECess = BigDecimal.ZERO;

	@XmlElement(name = "TABLE_NAME")
	private String tableName;

	@XmlElement(name = "TABLE_TYPE")
	private String tableType;
	

}
