/**
 * 
 */
package com.ey.advisory.app.docs.dto.erp;

import java.math.BigDecimal;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Data;

/**
 * 
 * @author Sasidhar Reddy
 *
 *2:57:40 PM_com.ey.advisory.app.docs.dto.erp
 */

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class AnxDataStatusRequestItemDto {

	@XmlElement(name = "ENTITY")
	private String entity;
	
	@XmlElement(name = "COMPANY_CODE")
	private String companyCode;

	@XmlElement(name = "RET_PER")
	private String retPeriod;

	@XmlElement(name = "GSTIN_NUM")
	private String gstin;
	
	@XmlElement(name = "DATA_TYPE")
	private String datatype;

	@XmlElement(name = "ENTITY_NAME")
	private String entityName;

	@XmlElement(name = "DIVISION")
	private String division;

	@XmlElement(name = "PROFIT_CENTER")
	private String profitCentre;

	@XmlElement(name = "STO_LOCATION")
	private String location;

	@XmlElement(name = "PLANT_CODE")
	private String plantcode;

	@XmlElement(name = "SALES_ORG")
	private String salesOrg;

	@XmlElement(name = "PURCHASE_ORG")
	private String purchaseOrg;

	@XmlElement(name = "DIST_CHANNEL")
	private String distributionChannel;

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

	@XmlElement(name = "PUSHED_DT")
	private String date;

	@XmlElement(name = "PUSH_ASP")
	private Integer aspTotal;

	@XmlElement(name = "PROCESS_ACT_ASP")
	private Integer activeProcess;

	@XmlElement(name = "PROCESS_INA_ASP")
	private Integer inactiveProcess;

	@XmlElement(name = "ERROR_ACT_ASP")
	private Integer activeError;

	@XmlElement(name = "ERROR_INA_ASP")
	private Integer inactiveError;

	@XmlElement(name = "INFOR_ACT_ASP")
	private Integer activeInfo;

	@XmlElement(name = "INFOR_INA_ASP")
	private Integer inactiveInfo;

	// Added missing fields from parent request for data api summary
	
	@XmlElement(name = "RET_TYPE")
	private String retType;

	@XmlElement(name = "RET_SECTION")
	private String retSection;

	@XmlElement(name = "T_COUNT")
	private Integer tCount;

	@XmlElement(name = "TAXABLE_VALUE")
	private BigDecimal taxableValue;

	@XmlElement(name = "TOTAL_TAX")
	private BigDecimal totalTax;

	@XmlElement(name = "IGST")
	private BigDecimal igst;

	@XmlElement(name = "CGST")
	private BigDecimal cgst;

	@XmlElement(name = "SGST")
	private BigDecimal sgst;

	@XmlElement(name = "CESS")
	private BigDecimal cess;

	@XmlElement(name = "DOC_TYPE")
	private String docType;

}
